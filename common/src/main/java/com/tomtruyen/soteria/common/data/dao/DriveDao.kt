package com.tomtruyen.soteria.common.data.dao

import androidx.room.Dao
import androidx.room.Query
import be.appwise.room.BaseRoomDao
import com.tomtruyen.soteria.common.data.DBConstants
import com.tomtruyen.soteria.common.data.entities.DriveFile

@Dao
abstract class DriveDao: BaseRoomDao<DriveFile>(DBConstants.DRIVE_FILE_TABLE_NAME) {
    @Query("SELECT * FROM drive_files LIMIT 1")
    abstract suspend fun findFirst(): DriveFile?
}