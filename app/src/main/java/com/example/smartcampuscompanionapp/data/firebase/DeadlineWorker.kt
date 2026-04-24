package com.example.smartcampuscompanionapp.data.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartcampuscompanionapp.R
import com.example.smartcampuscompanionapp.data.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class DeadlineWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val studentNumber = inputData.getString("studentNumber") ?: return Result.failure()
        
        val firestore = FirebaseFirestore.getInstance()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        return try {
            val snapshot = firestore.collection("tasks")
                .whereEqualTo("studentNumber", studentNumber)
                .whereEqualTo("dueDate", tomorrowDate)
                .get()
                .await()

            val tasks = snapshot.toObjects(Task::class.java)
            if (tasks.isNotEmpty()) {
                tasks.forEach { task ->
                    showNotification(task)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(task: Task) {
        val channelId = "deadline_channel"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Task Deadlines", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Task Due Tomorrow!")
            .setContentText(task.title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(task.id.hashCode(), notification)
    }
}
