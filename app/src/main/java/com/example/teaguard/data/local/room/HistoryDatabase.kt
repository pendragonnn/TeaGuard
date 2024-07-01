package com.example.teaguard.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.teaguard.data.local.entity.HistoryDiagnose

@Database(entities = [HistoryDiagnose::class], version = 1)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDiagnoseDao

    companion object {
        @Volatile
        private var INSTANCE : HistoryDatabase? = null

        @JvmStatic
        fun getDatabase(context : Context) : HistoryDatabase  {
            if (INSTANCE == null) {
                synchronized(HistoryDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        HistoryDatabase::class.java, "history_diagnose_database")
                        .build()
                }
            }
            return INSTANCE as HistoryDatabase
        }
    }

}