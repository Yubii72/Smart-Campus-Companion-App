package com.example.smartcampuscompanionapp.data.model

import java.util.UUID

data class Announcement(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val author: String = "Admin",
    val timestamp: Long = System.currentTimeMillis()
) {
    // Required for Firestore
    constructor() : this(UUID.randomUUID().toString(), "", "", "", "Admin", System.currentTimeMillis())
}
