package com.example.smartcampuscompanionapp.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "announcements",
    indices = [Index(value = ["firestoreId"], unique = true)]
)
data class Announcement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firestoreId: String? = null,
    val title: String,
    val content: String,
    val date: String,
    val author: String = "Admin",
    val isRead: Boolean = false
)
