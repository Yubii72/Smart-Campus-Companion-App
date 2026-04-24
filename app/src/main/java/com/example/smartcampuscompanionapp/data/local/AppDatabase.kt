package com.example.smartcampuscompanionapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.smartcampuscompanionapp.data.local.dao.AnnouncementDao
import com.example.smartcampuscompanionapp.data.local.dao.StudentDao
import com.example.smartcampuscompanionapp.data.local.entities.Announcement
import com.example.smartcampuscompanionapp.data.local.entities.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Student::class, Announcement::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun announcementDao(): AnnouncementDao

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
                            val database = getDatabase(context)
                            database.studentDao().insertStudent(getDemoStudent())
                            insertDemoAnnouncements(database.announcementDao())
                        }
                    }
                    
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            val studentDao = database.studentDao()
                            if (studentDao.getStudentCount() == 0) {
                                studentDao.insertStudent(getDemoStudent())
                            }
                            val announcementDao = database.announcementDao()
                            if (announcementDao.getAnnouncementCount() == 0) {
                                insertDemoAnnouncements(announcementDao)
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun insertDemoAnnouncements(dao: AnnouncementDao) {
            val demos = listOf(
                Announcement(
                    title = "Final Examination Schedule",
                    date = "Oct 24, 2024",
                    content = "The final examination schedule for the first semester has been posted on the university website."
                ),
                Announcement(
                    title = "Campus Maintenance",
                    date = "Oct 20, 2024",
                    content = "The library will be closed this coming weekend for scheduled maintenance."
                ),
                Announcement(
                    title = "Foundation Day Celebration",
                    date = "Oct 15, 2024",
                    content = "Join us for the 50th Foundation Day celebration next month! Registration for events is now open."
                )
            )
            demos.forEach { dao.insertAnnouncement(it) }
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
            honorsReceived = "With Honors",
            fathersName = "",
            fathersOccupation = "",
            fathersDateOfBirth = "",
            fathersSexAtBirth = "",
            mothersName = "",
            mothersOccupation = "",
            mothersDateOfBirth = "",
            mothersSexAtBirth = "",
            numberOfSiblings = 0,
            familyAnnualIncome = 0.0,
            guardiansName = "",
            relationToGuardian = "",
            guardiansContactNumber = "",
            college = "",
            program = "",
            curriculum = "",
            yearLevel = "",
            section = ""
        )
    }
}
