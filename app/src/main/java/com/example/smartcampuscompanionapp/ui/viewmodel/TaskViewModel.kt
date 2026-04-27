package com.example.smartcampuscompanionapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanionapp.data.model.Task
import com.example.smartcampuscompanionapp.data.repository.FirebaseTaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: FirebaseTaskRepository, private val studentNumber: String) : ViewModel() {

    val tasks: StateFlow<List<Task>> = repository.getTasks(studentNumber)
        .catch { e ->
            e.printStackTrace()
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(task.copy(studentNumber = studentNumber))
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }
}

class TaskViewModelFactory(
    private val repository: FirebaseTaskRepository,
    private val studentNumber: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository, studentNumber) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
