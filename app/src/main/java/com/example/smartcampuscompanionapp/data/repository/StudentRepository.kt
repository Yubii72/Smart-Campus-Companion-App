package com.example.smartcampuscompanionapp.data.repository

import com.example.smartcampuscompanionapp.data.local.dao.StudentDao
import com.example.smartcampuscompanionapp.data.local.entities.Student

class StudentRepository(private val studentDao: StudentDao) {
    suspend fun getStudentByNumber(studentNumber: String): Student? {
        return studentDao.getStudentByNumber(studentNumber)
    }

    suspend fun insertStudent(student: Student) {
        studentDao.insertStudent(student)
    }

    suspend fun getStudentCount(): Int {
        return studentDao.getStudentCount()
    }
}
