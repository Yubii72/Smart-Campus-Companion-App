package com.example.smartcampuscompanionapp.data.local.dao

import androidx.room.*
import com.example.smartcampuscompanionapp.data.local.entities.Announcement
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY date DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    @Update
    suspend fun updateAnnouncement(announcement: Announcement)

    @Delete
    suspend fun deleteAnnouncement(announcement: Announcement)

    @Query("SELECT COUNT(*) FROM announcements")
    suspend fun getAnnouncementCount(): Int
}
