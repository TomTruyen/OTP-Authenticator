package com.tomtruyen.soteria.common.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import be.appwise.room.BaseEntity
import com.tomtruyen.soteria.common.data.DBConstants

@Entity(tableName = DBConstants.DRIVE_FILE_TABLE_NAME)
data class DriveFile(@PrimaryKey override val id: String): BaseEntity