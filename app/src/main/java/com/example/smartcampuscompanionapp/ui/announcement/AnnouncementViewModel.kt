package com.example.smartcampuscompanionapp.ui.announcement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanionapp.data.local.db.Announcement
import com.example.smartcampuscompanionapp.data.repository.AnnouncementRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnnouncementViewModel(private val repository: AnnouncementRepository) : ViewModel() {

    val allAnnouncements: StateFlow<List<Announcement>> = repository.allAnnouncements.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun markAsRead(announcement: Announcement) {
        viewModelScope.launch {
            repository.markAsRead(announcement.id)
        }
    }

    fun seedAnnouncements() {
        viewModelScope.launch {
            if (allAnnouncements.value.isEmpty()) {
                repository.insert(
                    Announcement(
                        title = "Welcome to Smart Campus Companion!",
                        content = "This is a sample announcement to get you started.",
                        date = "2024-01-01"
                    )
                )
                repository.insert(
                    Announcement(
                        title = "Midterm Exams Schedule",
                        content = "Please be advised that the midterm exams will be held from October 24 to 28, 2024.",
                        date = "2024-10-15"
                    )
                )
            }
        }
    }
}

class AnnouncementViewModelFactory(private val repository: AnnouncementRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnnouncementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnnouncementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
