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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartcampuscompanionapp.data.local.AppDatabase
import com.example.smartcampuscompanionapp.data.repository.StudentRepository
import com.example.smartcampuscompanionapp.ui.campus_info.College
import com.example.smartcampuscompanionapp.ui.campus_info.CollegeInfoScreen
import com.example.smartcampuscompanionapp.ui.campus_info.CollegeListScreen
import com.example.smartcampuscompanionapp.ui.dashboard.DashboardScreen
import com.example.smartcampuscompanionapp.ui.login.LoginScreen
import com.example.smartcampuscompanionapp.ui.onboarding.OnboardingScreen
import com.example.smartcampuscompanionapp.ui.login.LoginViewModel
import com.example.smartcampuscompanionapp.ui.login.LoginViewModelFactory
import com.example.smartcampuscompanionapp.ui.profile.ProfileScreen
import com.example.smartcampuscompanionapp.ui.profile.ProfileViewModel
import com.example.smartcampuscompanionapp.ui.schedule.ScheduleScreen
import com.example.smartcampuscompanionapp.ui.schedule.Task
import com.example.smartcampuscompanionapp.ui.settings.SettingsScreen
import com.example.smartcampuscompanionapp.ui.theme.SmartCampusCompanionAppTheme

enum class MainTab(val title: String, val icon: ImageVector) {
    Dashboard("Dashboard", Icons.Default.Home),
    Tasks("Tasks", Icons.Default.CalendarToday),
    Profile("Profile", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { StudentRepository(database.studentDao()) }
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("smart_campus_prefs", Context.MODE_PRIVATE)

        enableEdgeToEdge()
        setContent {
            SmartCampusCompanionAppTheme {
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
                    var studentNumber by remember { mutableStateOf(sharedPreferences.getString("student_number", "")) }

                    var currentTab by remember { mutableStateOf(MainTab.Dashboard) }
                    var overlayScreen by remember { mutableStateOf<String?>(null) }
                    var selectedCollege by remember { mutableStateOf<College?>(null) }

                    val tasks = remember {
                        mutableStateListOf(
                            Task(title = "Complete Project Proposal", dueDate = "2024-08-15", description = "Finish the proposal."),
                            Task(title = "Study for Midterms", dueDate = "2024-08-20", description = "Cover chapters 4-6."),
                            Task(title = "Team Meeting", dueDate = "2024-08-12", description = "Discuss project progress.")
                        )
                    }

                    when {
                        !hasCompletedOnboarding -> OnboardingScreen(
                            onGetStarted = {
                                sharedPreferences.edit { putBoolean("has_completed_onboarding", true) }
                                hasCompletedOnboarding = true
                            }
                        )
                        isLoggedIn -> {
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
                                            onLogout = {
                                                sharedPreferences.edit()
                                                    .putBoolean("is_logged_in", false)
                                                    .putString("student_number", null)
                                                    .apply()
                                                isLoggedIn = false
                                                overlayScreen = null
                                            },
                                            onBack = { overlayScreen = null }
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
                                                        upcomingTasks = tasks,
                                                        onNavigateToAnnouncements = {
                                                            overlayScreen = "CollegeList"
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
                                                        showBackButton = false
                                                    )
                                                    MainTab.Profile -> {
                                                        val profileViewModel: ProfileViewModel by viewModels {
                                                            object : ViewModelProvider.Factory {
                                                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                                                    return ProfileViewModel(repository) as T
                                                                }
                                                            }
                                                        }
                                                        ProfileScreen(
                                                            studentNumber = studentNumber ?: "",
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
                        else -> LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = {
                                sharedPreferences.edit { putBoolean("is_logged_in", true) }
                                isLoggedIn = true
                            }
                        )
                    }
                }
            }
        }
    }
}
