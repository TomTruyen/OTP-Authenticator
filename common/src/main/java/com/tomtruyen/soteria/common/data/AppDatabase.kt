package com.tomtruyen.soteria.common.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tomtruyen.soteria.common.BuildConfig
import com.tomtruyen.soteria.common.data.dao.DriveDao
import com.tomtruyen.soteria.common.data.dao.TokenDao
import com.tomtruyen.soteria.common.data.entities.DriveFile
import com.tomtruyen.soteria.common.data.entities.Token
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*

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
                ).addCallback(SeedCallback(mScope, context))
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedCallback(private val mScope: CoroutineScope, private val mContext: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            if(BuildConfig.DEBUG) {
                mScope.launch {
                    val tokenDao = getDatabase(mContext).tokenDao()
                    tokenDao.insertManyDeleteOthers(
                        listOf(
                            Token(
                                id = UUID.randomUUID().toString(),
                                secret = "JBSWY3DPEHPK3PXP",
                                label = "Google (john@gmail.com)",
                            ),
                            Token(
                                id = UUID.randomUUID().toString(),
                                secret = "JBSWY3DPEHPK3PXL",
                                label = "Facebook (john@gmail.com)",
                            ),
                            Token(
                                id = UUID.randomUUID().toString(),
                                secret = "JBSWY3DPEHPK3PXO",
                                label = "Instagram (john@gmail.com)",
                            ),
                            Token(
                                id = UUID.randomUUID().toString(),
                                secret = "JBSWY3DPEHPK3PXI",
                                label = "YouTube (john@gmail.com)",
                            ),
                            Token(
                                id = UUID.randomUUID().toString(),
                                secret = "JBSWY3DPEHPK3PXK",
                                label = "Twitter (john@gmail.com)",
                            )
                        )
                    )
                }
            }
        }
    }
}