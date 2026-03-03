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
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val studentDao = database.studentDao()
                        studentDao.insertStudent(
                            Student(
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
                                fathersName = "Juan Dela Cruz",
                                fathersOccupation = "Engineer",
                                fathersDateOfBirth = "1975-05-15",
                                fathersSexAtBirth = "Male",
                                mothersName = "Maria Dela Cruz",
                                mothersOccupation = "Teacher",
                                mothersDateOfBirth = "1978-08-20",
                                mothersSexAtBirth = "Female",
                                numberOfSiblings = 2,
                                familyAnnualIncome = 500000.0,
                                guardiansName = "Juan Dela Cruz",
                                relationToGuardian = "Father",
                                guardiansContactNumber = "09123456789",
                                lastSchoolAttended = "City High School",
                                lastYearAttended = "2023",
                                learnerReferenceNumber = "123456789012",
                                honorsReceived = "With Honors",
                                college = "College of Business Administration and Accountancy",
                                program = "Bachelor of Science in Accountancy",
                                curriculum = "2024 Revised",
                                yearLevel = "1st Year",
                                section = "A"
                            )
                        )
                    }
                }
            }
        }
    }
}
