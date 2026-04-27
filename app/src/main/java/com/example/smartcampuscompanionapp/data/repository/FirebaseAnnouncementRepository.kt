package com.example.smartcampuscompanionapp.data.repository

import com.example.smartcampuscompanionapp.data.model.Announcement
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await

class FirebaseAnnouncementRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val announcementsCollection = firestore.collection("announcements")

    fun getAnnouncements(): Flow<List<Announcement>> = callbackFlow {
        val subscription = announcementsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Log the error but don't crash the flow
                    android.util.Log.e("Firestore", "Error fetching announcements: ${error.message}")
                    trySend(emptyList()) // Send empty list instead of crashing
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val announcements = snapshot.toObjects(Announcement::class.java)
                    trySend(announcements)
                }
            }
        awaitClose { subscription.remove() }
    }.catch { e ->
        android.util.Log.e("Firestore", "Flow caught error: ${e.message}")
        emit(emptyList())
    }

    suspend fun addAnnouncement(announcement: Announcement): Result<Unit> {
        return try {
            announcementsCollection.document(announcement.id).set(announcement).await()
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error adding announcement: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteAnnouncement(id: String): Result<Unit> {
        return try {
            announcementsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error deleting announcement: ${e.message}")
            Result.failure(e)
        }
    }
}
