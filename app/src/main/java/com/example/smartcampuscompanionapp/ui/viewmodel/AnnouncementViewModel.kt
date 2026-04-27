package com.example.smartcampuscompanionapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanionapp.data.local.entities.Announcement as LocalAnnouncement
import com.example.smartcampuscompanionapp.data.model.Announcement as FirebaseAnnouncement
import com.example.smartcampuscompanionapp.data.repository.AnnouncementRepository
import com.example.smartcampuscompanionapp.data.repository.FirebaseAnnouncementRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnnouncementViewModel(
    private val localRepository: AnnouncementRepository,
    private val firebaseRepository: FirebaseAnnouncementRepository
) : ViewModel() {

    val allAnnouncements: StateFlow<List<LocalAnnouncement>> = localRepository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        syncWithFirebase()
    }

    private fun syncWithFirebase() {
        viewModelScope.launch {
            firebaseRepository.getAnnouncements().collectLatest { firebaseList ->
                // Use the current list once to create a set of existing IDs
                val existingIds = allAnnouncements.value.mapNotNull { it.firestoreId }.toSet()
                
                firebaseList.forEach { firebaseAnn ->
                    if (!existingIds.contains(firebaseAnn.id)) {
                        localRepository.insert(LocalAnnouncement(
                            firestoreId = firebaseAnn.id,
                            title = firebaseAnn.title,
                            content = firebaseAnn.content,
                            date = firebaseAnn.date,
                            author = firebaseAnn.author
                        ))
                    }
                }
                
                // Optional: Delete local ones that are no longer in Firebase
                allAnnouncements.value.forEach { localAnn ->
                    if (localAnn.firestoreId != null && firebaseList.none { it.id == localAnn.firestoreId }) {
                        localRepository.delete(localAnn)
                    }
                }
            }
        }
    }

    fun addAnnouncement(title: String, content: String, date: String) {
        viewModelScope.launch {
            val announcement = FirebaseAnnouncement(
                title = title,
                content = content,
                date = date
            )
            firebaseRepository.addAnnouncement(announcement)
            // Also add locally for the admin to see immediately
            localRepository.insert(LocalAnnouncement(
                firestoreId = announcement.id,
                title = title,
                content = content,
                date = date
            ))
        }
    }

    fun updateAnnouncement(announcement: LocalAnnouncement) {
        viewModelScope.launch {
            localRepository.update(announcement)
            // Ideally we should also update firebase here
            val firebaseAnn = FirebaseAnnouncement(
                id = announcement.firestoreId ?: return@launch,
                title = announcement.title,
                content = announcement.content,
                date = announcement.date,
                author = announcement.author
            )
            firebaseRepository.addAnnouncement(firebaseAnn) // set() will update
        }
    }

    fun deleteAnnouncement(announcement: LocalAnnouncement) {
        viewModelScope.launch {
            localRepository.delete(announcement)
            announcement.firestoreId?.let {
                firebaseRepository.deleteAnnouncement(it)
            }
        }
    }

    fun markAsRead(announcement: LocalAnnouncement) {
        viewModelScope.launch {
            localRepository.update(announcement.copy(isRead = true))
        }
    }
}

class AnnouncementViewModelFactory(
    private val localRepository: AnnouncementRepository,
    private val firebaseRepository: FirebaseAnnouncementRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnnouncementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnnouncementViewModel(localRepository, firebaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
