package com.example.smartcampuscompanionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.smartcampuscompanionapp.ui.dashboard.DashboardScreen
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
                    DashboardScreen(
                        username = "Student",
                        onNavigationItemClick = { item ->
                            // Handle navigation item clicks
                            // This will be implemented when navigation is set up
                        }
                    )
                }
            }
        }
    }
}