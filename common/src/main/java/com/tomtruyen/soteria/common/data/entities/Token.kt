package com.tomtruyen.soteria.common.data.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import be.appwise.room.BaseEntity
import com.tomtruyen.soteria.common.data.DBConstants
import com.tomtruyen.soteria.common.utils.OTPUtils
import java.util.*
import kotlin.jvm.Throws

@Entity(tableName = DBConstants.TOKEN_TABLE_NAME)
data class Token(
    @PrimaryKey override val id: String,
    val label: String,
    val secret: String,
    val algorithm: String = "SHA1",
    val digits: Int = 6,
    val period: Long = 30,
    val image: String? = null,
): BaseEntity {
    companion object {
        @Throws(IllegalArgumentException::class)
        fun fromUri(uri: Uri): Token {
            if(!OTPUtils.isUriValid(uri)) {
                throw IllegalArgumentException("Invalid URI")
            }

            val path = uri.path!!.replace("/", "")
            if(path.isEmpty()) {
                throw IllegalArgumentException("Invalid path")
            }

            val label = OTPUtils.getLabelFromUri(uri, path)
            val secret = OTPUtils.getSecretFromUri(uri)
            val algorithm = OTPUtils.getAlgorithmFromUri(uri)
            val digits = OTPUtils.getDigitsFromUri(uri)
            val period = OTPUtils.getPeriodFromUri(uri)
            val image = OTPUtils.getImageFromUri(uri)

            return Token(
                id = UUID.randomUUID().toString(),
                label = label,
                secret = secret,
                algorithm = algorithm,
                digits = digits,
                period = period,
                image = image,
            )
        }
    }
}