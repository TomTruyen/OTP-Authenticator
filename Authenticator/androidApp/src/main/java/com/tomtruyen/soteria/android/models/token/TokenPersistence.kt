package com.tomtruyen.soteria.android.models.token

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.tomtruyen.soteria.android.models.token.Token.TokenUriInvalidException
import java.io.*
import java.lang.reflect.Type
import java.util.*



class TokenPersistence(ctx: Context) {
    lateinit var path: File
    private val IMPORT_DELIMITER = "==="
    private val NAME = "tokens"
    private val ORDER = "tokenOrder"
    private var gson: Gson = Gson()
    private var prefs: SharedPreferences =
        ctx.applicationContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    init {
        try {
            var pathLocation = ctx.getExternalFilesDir(null)!!.absolutePath

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                pathLocation = ctx.getExternalFilesDirs(null).last().absolutePath
            }

            path = File(pathLocation.toString())
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
        }
    }

    private fun getTokenOrder(): List<String> {
        val type: Type = object : TypeToken<List<String?>?>() {}.type
        val str = prefs.getString(ORDER, "[]")
        val order = gson.fromJson<List<String>>(str, type)
        return order ?: LinkedList<String>()
    }

    private fun setTokenOrder(order: List<String>): SharedPreferences.Editor? {
        return prefs.edit()?.putString(ORDER, gson.toJson(order))
    }

    fun save(token: Token): Boolean {
            val key: String = token.id

            if (prefs.contains(key)) {
                prefs.edit().putString(key, gson.toJson(token)).apply()
                return true
            }

            val order = getTokenOrder().toMutableList()
            order.add(0, key)
            setTokenOrder(order.toList())?.putString(key, gson.toJson(token))?.apply()

            return true
    }

    fun delete(position: Int) {
        val order = getTokenOrder().toMutableList()
        val key: String = order.removeAt(position)
        setTokenOrder(order)?.remove(key)?.apply()
    }

    fun length(): Int {
        return getTokenOrder().size
    }

    fun get(position: Int): Token? {
        val key = getTokenOrder()[position]
        val str = prefs.getString(key, null)

        try {
            return gson.fromJson(str, Token::class.java)
        } catch (jse: JsonSyntaxException) {
            // Backwards compatibility for URL-based persistence.
            try {
                return Token(Uri.parse(str), true)
            } catch (e: TokenUriInvalidException) {
                e.printStackTrace()
            }
        }

        return null
    }

    fun export() : String?{
        try {

            val file = File(path, "SoteriaBackup-${System.currentTimeMillis()}")

            val fw = FileWriter(file)
            val pw = PrintWriter(fw)
            val prefs : Map<String, *> = prefs.all
            for (entry in prefs.entries) {
                pw.println(entry.key + IMPORT_DELIMITER + entry.value.toString())
            }
            pw.close()
            fw.close()

            return "${path}/SoteriaBackup-${System.currentTimeMillis()}"
        } catch (e:Exception) {
            e.message
            e.printStackTrace()
            return null
        }
    }

    fun import(file: File) : Boolean {
        try {
            val lines = file.readLines()

            for(i in lines.indices) {
                val line = lines[i]

                if(line.contains(IMPORT_DELIMITER)) {
                    val parts = line.split(IMPORT_DELIMITER, limit = 2)

                    val key = parts[0]
                    val value = parts[1]

                    if(i == lines.size - 1) {
                        setTokenOrder(gson.fromJson(value, object : TypeToken<List<String>>() {}.type))
                        break
                    }

                    val token = gson.fromJson(value, Token::class.java)
                    token.id = key

                    save(token)
                }
            }

            return true
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            return false
        }
    }


}