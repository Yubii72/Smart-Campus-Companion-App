package com.example.smartcampuscompanionapp.data.repository

import com.example.smartcampuscompanionapp.data.local.dao.AnnouncementDao
import com.example.smartcampuscompanionapp.data.local.entities.Announcement
import kotlinx.coroutines.flow.Flow

class AnnouncementRepository(private val announcementDao: AnnouncementDao) {
    val allAnnouncements: Flow<List<Announcement>> = announcementDao.getAllAnnouncements()

    suspend fun insert(announcement: Announcement) {
        announcementDao.insertAnnouncement(announcement)
    }

    suspend fun update(announcement: Announcement) {
        announcementDao.updateAnnouncement(announcement)
    }

    suspend fun delete(announcement: Announcement) {
        announcementDao.deleteAnnouncement(announcement)
    }
}
