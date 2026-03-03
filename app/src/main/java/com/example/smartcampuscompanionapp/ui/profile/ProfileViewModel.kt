package com.example.smartcampuscompanionapp.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanionapp.data.local.entities.Student
import com.example.smartcampuscompanionapp.data.repository.StudentRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: StudentRepository) : ViewModel() {
    var isEditMode by mutableStateOf(false)

    // State for all fields
    var firstName by mutableStateOf("")
    var middleName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var nameExtension by mutableStateOf("")

    var studentNumber by mutableStateOf("")
    var password by mutableStateOf("") // Need this to preserve it during updates
    
    var sexAtBirth by mutableStateOf("")
    var civilStatus by mutableStateOf("")
    var residency by mutableStateOf("")
    var nationality by mutableStateOf("")
    var religion by mutableStateOf("")
    var dateOfBirth by mutableStateOf("")
    var placeOfBirth by mutableStateOf("")

    // Address state
    var presentProvince by mutableStateOf("")
    var presentCity by mutableStateOf("")
    var presentBarangay by mutableStateOf("")
    var presentHouse by mutableStateOf("")
    var presentZip by mutableStateOf("")

    var permanentProvince by mutableStateOf("")
    var permanentCity by mutableStateOf("")
    var permanentBarangay by mutableStateOf("")
    var permanentHouse by mutableStateOf("")
    var permanentZip by mutableStateOf("")

    var primaryMobileNumber by mutableStateOf("")
    var alternateMobileNumber by mutableStateOf("")
    var primaryEmailAddress by mutableStateOf("")
    var alternateEmailAddress by mutableStateOf("")

    // Educational
    var lastSchoolAttended by mutableStateOf("")
    var lastYearAttended by mutableStateOf("")
    var learnerReferenceNumber by mutableStateOf("")
    var honorsReceived by mutableStateOf("")

    fun loadProfile(studentNum: String) {
        viewModelScope.launch {
            repository.getStudentByNumber(studentNum)?.let { student ->
                studentNumber = student.studentNumber
                password = student.password
                firstName = student.firstName
                lastName = student.lastName
                
                sexAtBirth = student.sexAtBirth
                civilStatus = student.civilStatus
                residency = student.residency
                nationality = student.nationality
                religion = student.religion
                dateOfBirth = student.dateOfBirth
                placeOfBirth = student.placeOfBirth

                // Parse address components
                val presentParts = student.presentAddress.split(", ")
                presentHouse = presentParts.getOrNull(0) ?: ""
                presentBarangay = presentParts.getOrNull(1) ?: ""
                presentCity = presentParts.getOrNull(2) ?: ""
                presentProvince = presentParts.getOrNull(3) ?: ""
                
                val permanentParts = student.permanentAddress.split(", ")
                permanentHouse = permanentParts.getOrNull(0) ?: ""
                permanentBarangay = permanentParts.getOrNull(1) ?: ""
                permanentCity = permanentParts.getOrNull(2) ?: ""
                permanentProvince = permanentParts.getOrNull(3) ?: ""

                primaryMobileNumber = student.primaryMobileNumber
                alternateMobileNumber = student.alternateMobileNumber
                primaryEmailAddress = student.primaryEmailAddress
                alternateEmailAddress = student.alternateEmailAddress

                lastSchoolAttended = student.lastSchoolAttended
                lastYearAttended = student.lastYearAttended
                learnerReferenceNumber = student.learnerReferenceNumber
                honorsReceived = student.honorsReceived
            }
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val updatedStudent = Student(
                studentNumber = studentNumber,
                password = password,
                firstName = firstName,
                lastName = lastName,
                sexAtBirth = sexAtBirth,
                civilStatus = civilStatus,
                residency = residency,
                nationality = nationality,
                religion = religion,
                dateOfBirth = dateOfBirth,
                placeOfBirth = placeOfBirth,
                presentAddress = "$presentHouse, $presentBarangay, $presentCity, $presentProvince",
                permanentAddress = "$permanentHouse, $permanentBarangay, $permanentCity, $permanentProvince",
                primaryMobileNumber = primaryMobileNumber,
                alternateMobileNumber = alternateMobileNumber,
                primaryEmailAddress = primaryEmailAddress,
                alternateEmailAddress = alternateEmailAddress,
                lastSchoolAttended = lastSchoolAttended,
                lastYearAttended = lastYearAttended,
                learnerReferenceNumber = learnerReferenceNumber,
                honorsReceived = honorsReceived
            )
            repository.insertStudent(updatedStudent)
            isEditMode = false
        }
    }
}

class ProfileViewModelFactory(private val repository: StudentRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
