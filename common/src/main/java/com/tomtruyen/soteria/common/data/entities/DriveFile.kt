package com.tomtruyen.soteria.common.data.entities

import androidx.room.Entity
import be.appwise.room.BaseEntity
import com.tomtruyen.soteria.common.data.DBConstants

@Entity(tableName = DBConstants.DRIVE_FILE_TABLE_NAME)
data class DriveFile(override val id: String): BaseEntity