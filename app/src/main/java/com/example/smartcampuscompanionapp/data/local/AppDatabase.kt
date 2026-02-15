package com.example.smartcampuscompanionapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smartcampuscompanionapp.data.local.dao.StudentDao
import com.example.smartcampuscompanionapp.data.local.entities.Student

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_campus_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
