package com.cvte.maxhub.mvvmsample.models.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cvte.maxhub.mvvmsample.models.data.Voting
import com.orhanobut.logger.Logger

/**
 * 数据库
 */
@Database(entities = [Voting::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun votingDao(): VotingDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(
                        context
                    ).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            Logger.d("buildDatabase")
            return Room.databaseBuilder(context, AppDatabase::class.java, "voting-db")
                .addCallback((object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Logger.d("onCreate Database")
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        Logger.d("onOpen Database")
                    }
                }))
                .build()
        }
    }
}