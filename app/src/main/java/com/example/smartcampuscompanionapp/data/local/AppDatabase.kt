package com.example.smartcampuscompanionapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.smartcampuscompanionapp.data.local.dao.StudentDao
import com.example.smartcampuscompanionapp.data.local.entities.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Student::class], version = 3, exportSchema = false)
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
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            getDatabase(context).studentDao().insertStudent(getDemoStudent())
                        }
                    }
                    
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getDatabase(context).studentDao()
                            if (dao.getStudentCount() == 0) {
                                dao.insertStudent(getDemoStudent())
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private fun getDemoStudent() = Student(
            studentNumber = "2024-0001",
            password = "password123",
            firstName = "Mark",
            lastName = "Dela Cruz",
            sexAtBirth = "Male",
            civilStatus = "Single",
            residency = "Local",
            nationality = "Filipino",
            religion = "Catholic",
            dateOfBirth = "2002-01-01",
            placeOfBirth = "Manila",
            presentAddress = "123 Main St, Quezon City",
            permanentAddress = "123 Main St, Quezon City",
            primaryMobileNumber = "09123456789",
            alternateMobileNumber = "09987654321",
            primaryEmailAddress = "student@university.edu.ph",
            alternateEmailAddress = "personal@email.com",
            lastSchoolAttended = "City High School",
            lastYearAttended = "2023",
            learnerReferenceNumber = "123456789012",
            honorsReceived = "With Honors"
        )
    }
}
