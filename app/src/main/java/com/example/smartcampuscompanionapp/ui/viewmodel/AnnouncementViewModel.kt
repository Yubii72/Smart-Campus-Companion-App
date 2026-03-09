package com.example.smartcampuscompanionapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanionapp.data.local.entities.Announcement
import com.example.smartcampuscompanionapp.data.repository.AnnouncementRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnnouncementViewModel(private val repository: AnnouncementRepository) : ViewModel() {
    val allAnnouncements: StateFlow<List<Announcement>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addAnnouncement(title: String, content: String, date: String) {
        viewModelScope.launch {
            repository.insert(Announcement(title = title, content = content, date = date))
        }
    }

    fun updateAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            repository.update(announcement)
        }
    }

    fun deleteAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            repository.delete(announcement)
        }
    }

    fun markAsRead(announcement: Announcement) {
        viewModelScope.launch {
            repository.update(announcement.copy(isRead = true))
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
