package com.example.smartcampuscompanionapp.data.local.dao

import androidx.room.*
import com.example.smartcampuscompanionapp.data.local.entities.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: String): Student?

    @Query("SELECT * FROM students WHERE studentNumber = :studentNumber LIMIT 1")
    suspend fun getStudentByNumber(studentNumber: String): Student?

    @Query("SELECT * FROM students WHERE primaryEmailAddress = :email LIMIT 1")
    suspend fun getStudentByEmail(email: String): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int

    @Query("SELECT * FROM students ORDER BY lastName ASC")
    fun getAllStudents(): Flow<List<Student>>
}
