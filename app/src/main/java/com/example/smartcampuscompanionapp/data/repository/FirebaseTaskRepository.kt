package com.example.smartcampuscompanionapp.data.repository

import com.example.smartcampuscompanionapp.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await

class FirebaseTaskRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val tasksCollection = firestore.collection("tasks")

    fun getTasks(studentNumber: String): Flow<List<Task>> = callbackFlow {
        val subscription = tasksCollection
            .whereEqualTo("studentNumber", studentNumber)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("Firestore", "Error fetching tasks: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val tasks = snapshot.toObjects(Task::class.java)
                    trySend(tasks)
                }
            }
        awaitClose { subscription.remove() }
    }.catch { e ->
        android.util.Log.e("Firestore", "Tasks Flow caught error: ${e.message}")
        emit(emptyList())
    }

    suspend fun addTask(task: Task): Result<Unit> {
        return try {
            tasksCollection.document(task.id).set(task).await()
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error adding task: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            tasksCollection.document(task.id).set(task).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            tasksCollection.document(taskId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
