package com.example.smartcampuscompanionapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val studentNumber: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    
    // Personal Information
    val sexAtBirth: String,
    val civilStatus: String,
    val residency: String,
    val nationality: String,
    val religion: String,
    val dateOfBirth: String,
    val placeOfBirth: String,

    // Contact Information
    val presentAddress: String,
    val permanentAddress: String,
    val primaryMobileNumber: String,
    val alternateMobileNumber: String,
    val primaryEmailAddress: String,
    val alternateEmailAddress: String,

    // Family Background
    val fathersName: String = "",
    val fathersOccupation: String = "",
    val fathersDateOfBirth: String = "",
    val fathersSexAtBirth: String = "",

    val mothersName: String = "",
    val mothersOccupation: String = "",
    val mothersDateOfBirth: String = "",
    val mothersSexAtBirth: String = "",

    val numberOfSiblings: Int = 0,
    val familyAnnualIncome: Double = 0.0,

    val guardiansName: String = "",
    val relationToGuardian: String = "",
    val guardiansContactNumber: String = "",

    // Educational Background
    val lastSchoolAttended: String,
    val lastYearAttended: String,
    val learnerReferenceNumber: String,
    val honorsReceived: String,

    // Enrollment Details
    val college: String = "",
    val program: String = "",
    val curriculum: String = "",
    val yearLevel: String = "",
    val section: String = ""
)
