package com.example.smartcampuscompanionapp.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smartcampuscompanionapp.ui.viewmodel.ProfileViewModel
import com.example.smartcampuscompanionapp.ui.viewmodel.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupProfileScreen(
    studentNumber: String,
    viewModel: ProfileViewModel,
    onComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(studentNumber) {
        viewModel.loadProfile(studentNumber)
    }

    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Success && viewModel.isEditMode == false) {
             // If we just saved successfully, we can complete
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Setup Your Profile", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState is ProfileUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Welcome! Please complete your enrollment details to get started.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    ProfileSectionHeader("Academic Information", Icons.Default.Assignment)
                    
                    OutlinedTextField(
                        value = viewModel.studentNumber,
                        onValueChange = { viewModel.studentNumber = it },
                        label = { Text("Student Number *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = viewModel.college,
                        onValueChange = { viewModel.college = it },
                        label = { Text("College *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = viewModel.program,
                        onValueChange = { viewModel.program = it },
                        label = { Text("Program / Course *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = viewModel.yearLevel,
                        onValueChange = { viewModel.yearLevel = it },
                        label = { Text("Year Level *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = viewModel.section,
                        onValueChange = { viewModel.section = it },
                        label = { Text("Section *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ProfileSectionHeader("Personal Information", Icons.Default.Info)
                    
                    OutlinedTextField(
                        value = viewModel.sexAtBirth,
                        onValueChange = { viewModel.sexAtBirth = it },
                        label = { Text("Sex at Birth") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = viewModel.nationality,
                        onValueChange = { viewModel.nationality = it },
                        label = { Text("Nationality") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = {
                            if (viewModel.college.isNotBlank() && viewModel.program.isNotBlank()) {
                                viewModel.saveProfile()
                                onComplete()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = viewModel.college.isNotBlank() && viewModel.program.isNotBlank()
                    ) {
                        Text("Finish Setup", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onComplete) {
                        Text("Skip for now")
                    }
                }
            }
        }
    }
}
