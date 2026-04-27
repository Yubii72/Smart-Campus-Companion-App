package com.example.smartcampuscompanionapp.data.repository

import com.example.smartcampuscompanionapp.data.local.dao.StudentDao
import com.example.smartcampuscompanionapp.data.local.entities.Student
import kotlinx.coroutines.flow.Flow

class StudentRepository(private val studentDao: StudentDao) {
    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()

    suspend fun getStudentByNumber(studentNumber: String): Student? {
        return studentDao.getStudentByNumber(studentNumber)
    }

    suspend fun getStudentById(id: String): Student? {
        return studentDao.getStudentById(id)
    }

    suspend fun getStudentByEmail(email: String): Student? {
        return studentDao.getStudentByEmail(email)
    }

    suspend fun insertStudent(student: Student) {
        studentDao.insertStudent(student)
    }

    suspend fun getStudentCount(): Int {
        return studentDao.getStudentCount()
    }
}
