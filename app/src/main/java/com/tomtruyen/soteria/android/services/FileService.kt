package com.tomtruyen.soteria.android.services

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.tomtruyen.soteria.android.App
import com.tomtruyen.soteria.android.repositories.TokenRepository
import com.tomtruyen.soteria.common.data.entities.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.lang.Exception

class FileService(private val context: Context) {
    companion object {
        private const val DELIMITER = "==="
    }

    private val gson by lazy { Gson() }

    private val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath + "/SoteriaBackup-${System.currentTimeMillis()}"

    suspend fun export(): File? {
        try {
            Log.d("@@@", "Exporting to $filePath")
            val file = File(filePath)

            val fw = FileWriter(file)
            val pw = PrintWriter(fw)

            TokenRepository.tokenDao.findAllEntities()?.let {
                for(token in it) {
                    pw.println(token.id + DELIMITER + gson.toJson(token))
                }
            }

            pw.close()
            fw.close()

            return file
        } catch (_: Exception) {
            return null
        }
    }

    suspend fun import(file: File, onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}) {
        try {
            val lines = file.readLines()

            for(line in lines) {
                if(line.contains(DELIMITER)) {
                    val parts = line.split(DELIMITER, limit = 2)

                    val key = parts[0]
                    val value = parts[1]

                    val token = gson.fromJson(value, Token::class.java)
                    token.id = key

                    TokenRepository.tokenDao.insert(token)
                }
            }

            val tokens = TokenRepository.tokenDao.findAllEntities() ?: emptyList()
            WearSyncService.syncTokens(context, tokens)

            onSuccess()
        } catch (_: Exception) {
            onFailure()
        }
    }
}