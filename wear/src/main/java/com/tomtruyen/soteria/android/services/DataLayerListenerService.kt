package com.tomtruyen.soteria.android.services

import android.util.Log
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tomtruyen.soteria.android.repositories.TokenRepository
import com.tomtruyen.soteria.common.SyncServiceConstants
import com.tomtruyen.soteria.common.data.entities.Token
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataLayerListenerService: WearableListenerService() {
    private val gson by lazy { Gson() }

    override fun onDataChanged(events: DataEventBuffer) {
        events.map { dataEvent ->
            if (dataEvent.dataItem.uri.path == SyncServiceConstants.PATH_TOKENS) {
                val tokensGson = DataMapItem.fromDataItem(dataEvent.dataItem)
                    .dataMap
                    .getString(SyncServiceConstants.KEY_TOKENS)

                val tokenType = object : TypeToken<List<Token>>() {}.type

                val tokens = gson.fromJson<List<Token>>(tokensGson, tokenType)

                CoroutineScope(Dispatchers.IO).launch {
                    TokenRepository.tokenDao.insertManyDeleteOthers(tokens)
                }
            }
        }
    }
}