package com.example.smartcampuscompanionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.smartcampuscompanionapp.ui.dashboard.DashboardScreen
import com.example.smartcampuscompanionapp.ui.login.LoginScreen
import com.example.smartcampuscompanionapp.ui.schedule.ScheduleScreen
import com.example.smartcampuscompanionapp.ui.settings.SettingsScreen
import com.example.smartcampuscompanionapp.ui.theme.SmartCampusCompanionAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartCampusCompanionAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isLoggedIn by remember { mutableStateOf(false) }
                    var currentScreen by remember { mutableStateOf("Dashboard") }

                    if (isLoggedIn) {
                        when (currentScreen) {
                            "Dashboard" -> DashboardScreen(
                                username = "admin",
                                onNavigationItemClick = { item ->
                                    currentScreen = item
                                }
                            )
                            "Settings" -> SettingsScreen(
                                onLogout = {
                                    isLoggedIn = false
                                    currentScreen = "Dashboard"
                                },
                                onBack = {
                                    currentScreen = "Dashboard"
                                }
                            )
                            "Schedule" -> ScheduleScreen(
                                onBack = {
                                    currentScreen = "Dashboard"
                                }
                            )
                        }
                    } else {
                        LoginScreen(onLoginSuccess = {
                            isLoggedIn = true
                        })
                    }
                }
            }
        }
    }
}