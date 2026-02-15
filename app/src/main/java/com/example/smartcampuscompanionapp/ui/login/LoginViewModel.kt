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
            // We'll update the data even if not empty to ensure the new details are used
            val demoStudent = Student(
                studentNumber = "2203509",
                password = "password123",
                avatar = "irang", // Resource name
                sexAtBirth = "Male",
                civilStatus = "Single",
                residency = "Cabuyeño",
                nationality = "Filipino",
                religion = "Roman Catholic",
                dateOfBirth = "May 5, 2003",
                placeOfBirth = "Sala, Cabuyao, Laguna",
                presentAddress = "Block 1 Lot 1 Phase 1 Brgy. Worldwide, City of Cabuyao, Laguna",
                permanentAddress = "Block 1 Lot 1 Phase 1 Brgy. Worldwide, City of Cabuyao, Laguna",
                primaryMobileNumber = "09000000000",
                alternateMobileNumber = "N/A",
                primaryEmailAddress = "irangfernandojr09@gmail.com",
                alternateEmailAddress = "N/A",
                fathersName = "Papa Irang",
                fathersOccupation = "Tricycle driver",
                fathersDateOfBirth = "October 19, 1972",
                fathersSexAtBirth = "Male",
                mothersName = "Mama Irang",
                mothersOccupation = "Factory worker",
                mothersDateOfBirth = "November 18, 1976",
                mothersSexAtBirth = "Female",
                numberOfSiblings = 3,
                familyAnnualIncome = 307190.0,
                guardiansName = "Fernando Irang",
                relationToGuardian = "Father",
                guardiansContactNumber = "09065387621",
                lastSchoolAttended = "Worldwide Integrated High School",
                lastYearAttended = "2022",
                learnerReferenceNumber = "1192498",
                honorsReceived = "Most Sleepless of 2022\nWith Highest Honors, 96",
                college = "College of Computing Studies",
                program = "Bachelor of Science in Information Technology",
                curriculum = "BSIT 2018",
                yearLevel = "Third Year",
                section = "IT-C"
            )
            repository.insertStudent(demoStudent)
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
