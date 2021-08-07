package com.tomtruyen.soteria.android.models

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tomtruyen.soteria.android.models.token.Token
import org.apache.commons.codec.binary.Base32
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

class DatabaseService(private val context: Context) :
    SQLiteOpenHelper(context, getPath(context) + DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "SoteriaDB"
        private const val TABLE_TOKENS = "TokenTable"
        private const val TABLE_GOOGLE_DRIVE = "GoogleDriveTable"
        private const val TABLE_SETTINGS = "Settings"
        private const val KEY_ID = "id"
        private const val KEY_TOKEN = "token"
        private const val KEY_DRIVE_FILE_ID = "file_id"
        private const val KEY_SETTING_PIN = "pin"
        private const val DELIMITER = "==="

        fun getPath(context: Context): String {
            return context.getExternalFilesDirs(null).last().absolutePath
        }
    }

    private val createTokenTable =
        ("CREATE TABLE $TABLE_TOKENS($KEY_ID TEXT PRIMARY KEY,$KEY_TOKEN TEXT)")
    private val createGoogleDriveTable =
        ("CREATE TABLE $TABLE_GOOGLE_DRIVE($KEY_ID INTEGER PRIMARY KEY,$KEY_DRIVE_FILE_ID TEXT)")
    private val createSettingsTable =
        ("CREATE TABLE $TABLE_SETTINGS($KEY_ID INTEGER PRIMARY KEY,$KEY_SETTING_PIN TEXT)")

    private var gson: Gson = Gson()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTokenTable)
        db?.execSQL(createGoogleDriveTable)
        db?.execSQL(createSettingsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL(createSettingsTable)
        }
    }

    fun isPinEnabled(): Boolean {
        return readPin() != null
    }

    fun readPin(): String? {
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT $KEY_SETTING_PIN FROM $TABLE_SETTINGS", null)
        } catch (e: SQLiteException) {
            return null
        }

        if (cursor.moveToFirst()) {
            val pin = cursor.getString(cursor.getColumnIndex(KEY_SETTING_PIN))
            cursor.close()

            return pin
        }

        return null
    }

    fun savePin(pin: String) {
        try {
            val db = this.writableDatabase
            val contentValues = ContentValues()

            contentValues.put(KEY_ID, 1)
            contentValues.put(KEY_SETTING_PIN, pin)

            if (readDriveFileIdDCount() > 0) {
                db.update(TABLE_SETTINGS, contentValues, "id=1", null)
            } else {
                db.insert(TABLE_SETTINGS, null, contentValues)
            }
        } catch (e: SQLiteException) {
        }
    }

    // Google Drive file
    fun readDriveFileId(): String? {
        if (readDriveFileIdDCount() == 0) return null

        val db = this.readableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("SELECT $KEY_DRIVE_FILE_ID FROM $TABLE_GOOGLE_DRIVE", null)
        } catch (e: SQLiteException) {
            return null
        }

        if (cursor.moveToFirst()) {
            val fileId = cursor.getString(cursor.getColumnIndex(KEY_DRIVE_FILE_ID))
            cursor.close()

            return fileId
        }

        return null
    }

    private fun readDriveFileIdDCount(): Int {
        val db = this.readableDatabase
        val count = DatabaseUtils.queryNumEntries(db, TABLE_GOOGLE_DRIVE)
        return count.toInt()
    }

    fun setDriveFileId(fileId: String) {
        try {
            val db = this.writableDatabase
            val contentValues = ContentValues()

            contentValues.put(KEY_ID, 1)
            contentValues.put(KEY_DRIVE_FILE_ID, fileId)

            if (readDriveFileIdDCount() > 0) {
                db.update(TABLE_GOOGLE_DRIVE, contentValues, "id=1", null)
            } else {
                db.insert(TABLE_GOOGLE_DRIVE, null, contentValues)
            }
        } catch (e: SQLiteException) {
        }
    }


    // Tokens
    private fun isTokenDuplicate(token: Token): Boolean {
        val tokenList = readTokens()

        for (t in tokenList) {
            if (t.isEqual(token)) return true
        }

        return false
    }

    private fun readTokens(): List<Token> {
        val tokenList = ArrayList<Token>()

        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_TOKENS ORDER BY id ASC", null)
        } catch (e: SQLiteException) {
            db.execSQL("SELECT * FROM $TABLE_TOKENS ORDER BY id ASC")
            return tokenList
        }

        var id: String
        var tokenString: String

        if (cursor.moveToFirst()) {
            do {
                try {
                    id = cursor.getString(cursor.getColumnIndex("id"))
                    tokenString = cursor.getString(cursor.getColumnIndex("token"))

                    val token = gson.fromJson(tokenString, Token::class.java)
                    token.id = id

                    tokenList.add(token)
                } catch (e: JsonSyntaxException) {
                }
            } while (cursor.moveToNext())
        }

        cursor.close()

        // Sort by label
        tokenList.sortBy { it.getLabel() }

        return tokenList
    }

    fun readOneToken(position: Int): Token {
        return readTokens()[position]
    }

    fun tokenLength(): Int {
        return readTokens().size
    }

    fun saveToken(token: Token) {
        try {
            val db = this.writableDatabase
            val contentValues = ContentValues()

            contentValues.put(KEY_ID, token.id)
            contentValues.put(KEY_TOKEN, gson.toJson(token))
            if (!isTokenDuplicate(token)) {
                db.insert(TABLE_TOKENS, null, contentValues)
            } else {
                db.update(TABLE_TOKENS, contentValues, "id='${token.id}'", null)
            }

        } catch (e: SQLiteException) {
        }
    }

    fun deleteToken(position: Int) {
        try {
            val db = this.writableDatabase
            val token = readOneToken(position)


            db.delete(TABLE_TOKENS, "id='${token.id}'", null)

        } catch (e: SQLiteException) {
        }
    }

    fun exportToken(): String? {
        try {

            val fileName = "SoteriaBackup-${System.currentTimeMillis()}"
            val file = File(getPath(context), fileName)

            val fw = FileWriter(file)
            val pw = PrintWriter(fw)
            val tokens: List<Token> = readTokens()

            for (token in tokens) {
                token.stringSecret = Base32().encodeToString(token.secret)
                pw.println(token.id + DELIMITER + gson.toJson(token))
            }

            pw.close()
            fw.close()


            return "${getPath(context)}/$fileName"
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun importToken(file: File): Boolean {
        try {
            val lines = file.readLines()

            for (line in lines) {
                if (line.contains(DELIMITER)) {
                    val parts = line.split(DELIMITER, limit = 2)

                    val key = parts[0]
                    val value = parts[1]

                    val token = gson.fromJson(value, Token::class.java)
                    token.id = key

                    saveToken(token)
                }
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}