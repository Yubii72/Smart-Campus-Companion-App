package com.example.smartcampuscompanionapp.ui.admin

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanionapp.data.local.entities.Student
import com.example.smartcampuscompanionapp.data.repository.FirebaseStudentRepository
import com.example.smartcampuscompanionapp.ui.theme.AdminArgonBubbleColor
import com.example.smartcampuscompanionapp.ui.theme.AdminArgonGradientEnd
import com.example.smartcampuscompanionapp.ui.theme.AdminArgonGradientStart
import kotlinx.coroutines.delay

/**
 * STUDENT RECORDS SCREEN
 * Displays registered users from Firebase Firestore
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRecordsScreen(
    firebaseRepository: FirebaseStudentRepository = FirebaseStudentRepository(),
    onBack: () -> Unit
) {
    // CLOUD DATA STREAM
    val students by firebaseRepository.getAllStudents().collectAsState(initial = null)
    var searchQuery by remember { mutableStateOf("") }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    
    // UI LOADING STATE
    var showLoading by remember { mutableStateOf(true) }
    LaunchedEffect(students) {
        if (students != null) {
            showLoading = false
        } else {
            delay(5000)
            showLoading = false
        }
    }

    // SEARCH FILTER LOGIC
    val filteredStudents = (students ?: emptyList()).filter {
        it.lastName.contains(searchQuery, ignoreCase = true) ||
        it.firstName.contains(searchQuery, ignoreCase = true) ||
        it.studentNumber.contains(searchQuery)
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        
        // ARGON HEADER SECTION
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(AdminArgonGradientStart, AdminArgonGradientEnd)
                    )
                )
        ) {
            // GLASSMORPHISM EFFECT
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .offset(x = (-30).dp, y = (-30).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .blur(25.dp)
            )

            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        "Student Records",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                // INTEGRATED SEARCH BAR
                Surface(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search Students...", color = Color.White.copy(alpha = 0.7f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // MAIN LIST AREA
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                // LOADING VIEW
                students == null && showLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                // EMPTY VIEW
                (students == null || students!!.isEmpty()) && !showLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.PersonOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        Text("No students found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                // DATA LIST
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredStudents) { student ->
                            StudentItem(student = student, onClick = { selectedStudent = student })
                        }
                    }
                }
            }
        }
    }

    // PROFILE DETAILS POPUP
    if (selectedStudent != null) {
        StudentDetailsDialog(student = selectedStudent!!, onDismiss = { selectedStudent = null })
    }
}

/**
 * LIST ITEM COMPONENT
 */
@Composable
fun StudentItem(student: Student, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                Box(contentAlignment = Alignment.Center) {
                    Text(student.firstName.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("${student.firstName} ${student.lastName}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(student.primaryEmailAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

/**
 * PROFILE DIALOG COMPONENT
 */
@Composable
fun StudentDetailsDialog(student: Student, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = onDismiss) { Text("Close") } },
        title = { Text("${student.firstName} Profile", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                DetailRow("UID", student.studentNumber)
                DetailRow("Email", student.primaryEmailAddress)
                DetailRow("Course", student.program)
            }
        }
    )
}

/**
 * REUSABLE DETAIL ROW
 */
@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value.ifBlank { "N/A" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}
