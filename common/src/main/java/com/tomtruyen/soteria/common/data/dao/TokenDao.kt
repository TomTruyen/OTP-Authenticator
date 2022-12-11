package com.tomtruyen.soteria.common.data.dao

import androidx.room.Dao
import androidx.room.Query
import be.appwise.room.BaseRoomDao
import com.tomtruyen.soteria.common.data.DBConstants
import com.tomtruyen.soteria.common.data.entities.Token
import kotlinx.coroutines.flow.Flow


@Dao
abstract class TokenDao: BaseRoomDao<Token>(DBConstants.TOKEN_TABLE_NAME) {
    @Query("SELECT * FROM tokens ORDER BY LOWER(label) ASC")
    abstract fun findAllLive(): Flow<List<Token>>

    @Query("SELECT * FROM tokens ORDER BY LOWER(label) ASC")
    abstract suspend fun findAll(): List<Token>

    @Query("SELECT * FROM tokens WHERE id = :id")
    abstract fun findLiveById(id: String): Flow<Token>

    @Query("DELETE FROM tokens WHERE id = :id")
    abstract suspend fun deleteById(id: String)
}