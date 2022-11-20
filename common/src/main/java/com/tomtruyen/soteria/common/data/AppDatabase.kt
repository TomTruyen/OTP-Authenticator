package com.tomtruyen.soteria.common.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tomtruyen.soteria.common.data.dao.DriveDao
import com.tomtruyen.soteria.common.data.dao.TokenDao
import com.tomtruyen.soteria.common.data.entities.DriveFile
import com.tomtruyen.soteria.common.data.entities.Token
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Database(
    entities = [Token::class, DriveFile::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun tokenDao(): TokenDao
    abstract fun driveDao(): DriveDao

    companion object {
        private val mScope = CoroutineScope(SupervisorJob())

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    DBConstants.DATABASE_NAME
                ).addCallback(DatabaseCallback(mScope))
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val mScope: CoroutineScope) : RoomDatabase.Callback()
}