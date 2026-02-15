package com.example.smartcampuscompanionapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.smartcampuscompanionapp.ui.campus_info.College
import com.example.smartcampuscompanionapp.ui.campus_info.CollegeInfoScreen
import com.example.smartcampuscompanionapp.ui.campus_info.CollegeListScreen
import com.example.smartcampuscompanionapp.ui.dashboard.DashboardScreen
import com.example.smartcampuscompanionapp.ui.login.LoginScreen
import com.example.smartcampuscompanionapp.ui.settings.SettingsScreen
import com.example.smartcampuscompanionapp.ui.theme.SmartCampusCompanionAppTheme
import androidx.core.content.edit

class MainActivity : ComponentActivity() {
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
                    var isLoggedIn by remember {
                        mutableStateOf(sharedPreferences.getBoolean("is_logged_in", false))
                    }
                    //for dashboard
                    var currentScreen by remember { mutableStateOf("Dashboard") }
                    var selectedCollege by remember { mutableStateOf<College?>(null) }

                    if (isLoggedIn) {
                        when (currentScreen) {
                            "Dashboard" -> DashboardScreen(
                                username = "admin",
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
                        LoginScreen(onLoginSuccess = {
                            sharedPreferences.edit { putBoolean("is_logged_in", true) }
                            isLoggedIn = true
                        })
                    }
                }
            }
        }
    }
}