package com.example.smartcampuscompanionapp.data.repository

import com.example.smartcampuscompanionapp.data.local.entities.Student
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await

class FirebaseStudentRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    // Binago sa "users" base sa iyong Firestore screenshot
    private val studentsCollection = firestore.collection("users")

    fun getAllStudents(): Flow<List<Student>> = callbackFlow {
        val subscription = studentsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                android.util.Log.e("FirebaseStudent", "Error: ${error.message}")
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val studentList = snapshot.documents.map { doc ->
                    // Mapping Firestore "users" fields to our Student model
                    Student(
                        studentNumber = doc.getString("uid") ?: doc.id,
                        firstName = doc.getString("name") ?: "",
                        lastName = "", // Firestore only has "name"
                        primaryEmailAddress = doc.getString("email") ?: "",
                        program = doc.getString("course") ?: "",
                        college = "N/A",
                        password = "" // We don't read passwords from here
                    )
                }
                trySend(studentList)
            }
        }
        awaitClose { subscription.remove() }
    }.catch { e -> 
        android.util.Log.e("FirebaseStudent", "Flow catch: ${e.message}")
        emit(emptyList()) 
    }

    suspend fun saveStudent(student: Student) {
        try {
            // Mapping back to "users" collection format
            val userData = mapOf(
                "uid" to student.studentNumber,
                "name" to "${student.firstName} ${student.lastName}".trim(),
                "email" to student.primaryEmailAddress,
                "course" to student.program,
                "superUser" to false
            )
            studentsCollection.document(student.studentNumber).set(userData).await()
        } catch (e: Exception) {
            android.util.Log.e("FirebaseStudent", "Error saving student: ${e.message}")
        }
    }
}
