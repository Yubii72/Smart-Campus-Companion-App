package com.example.smartcampuscompanionapp.data.model

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val studentNumber: String = "",
    val title: String = "",
    val dueDate: String = "",
    val description: String = ""
) {
    // Required for Firestore
    constructor() : this(UUID.randomUUID().toString(), "", "", "", "")
}
