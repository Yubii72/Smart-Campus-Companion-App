package com.example.smartcampuscompanionapp.data.repository

import com.example.smartcampuscompanionapp.data.model.Announcement
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAnnouncementRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val announcementsCollection = firestore.collection("announcements")

    fun getAnnouncements(): Flow<List<Announcement>> = callbackFlow {
        val subscription = announcementsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val announcements = snapshot.toObjects(Announcement::class.java)
                    trySend(announcements)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addAnnouncement(announcement: Announcement): Result<Unit> {
        return try {
            announcementsCollection.document(announcement.id).set(announcement).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAnnouncement(id: String): Result<Unit> {
        return try {
            announcementsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
