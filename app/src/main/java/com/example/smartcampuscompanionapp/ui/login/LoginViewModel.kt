package com.example.smartcampuscompanionapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanionapp.data.local.entities.Student
import com.example.smartcampuscompanionapp.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StudentRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(studentNumber: String, password: String) {
        if (studentNumber.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Please fill in all fields")
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val student = repository.getStudentByNumber(studentNumber)
            if (student != null && student.password == password) {
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error("Invalid student number or password")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }

    // Temporary method to seed data if database is empty
    fun seedDataIfEmpty() {
        viewModelScope.launch {
            if (repository.getStudentCount() == 0) {
                val demoStudent = Student(
                    studentNumber = "2024-0001",
                    password = "password123",
                    firstName = "Irang",
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
                repository.insertStudent(demoStudent)
            }
        }
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModelFactory(private val repository: StudentRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
