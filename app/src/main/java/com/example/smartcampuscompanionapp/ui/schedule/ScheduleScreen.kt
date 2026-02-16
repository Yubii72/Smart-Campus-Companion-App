package com.example.smartcampuscompanionapp.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartcampuscompanionapp.ui.theme.SmartCampusCompanionAppTheme

data class Task(
    val id: Int,
    val title: String,
    val dueDate: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(onBack: () -> Unit = {}) {
    val tasks = remember {
        mutableStateListOf(
            Task(1, "Complete Project Proposal", "2024-08-15", "Finish the proposal for the new app feature."),
            Task(2, "Study for Midterms", "2024-08-20", "Cover chapters 4, 5, and 6."),
            Task(3, "Team Meeting", "2024-08-12", "Discuss project progress and next steps.")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task & Schedule Manager") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Add new task */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DateTimePicker()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks) { task ->
                    TaskItem(task = task, onEdit = {}, onDelete = {})
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Date") },
            placeholder = { Text("Select Date") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Date"
                )
            },
            modifier = Modifier.weight(1f),
            readOnly = true
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Time") },
            placeholder = { Text("Select Time") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Time"
                )
            },
            modifier = Modifier.weight(1f),
            readOnly = true
        )
    }
}


@Composable
fun TaskItem(
    task: Task,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Due: ${task.dueDate}")
                 Spacer(modifier = Modifier.height(4.dp))
                Text(text = task.description)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Task")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Task")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleScreenPreview() {
    SmartCampusCompanionAppTheme {
        ScheduleScreen()
    }
}
