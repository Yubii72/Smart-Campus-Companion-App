package com.example.smartcampuscompanionapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.edit
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.smartcampuscompanionapp.data.firebase.DeadlineWorker
import com.example.smartcampuscompanionapp.data.local.AppDatabase
import com.example.smartcampuscompanionapp.data.repository.AnnouncementRepository
import com.example.smartcampuscompanionapp.data.repository.AuthRepository
import com.example.smartcampuscompanionapp.data.repository.FirebaseAnnouncementRepository
import com.example.smartcampuscompanionapp.data.repository.FirebaseTaskRepository
import com.example.smartcampuscompanionapp.data.repository.StudentRepository
import com.example.smartcampuscompanionapp.ui.admin.AdminDashboardScreen
import com.example.smartcampuscompanionapp.ui.admin.AdminLoginScreen
import com.example.smartcampuscompanionapp.ui.campus_info.College
import com.example.smartcampuscompanionapp.ui.campus_info.CollegeInfoScreen
import com.example.smartcampuscompanionapp.ui.campus_info.CollegeListScreen
import com.example.smartcampuscompanionapp.ui.onboarding.OnboardingScreen
import com.example.smartcampuscompanionapp.ui.student.ScheduleScreen
import com.example.smartcampuscompanionapp.data.model.Task
import com.example.smartcampuscompanionapp.ui.settings.SettingsScreen
import com.example.smartcampuscompanionapp.ui.student.AnnouncementScreen
import com.example.smartcampuscompanionapp.ui.student.DashboardScreen
import com.example.smartcampuscompanionapp.ui.student.LoginScreen
import com.example.smartcampuscompanionapp.ui.student.ProfileScreen
import com.example.smartcampuscompanionapp.ui.student.RegisterScreen
import com.example.smartcampuscompanionapp.ui.theme.SmartCampusCompanionAppTheme
import com.example.smartcampuscompanionapp.ui.viewmodel.AnnouncementViewModel
import com.example.smartcampuscompanionapp.ui.viewmodel.AnnouncementViewModelFactory
import com.example.smartcampuscompanionapp.ui.viewmodel.LoginViewModel
import com.example.smartcampuscompanionapp.ui.viewmodel.LoginViewModelFactory
import com.example.smartcampuscompanionapp.ui.viewmodel.ProfileViewModel
import com.example.smartcampuscompanionapp.ui.viewmodel.ProfileViewModelFactory
import com.example.smartcampuscompanionapp.ui.viewmodel.TaskViewModel
import com.example.smartcampuscompanionapp.ui.viewmodel.TaskViewModelFactory
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentChange
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import java.util.concurrent.TimeUnit

enum class MainTab(val title: String, val icon: ImageVector) {
    Dashboard("Dashboard", Icons.Default.Home),
    Tasks("Tasks", Icons.Default.CalendarToday),
    Profile("Profile", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val studentRepository by lazy { StudentRepository(database.studentDao()) }
    private val announcementRepository by lazy { AnnouncementRepository(database.announcementDao()) }
    private val authRepository by lazy { AuthRepository() }
    private val firebaseTaskRepository by lazy { FirebaseTaskRepository() }
    private val firebaseAnnouncementRepository by lazy { FirebaseAnnouncementRepository() }
    
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(studentRepository, authRepository)
    }
    
