package com.example.smartcampuscompanionapp.ui.student

// Import for Time Picker dialog
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartcampuscompanionapp.ui.theme.SmartCampusCompanionAppTheme
import java.text.SimpleDateFormat
import java.util.*

// Data class representing a task
data class Task(
    val id: String = UUID.randomUUID().toString(), // Unique ID for each task
    val studentNumber: String = "", // Added to identify the owner of the task
    val title: String, // Task title
    val dueDate: String, // Task due date
    val description: String // Task description
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    tasks: MutableList<Task>,
    studentNumber: String, // Added parameter
    onBack: () -> Unit = {},
    showBackButton: Boolean = true
) {

    // Dialog visibility state
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    // Currently editing task
    var editingTask by remember { mutableStateOf<Task?>(null) }
    // Sorting order state
    var sortOrder by remember { mutableStateOf(SortOrder.NONE) }

    // Search query text
    var searchQuery by remember { mutableStateOf("") }
    // Checkbox for filtering today's tasks
    var filterToday by remember { mutableStateOf(false) }

    // Scaffold layout containing top bar, FAB, and content
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Task & Schedule Manager",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    SortDropDown(sortOrder = sortOrder, onSortOrderChange = { sortOrder = it })
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingTask = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->

        // Get today's date for filtering
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Filter tasks based on logged-in student and search query
        val userTasks = tasks.filter { it.studentNumber == studentNumber }
        
        val filteredTasks = userTasks.filter {
            it.title.contains(searchQuery, true) || it.description.contains(searchQuery, true)
        }.filter {
            // Filter only today's tasks if checkbox enabled
            if (!filterToday) true else it.dueDate.startsWith(todayDate)
        }

        // Sort tasks depending on selected order
        val sortedTasks = when (sortOrder) {
            SortOrder.ASCENDING -> filteredTasks.sortedBy { it.dueDate }
            SortOrder.DESCENDING -> filteredTasks.sortedByDescending { it.dueDate }
            SortOrder.NONE -> filteredTasks
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Search and filter UI section
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search tasks") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Checkbox(
                        checked = filterToday,
                        onCheckedChange = { filterToday = it }
                    )
                    Text(
                        "Show today's tasks only",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Tasks section header
            Text(
                text = "MY TASKS",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
            )

            // List of tasks
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Display empty state if no tasks found
                if (sortedTasks.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EventNote,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No tasks yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap + to add your first task",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Display each task
                items(sortedTasks) { task ->
                    TaskItem(
                        task = task,
                        onEdit = {
                            editingTask = task
                            showDialog = true
                        },
                        onDelete = {
                            taskToDelete = task
                            showDeleteConfirmDialog = true
                        }
                    )
                }
            }
        }
    }

    // Dialog for adding/editing task
    if (showDialog) {
        TaskDialog(
            task = editingTask,
            onDismiss = { showDialog = false },
            onSave = { task ->
                // Ensure new task is assigned to current student
                val taskWithUser = if (editingTask == null) {
                    task.copy(studentNumber = studentNumber)
                } else {
                    task
                }

                if (editingTask == null) {
                    tasks.add(taskWithUser)
                } else {
                    val index = tasks.indexOfFirst { it.id == task.id }
                    if (index != -1) tasks[index] = taskWithUser
                }
                showDialog = false
            }
        )
    }

    if (showDeleteConfirmDialog) {
        DeleteConfirmationDialog(
            task = taskToDelete,
            onConfirm = {
                taskToDelete?.let { tasks.remove(it) }
                showDeleteConfirmDialog = false
                taskToDelete = null
            },
            onDismiss = {
                showDeleteConfirmDialog = false
                taskToDelete = null
            }
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    task: Task?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (task != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete the task \"${task.title}\"?") },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
        )
    }
}


// Enum class for sorting options
enum class SortOrder {
    ASCENDING, DESCENDING, NONE
}

@Composable
fun SortDropDown(sortOrder: SortOrder, onSortOrderChange: (SortOrder) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    // Dropdown menu container
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.Sort, contentDescription = "Sort Tasks")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Sort Ascending") }, onClick = {
                onSortOrderChange(SortOrder.ASCENDING); expanded = false
            })
            DropdownMenuItem(text = { Text("Sort Descending") }, onClick = {
                onSortOrderChange(SortOrder.DESCENDING); expanded = false
            })
            DropdownMenuItem(text = { Text("Clear Sort") }, onClick = {
                onSortOrderChange(SortOrder.NONE); expanded = false
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    task: Task?,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    // Title input state
    var title by remember { mutableStateOf(task?.title ?: "") }
    // Due date state - format yyyy-MM-dd for storage
    var dueDate by remember(task) {
        mutableStateOf(task?.dueDate?.take(10) ?: "")
    }
    // Description input state
    var description by remember { mutableStateOf(task?.description ?: "") }

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val (initYear, initMonth, initDay) = remember(dueDate) {
        val cal = Calendar.getInstance()
        if (dueDate.isNotBlank()) {
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dueDate)?.let {
                    cal.time = it
                }
            } catch (_: Exception) { }
        }
        Triple(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
    }
    val datePickerDialog = remember(initYear, initMonth, initDay) {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            },
            initYear, initMonth, initDay
        )
    }

    // Dialog UI
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Add Task" else "Edit Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { datePickerDialog.show() }
                ) {
                    OutlinedTextField(
                        value = if (dueDate.isBlank()) "" else dueDate,
                        onValueChange = { },
                        label = { Text("Due Date") },
                        placeholder = { Text("Tap to select date") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                if (title.isBlank()) return@Button
                if (dueDate.isBlank()) return@Button

                // Initial creation doesn't have studentNumber, it will be set by the caller (onSave)
                val newTask = task?.copy(title = title, dueDate = dueDate, description = description)
                    ?: Task(title = title, dueDate = dueDate, description = description)

                onSave(newTask)
            },
                shape = RoundedCornerShape(12.dp)
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun TaskItem(task: Task, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        "Due: ${task.dueDate}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleScreenPreview() {
    SmartCampusCompanionAppTheme {
        ScheduleScreen(tasks = mutableListOf(), studentNumber = "12345")
    }
}
