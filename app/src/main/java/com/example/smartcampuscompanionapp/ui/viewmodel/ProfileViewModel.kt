package com.example.smartcampuscompanionapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanionapp.data.local.entities.Student
import com.example.smartcampuscompanionapp.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    object Success : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(private val repository: StudentRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    var isEditMode by mutableStateOf(false)

    // State for all fields
    var id by mutableStateOf("")
    var firstName by mutableStateOf("")
    var middleName by mutableStateOf("")
    var lastName by mutableStateOf("")

    var studentNumber by mutableStateOf("")
    var password by mutableStateOf("") 
    
    var sexAtBirth by mutableStateOf("")
    var nationality by mutableStateOf("")
    var dateOfBirth by mutableStateOf("")

    // Address state
    var presentProvince by mutableStateOf("")
    var presentCity by mutableStateOf("")
    var presentBarangay by mutableStateOf("")
    var presentHouse by mutableStateOf("")
    var presentZip by mutableStateOf("")

    var primaryMobileNumber by mutableStateOf("")
    var primaryEmailAddress by mutableStateOf("")

    // Father
    var fatherFirstName by mutableStateOf("")
    var fatherMiddleName by mutableStateOf("")
    var fatherLastName by mutableStateOf("")

    // Mother
    var motherFirstName by mutableStateOf("")
    var middleNameMother by mutableStateOf("")
    var motherLastName by mutableStateOf("")

    // Enrollment
    var college by mutableStateOf("")
    var program by mutableStateOf("")
    var curriculum by mutableStateOf("")
    var yearLevel by mutableStateOf("")
    var section by mutableStateOf("")
    var profileImageUrl by mutableStateOf<String?>(null)

    fun loadProfile(identifier: String) {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            try {
                // Try looking up by ID first (preferred), then by studentNumber as fallback
                val student = repository.getStudentById(identifier) 
                    ?: repository.getStudentByNumber(identifier)
                    ?: repository.getStudentByEmail(identifier)

                if (student != null) {
                    id = student.id
                    studentNumber = student.studentNumber
                    password = student.password
                    firstName = student.firstName
                    lastName = student.lastName
                    
                    sexAtBirth = student.sexAtBirth
                    nationality = student.nationality
                    dateOfBirth = student.dateOfBirth

                    // Parse address
                    val presentParts = student.presentAddress.split(", ")
                    presentHouse = presentParts.getOrNull(0) ?: ""
                    presentBarangay = presentParts.getOrNull(1) ?: ""
                    presentCity = presentParts.getOrNull(2) ?: ""
                    presentProvince = presentParts.getOrNull(3) ?: ""
                    
                    primaryMobileNumber = student.primaryMobileNumber
                    primaryEmailAddress = student.primaryEmailAddress

                    // Family - Father
                    val fNames = student.fathersName.split(" ")
                    fatherFirstName = fNames.getOrNull(0) ?: ""
                    fatherMiddleName = fNames.getOrNull(1) ?: ""
                    fatherLastName = fNames.getOrNull(2) ?: ""

                    // Family - Mother
                    val mNames = student.mothersName.split(" ")
                    motherFirstName = mNames.getOrNull(0) ?: ""
                    middleNameMother = mNames.getOrNull(1) ?: ""
                    motherLastName = mNames.getOrNull(2) ?: ""

                    college = student.college
                    program = student.program
                    curriculum = student.curriculum
                    yearLevel = student.yearLevel
                    section = student.section
                    profileImageUrl = student.profileImageUrl
                    _uiState.value = ProfileUiState.Success
                } else {
                    _uiState.value = ProfileUiState.Error("Student not found")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun saveProfile() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            try {
                val updatedStudent = Student(
                    id = id,
                    studentNumber = studentNumber,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    sexAtBirth = sexAtBirth,
                    nationality = nationality,
                    dateOfBirth = dateOfBirth,
                    presentAddress = "$presentHouse, $presentBarangay, $presentCity, $presentProvince",
                    primaryMobileNumber = primaryMobileNumber,
                    primaryEmailAddress = primaryEmailAddress,
                    fathersName = "$fatherFirstName $fatherMiddleName $fatherLastName".trim(),
                    mothersName = "$motherFirstName $middleNameMother $motherLastName".trim(),
                    college = college,
                    program = program,
                    curriculum = curriculum,
                    yearLevel = yearLevel,
                    section = section,
                    profileImageUrl = profileImageUrl
                )
                repository.insertStudent(updatedStudent)
                isEditMode = false
                _uiState.value = ProfileUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to save profile")
            }
        }
    }
}

class ProfileViewModelFactory(private val repository: StudentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