    private val announcementViewModel: AnnouncementViewModel by viewModels {
        AnnouncementViewModelFactory(announcementRepository, firebaseAnnouncementRepository)
    }
    
    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(studentRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("smart_campus_prefs", Context.MODE_PRIVATE)

        // Notification permission request (Android 13+)
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            // Handle permission result if needed
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        enableEdgeToEdge()
        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemInDarkTheme) }

            SmartCampusCompanionAppTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var hasCompletedOnboarding by remember {
                        mutableStateOf(sharedPreferences.getBoolean("has_completed_onboarding", false))
                    }
                    var isLoggedIn by remember {
                        mutableStateOf(sharedPreferences.getBoolean("is_logged_in", false))
                    }
                    var isAdmin by remember {
                        mutableStateOf(sharedPreferences.getBoolean("is_admin", false))
                    }
                    var studentNumber by remember { mutableStateOf(sharedPreferences.getString("student_number", "") ?: "") }

                    val taskViewModel: TaskViewModel? = if (isLoggedIn && !isAdmin && studentNumber.isNotBlank()) {
                        val factory = TaskViewModelFactory(firebaseTaskRepository, studentNumber)
                        androidx.lifecycle.viewmodel.compose.viewModel(
                            key = "TaskVM_$studentNumber",
                            factory = factory
                        )
                    } else null

                    // Subscribe to announcements topic
                    LaunchedEffect(isLoggedIn) {
                        if (isLoggedIn) {
                            FirebaseMessaging.getInstance().subscribeToTopic("announcements")
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        android.util.Log.d("FCM", "Subscribed to announcements")
                                    } else {
                                        android.util.Log.e("FCM", "Subscription failed", task.exception)
                                    }
                                }
                            if (studentNumber.isNotBlank()) {
                                scheduleDeadlineWorker(studentNumber)
                            }
                            
                            // Listen for new announcements in Firestore to show local notifications
                            if (!isAdmin) {
                                // Set start time with a small buffer to avoid showing old ones
                                val startTime = System.currentTimeMillis() - 5000
                                FirebaseFirestore.getInstance().collection("announcements")
                                    .addSnapshotListener { snapshots, e ->
                                        if (e != null) return@addSnapshotListener
                                        
                                        for (dc in snapshots!!.documentChanges) {
                                            if (dc.type == DocumentChange.Type.ADDED) {
                                                val timestamp = dc.document.getLong("timestamp") ?: 0L
                                                // Only notify if it's a truly new announcement posted after app start
                                                if (timestamp > startTime) {
                                                    val title = dc.document.getString("title") ?: "New Announcement"
                                                    val content = dc.document.getString("content") ?: ""
                                                    android.util.Log.d("NotificationDebug", "Triggering notification for: $title")
                                                    showAnnouncementNotification(title, content)
                                                }
                                            }
                                        }
                                    }
                            }
                        }
                    }

                    var currentScreen by remember { mutableStateOf("Auth") }
                    var currentTab by remember { mutableStateOf(MainTab.Dashboard) }
                    var overlayScreen by remember { mutableStateOf<String?>(null) }
                    var selectedCollege by remember { mutableStateOf<College?>(null) }

                    when {
                        !hasCompletedOnboarding -> OnboardingScreen(
                            onGetStarted = {
                                sharedPreferences.edit { putBoolean("has_completed_onboarding", true) }
                                hasCompletedOnboarding = true
                            }
                        )
                        isLoggedIn -> {
                            if (isAdmin) {
                                when (overlayScreen) {
                                    "Announcements" -> AnnouncementScreen(
                                        onBack = { overlayScreen = null },
                                        viewModel = announcementViewModel,
                                        isAdmin = true
                                    )
                                    else -> AdminDashboardScreen(
                                        onNavigationItemClick = { item ->
                                            when (item) {
                                                "Logout" -> {
                                                    sharedPreferences.edit { 
                                                        putBoolean("is_logged_in", false)
                                                        putBoolean("is_admin", false)
                                                    }
                                                    isLoggedIn = false
                                                    isAdmin = false
                                                }
                                                "Manage Announcements" -> overlayScreen = "Announcements"
                                            }
                                        }
                                    )
                                }
                            } else {
                                Scaffold(
                                    modifier = Modifier.fillMaxSize(),
                                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                                    bottomBar = {
                                        NavigationBar {
                                            MainTab.entries.forEach { tab ->
                                                NavigationBarItem(
                                                    selected = currentTab == tab,
                                                    onClick = {
                                                        overlayScreen = null
                                                        currentTab = tab
                                                    },
                                                    icon = {
                                                        Icon(
                                                            imageVector = tab.icon,
                                                            contentDescription = tab.title
                                                        )
                                                    },
                                                    label = { Text(tab.title) },
                                                    colors = NavigationBarItemDefaults.colors()
                                                )
                                            }
                                        }
                                    }
                                ) { paddingValues ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(paddingValues)
                                    ) {
                                        AnimatedContent(
                                            targetState = overlayScreen,
                                            modifier = Modifier.fillMaxSize(),
                                            transitionSpec = {
                                                (fadeIn() + slideInHorizontally { it / 4 }) togetherWith
                                                        (fadeOut() + slideOutHorizontally { -it / 4 })
                                            },
                                            label = "overlay"
                                        ) { overlay ->
                                        when (overlay) {
                                            "Settings" -> SettingsScreen(
                                                isDarkTheme = isDarkTheme,
                                                onThemeChange = { isDarkTheme = it },
                                                onLogout = {
                                                    sharedPreferences.edit()
                                                        .putBoolean("is_logged_in", false)
                                                        .putBoolean("is_admin", false)
                                                        .putString("student_number", null)
                                                        .apply()
                                                    isLoggedIn = false
                                                    overlayScreen = null
                                                    studentNumber = ""
                                                },
                                                onBack = { overlayScreen = null }
                                            )
                                            "Announcements" -> AnnouncementScreen(
                                                onBack = { overlayScreen = null },
                                                viewModel = announcementViewModel,
                                                isAdmin = false
                                            )
                                            "CollegeList" -> CollegeListScreen(
                                                onCollegeClick = { college ->
                                                    selectedCollege = college
                                                    overlayScreen = "CollegeInfo"
                                                },
                                                onBackClick = { overlayScreen = null }
                                            )
                                            "CollegeInfo" -> selectedCollege?.let { college ->
                                                CollegeInfoScreen(
                                                    college = college,
                                                    onBackClick = { overlayScreen = "CollegeList" }
                                                )
                                            } ?: Box(Modifier.fillMaxSize())
                                            null -> {
                                                AnimatedContent(
                                                    targetState = currentTab,
                                                    modifier = Modifier.fillMaxSize(),
                                                    transitionSpec = {
                                                        (slideInHorizontally { width -> width / 4 } + fadeIn()) togetherWith
                                                                (slideOutHorizontally { width -> -width / 4 } + fadeOut())
                                                    },
                                                    label = "tab_content"
                                                ) { tab ->
                                                    when (tab) {
                                                        MainTab.Dashboard -> DashboardScreen(
                                                            surname = studentNumber,
                                                            upcomingTasks = taskViewModel?.tasks?.collectAsState()?.value ?: emptyList(),
                                                            announcementViewModel = announcementViewModel,
                                                            onNavigateToAnnouncements = {
                                                                overlayScreen = "Announcements"
                                                            },
                                                            onNavigateToTasks = {
                                                                currentTab = MainTab.Tasks
                                                            },
                                                            onNavigateToCampusInfo = {
                                                                overlayScreen = "CollegeList"
                                                            },
                                                            onNavigateToCollegeInfo = { college ->
                                                                selectedCollege = college
                                                                overlayScreen = "CollegeInfo"
                                                            },
                                                            onNavigateToProfile = {
                                                                currentTab = MainTab.Profile
                                                            },
                                                            onNavigateToSettings = {
                                                                overlayScreen = "Settings"
                                                            },
                                                            onNavigateToSchedule = {
                                                                currentTab = MainTab.Tasks
                                                            }
                                                        )
                                                        MainTab.Tasks -> taskViewModel?.let {
                                                            ScheduleScreen(
                                                                viewModel = it,
                                                                showBackButton = false
                                                            )
                                                        } ?: Box(Modifier.fillMaxSize())
                                                        MainTab.Profile -> {
                                                            ProfileScreen(
                                                                studentNumber = studentNumber,
                                                                onBack = { },
                                                                viewModel = profileViewModel,
                                                                showBackButton = false,
                                                                onSettingsClick = { overlayScreen = "Settings" }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        }
                                    }
                                }
                            }
                        }
                        currentScreen == "Register" -> RegisterScreen(
                            viewModel = loginViewModel,
                            onRegisterSuccess = { currentScreen = "Auth" },
                            onBack = { currentScreen = "Auth" }
                        )
                        currentScreen == "AdminLogin" -> AdminLoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = { user, isAdm ->
                                sharedPreferences.edit { 
                                    putBoolean("is_logged_in", true) 
                                    putBoolean("is_admin", true)
                                    putString("student_number", user)
                                }
                                studentNumber = user
                                isLoggedIn = true
                                isAdmin = true
                            },
                            onBackToStudentLogin = { currentScreen = "Auth" }
                        )
                        else -> LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = { studentNum, isAdm ->
                                sharedPreferences.edit { 
                                    putBoolean("is_logged_in", true) 
                                    putBoolean("is_admin", false)
                                    putString("student_number", studentNum)
                                }
                                studentNumber = studentNum
                                isLoggedIn = true
                                isAdmin = false
                            },
                            onRegisterClick = { currentScreen = "Register" },
                            onAdminLoginClick = { currentScreen = "AdminLogin" }
                        )
                    }
                }
            }
        }
    }

    private fun scheduleDeadlineWorker(studentNumber: String) {
        val data = Data.Builder()
            .putString("studentNumber", studentNumber)
            .build()

        val request = PeriodicWorkRequestBuilder<DeadlineWorker>(24, TimeUnit.HOURS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DeadlineWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun showAnnouncementNotification(title: String, content: String) {
        val channelId = "announcements_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Announcements", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
