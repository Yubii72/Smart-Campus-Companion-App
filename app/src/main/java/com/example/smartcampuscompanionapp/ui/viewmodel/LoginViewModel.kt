package com.example.smartcampuscompanionapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanionapp.data.local.entities.Student
import com.example.smartcampuscompanionapp.data.repository.AuthRepository
import com.example.smartcampuscompanionapp.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: StudentRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Hardcoded Admin Credentials
    private val ADMIN_USERNAME = "admin"
    private val ADMIN_PASSWORD = "admin123"

    fun login(studentNumber: String, password: String) {
        if (studentNumber.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Please fill in all fields")
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            // Check for hardcoded admin first
            if (studentNumber.lowercase() == ADMIN_USERNAME && password == ADMIN_PASSWORD) {
                _uiState.value = LoginUiState.Success(studentNumber)
                return@launch
            }

            // Otherwise, check the local database for students
            val student = repository.getStudentByNumber(studentNumber)
            if (student != null && student.password == password) {
                _uiState.value = LoginUiState.Success(studentNumber)
            } else {
                _uiState.value = LoginUiState.Error("Invalid username or password")
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            if (result.isSuccess) {
                val user = result.getOrNull()
                _uiState.value = LoginUiState.Success(user?.displayName ?: user?.email ?: "Google User")
            } else {
                val exception = result.exceptionOrNull()
                exception?.printStackTrace()
                _uiState.value = LoginUiState.Error(exception?.message ?: "Google Sign-In failed")
            }
        }
    }

    fun register(student: Student) {
        viewModelScope.launch {
            repository.insertStudent(student)
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }

    fun setError(message: String) {
        _uiState.value = LoginUiState.Error(message)
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
