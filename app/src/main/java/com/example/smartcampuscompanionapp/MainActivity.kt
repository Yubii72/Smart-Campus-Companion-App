package com.example.smartcampuscompanionapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.edit
import com.example.smartcampuscompanionapp.data.local.AppDatabase
import com.example.smartcampuscompanionapp.data.repository.StudentRepository
import com.example.smartcampuscompanionapp.ui.campus_info.College
import com.example.smartcampuscompanionapp.ui.campus_info.CollegeInfoScreen
import com.example.smartcampuscompanionapp.ui.campus_info.CollegeListScreen
import com.example.smartcampuscompanionapp.ui.dashboard.DashboardScreen
import com.example.smartcampuscompanionapp.ui.login.LoginScreen
import com.example.smartcampuscompanionapp.ui.login.LoginViewModel
import com.example.smartcampuscompanionapp.ui.login.LoginViewModelFactory
import com.example.smartcampuscompanionapp.ui.settings.SettingsScreen
import com.example.smartcampuscompanionapp.ui.theme.SmartCampusCompanionAppTheme

class MainActivity : ComponentActivity() {
    
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { StudentRepository(database.studentDao()) }
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Seed initial data
        loginViewModel.seedDataIfEmpty()
        
        val sharedPreferences = getSharedPreferences("smart_campus_prefs", Context.MODE_PRIVATE)
        
        enableEdgeToEdge()
        setContent {
            SmartCampusCompanionAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isLoggedIn by remember { 
                        mutableStateOf(sharedPreferences.getBoolean("is_logged_in", false)) 
                    }
                    
                    var currentScreen by remember { mutableStateOf("Dashboard") }
                    var selectedCollege by remember { mutableStateOf<College?>(null) }

                    if (isLoggedIn) {
                        when (currentScreen) {
                            "Dashboard" -> DashboardScreen(
                                username = "2024-0001", // Using the student number from seeded data
                                onNavigationItemClick = { item ->
                                    when (item) {
                                        "Settings" -> currentScreen = "Settings"
                                        "Campus Information" -> currentScreen = "CollegeList"
                                    }
                                }
                            )
                            "Settings" -> SettingsScreen(
                                onLogout = {
                                    sharedPreferences.edit { putBoolean("is_logged_in", false) }
                                    isLoggedIn = false
                                    currentScreen = "Dashboard"
                                },
                                onBack = {
                                    currentScreen = "Dashboard"
                                }
                            )
                            "CollegeList" -> CollegeListScreen(
                                onCollegeClick = { college ->
                                    selectedCollege = college
                                    currentScreen = "CollegeInfo"
                                },
                                onBackClick = {
                                    currentScreen = "Dashboard"
                                }
                            )
                            "CollegeInfo" -> CollegeInfoScreen(
                                college = selectedCollege!!,
                                onBackClick = {
                                    currentScreen = "CollegeList"
                                }
                            )
                        }
                    } else {
                        LoginScreen(
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
