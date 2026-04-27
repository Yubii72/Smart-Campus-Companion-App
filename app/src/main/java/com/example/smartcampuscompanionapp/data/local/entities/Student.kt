package com.example.smartcampuscompanionapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val studentNumber: String,
    val password: String,
    
    // Personal Information
    val firstName: String,
    val lastName: String,
    val sexAtBirth: String,
    val nationality: String,
    val dateOfBirth: String,

    // Contact Information
    val presentAddress: String,
    val primaryMobileNumber: String,
    val primaryEmailAddress: String,

    // Family Background (Emergency Contacts)
    val fathersName: String,
    val mothersName: String,

    // Enrollment Details
    val college: String,
    val program: String,
    val curriculum: String,
    val yearLevel: String,
    val section: String
)
