package com.example.smartcampuscompanionapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Announcement::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun announcementDao(): AnnouncementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_campus_companion_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}