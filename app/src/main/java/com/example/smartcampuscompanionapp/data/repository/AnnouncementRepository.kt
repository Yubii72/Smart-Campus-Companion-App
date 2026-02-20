package com.example.smartcampuscompanionapp.data.repository

import com.example.smartcampuscompanionapp.data.local.db.Announcement
import com.example.smartcampuscompanionapp.data.local.db.AnnouncementDao
import kotlinx.coroutines.flow.Flow

class AnnouncementRepository(private val announcementDao: AnnouncementDao) {
    val allAnnouncements: Flow<List<Announcement>> = announcementDao.getAllAnnouncements()

    suspend fun insert(announcement: Announcement) {
        announcementDao.insertAnnouncement(announcement)
    }

    suspend fun markAsRead(id: Int) {
        announcementDao.markAsRead(id, true)
    }

    suspend fun delete(announcement: Announcement) {
        announcementDao.deleteAnnouncement(announcement)
    }
}
