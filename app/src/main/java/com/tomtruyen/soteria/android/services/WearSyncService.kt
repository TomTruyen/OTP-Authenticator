package com.tomtruyen.soteria.android.services

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.tomtruyen.soteria.common.SyncServiceConstants
import com.tomtruyen.soteria.common.data.entities.Token
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

object WearSyncService {
    private val gson by lazy { Gson() }

    fun syncTokens(context: Context, tokens: List<Token>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataClient = Wearable.getDataClient(context)

                val request = PutDataMapRequest.create(SyncServiceConstants.PATH_TOKENS).apply {
                    dataMap.putLong(SyncServiceConstants.KEY_TIMESTAMP, Instant.now().epochSecond)
                    dataMap.putString(SyncServiceConstants.KEY_TOKENS, gson.toJson(tokens))
                }

                dataClient.putDataItem(request.asPutDataRequest().setUrgent())
                    .addOnSuccessListener {}
                    .addOnFailureListener {}
                    .addOnCompleteListener {}
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}