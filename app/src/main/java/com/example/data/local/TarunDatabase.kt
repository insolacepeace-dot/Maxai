package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ConversationEntity::class,
        NotificationEventEntity::class,
        MemoryFactEntity::class,
        AutomationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class TarunDatabase : RoomDatabase() {
    abstract fun tarunDao(): TarunDao

    companion object {
        @Volatile
        private var INSTANCE: TarunDatabase? = null

        fun getInstance(context: Context): TarunDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TarunDatabase::class.java,
                    "tarun_ai_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
