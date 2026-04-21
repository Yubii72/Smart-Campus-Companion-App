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
import com.example.smartcampuscompanionapp.data.local.AppDatabase
import com.example.smartcampuscompanionapp.data.repository.AnnouncementRepository
import com.example.smartcampuscompanionapp.data.repository.StudentRepository
import com.example.smartcampuscompanionapp.ui.admin.AdminDashboardScreen
import com.example.smartcampuscompanionapp.ui.admin.AdminLoginScreen
import com.example.smartcampuscompanionapp.ui.campus_info.College
import com.example.smartcampuscompanionapp.ui.campus_info.CollegeInfoScreen
import com.example.smartcampuscompanionapp.ui.campus_info.CollegeListScreen
import com.example.smartcampuscompanionapp.ui.onboarding.OnboardingScreen
import com.example.smartcampuscompanionapp.ui.student.ScheduleScreen
import com.example.smartcampuscompanionapp.ui.student.Task
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

enum class MainTab(val title: String, val icon: ImageVector) {
    Dashboard("Dashboard", Icons.Default.Home),
    Tasks("Tasks", Icons.Default.CalendarToday),
    Profile("Profile", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val studentRepository by lazy { StudentRepository(database.studentDao()) }
    private val announcementRepository by lazy { AnnouncementRepository(database.announcementDao()) }
    
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(studentRepository)
    }
    
    private val announcementViewModel: AnnouncementViewModel by viewModels {
        AnnouncementViewModelFactory(announcementRepository)
    }
    
    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(studentRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("smart_campus_prefs", Context.MODE_PRIVATE)

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

                    var currentScreen by remember { mutableStateOf("Auth") }
                    var currentTab by remember { mutableStateOf(MainTab.Dashboard) }
                    var overlayScreen by remember { mutableStateOf<String?>(null) }
                    var selectedCollege by remember { mutableStateOf<College?>(null) }

                    // Shared list of tasks for demo purposes. 
                    // In a real app, these would come from a Database linked to the studentNumber.
                    val tasks = remember {
                        mutableStateListOf<Task>()
                    }

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
                                                            // Pass filtered tasks to Dashboard if needed for "Upcoming Tasks"
                                                            upcomingTasks = tasks.filter { it.studentNumber == studentNumber },
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
                                                        MainTab.Tasks -> ScheduleScreen(
                                                            tasks = tasks,
                                                            studentNumber = studentNumber,
                                                            showBackButton = false
                                                        )
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
}
