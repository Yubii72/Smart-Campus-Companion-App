package com.example.smartcampuscompanionapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanionapp.data.local.entities.Student
import com.example.smartcampuscompanionapp.data.repository.AuthRepository
import com.example.smartcampuscompanionapp.data.repository.StudentRepository
import com.example.smartcampuscompanionapp.data.repository.FirebaseStudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: StudentRepository,
    private val authRepository: AuthRepository,
    private val firebaseStudentRepository: FirebaseStudentRepository = FirebaseStudentRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val ADMIN_USERNAME = "admin"
    private val ADMIN_PASSWORD = "admin123"

    fun login(studentNumber: String, password: String) {
        if (studentNumber.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Please fill in all fields")
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            if (studentNumber.lowercase() == ADMIN_USERNAME && password == ADMIN_PASSWORD) {
                _uiState.value = LoginUiState.Success(studentNumber)
                return@launch
            }

            val student = repository.getStudentByNumber(studentNumber)
            if (student != null && student.password == password) {
                _uiState.value = LoginUiState.Success(studentNumber)
            } else {
                _uiState.value = LoginUiState.Error("Invalid username or password")
            }
        }
    }

    fun register(student: Student) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                repository.insertStudent(student)
                // Sync to Firebase so admin can see it
                firebaseStudentRepository.saveStudent(student)
                _uiState.value = LoginUiState.Success(student.studentNumber)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }

    fun setError(message: String) {
        _uiState.value = LoginUiState.Error(message)
    }

    fun signInWithGoogle(idToken: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            if (result.isSuccess) {
                val user = result.getOrNull()
                val email = user?.email ?: ""
                val displayName = user?.displayName ?: ""
                val photoUrl = user?.photoUrl?.toString()
                
                // Use email as student number if not available, or some unique id
                val identifier = email.ifBlank { user?.uid ?: "GoogleUser" }
                
                // Check if student exists locally
                val existingStudent = repository.getStudentByNumber(identifier)
                if (existingStudent == null) {
                    val names = displayName.split(" ")
                    val firstName = names.firstOrNull() ?: ""
                    val lastName = if (names.size > 1) names.last() else ""
                    
                    val newStudent = Student(
                        studentNumber = identifier,
                        firstName = firstName,
                        lastName = lastName,
                        primaryEmailAddress = email,
                        profileImageUrl = photoUrl
                    )
                    repository.insertStudent(newStudent)
                    firebaseStudentRepository.saveStudent(newStudent)
                } else if (existingStudent.profileImageUrl == null && photoUrl != null) {
                    // Update profile image if it was null
                    val updatedStudent = existingStudent.copy(profileImageUrl = photoUrl)
                    repository.insertStudent(updatedStudent)
                }
                
                _uiState.value = LoginUiState.Success(identifier)
            } else {
                _uiState.value = LoginUiState.Error(result.exceptionOrNull()?.message ?: "Google Sign-In failed")
            }
        }
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val studentNumber: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModelFactory(
    private val repository: StudentRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
