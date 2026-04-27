package com.example.smartcampuscompanionapp.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanionapp.data.local.entities.Student
import com.example.smartcampuscompanionapp.data.repository.FirebaseStudentRepository
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRecordsScreen(
    firebaseRepository: FirebaseStudentRepository = FirebaseStudentRepository(),
    onBack: () -> Unit
) {
    val students by firebaseRepository.getAllStudents().collectAsState(initial = null)
    var searchQuery by remember { mutableStateOf("") }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    
    // Timer to stop showing the loading spinner after 5 seconds if no data is found
    var showLoading by remember { mutableStateOf(true) }
    LaunchedEffect(students) {
        if (students != null) {
            showLoading = false
        } else {
            delay(5000)
            showLoading = false
        }
    }

    val filteredStudents = (students ?: emptyList()).filter {
        it.lastName.contains(searchQuery, ignoreCase = true) ||
        it.firstName.contains(searchQuery, ignoreCase = true) ||
        it.studentNumber.contains(searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Records (Cloud)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    titleContentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Search Firebase students...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )

            when {
                students == null && showLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.error)
                    }
                }
                (students == null || students!!.isEmpty()) && !showLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.PersonOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No students found in Firebase.",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Text(
                            "Ensure students have registered on this or another device and Firestore rules allow reading.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredStudents) { student ->
                            StudentItem(student = student, onClick = { selectedStudent = student })
                        }
                    }
                }
            }
        }

        if (selectedStudent != null) {
            StudentDetailsDialog(
                student = selectedStudent!!,
                onDismiss = { selectedStudent = null }
            )
        }
    }
}

@Composable
fun StudentItem(student: Student, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = student.lastName.ifEmpty { "?" }.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("${student.lastName}, ${student.firstName}", fontWeight = FontWeight.Bold)
                Text("SN: ${student.studentNumber}", style = MaterialTheme.typography.bodySmall)
                Text("${student.college} | ${student.program}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun StudentDetailsDialog(student: Student, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        title = {
            Text("${student.firstName} ${student.lastName}", fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    DetailRow("Student Number", student.studentNumber)
                    DetailRow("College", student.college)
                    DetailRow("Program", student.program)
                    DetailRow("Year & Section", "${student.yearLevel} - ${student.section}")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow("Email", student.primaryEmailAddress)
                    DetailRow("Phone", student.primaryMobileNumber)
                    DetailRow("Address", student.presentAddress)
                    DetailRow("Birthday", student.dateOfBirth)
                    DetailRow("Sex", student.sexAtBirth)
                    DetailRow("Nationality", student.nationality)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow("Father's Name", student.fathersName)
                    DetailRow("Mother's Name", student.mothersName)
                }
            }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        Text(value.ifBlank { "N/A" }, style = MaterialTheme.typography.bodyMedium)
    }
}
