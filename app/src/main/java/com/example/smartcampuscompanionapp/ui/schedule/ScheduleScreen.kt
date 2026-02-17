package com.example.smartcampuscompanionapp.ui.schedule

// Import for Time Picker dialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartcampuscompanionapp.ui.theme.SmartCampusCompanionAppTheme
import java.text.SimpleDateFormat
import java.util.*

// Data class representing a task
data class Task(
    val id: String = UUID.randomUUID().toString(), // Unique ID for each task
    val title: String, // Task title
    val dueDate: String, // Task due date
    val description: String // Task description
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(onBack: () -> Unit) {

    // List of tasks stored in memory
    val tasks = remember {
        mutableStateListOf(
            Task(title = "Complete Project Proposal", dueDate = "2024-08-15", description = "Finish the proposal."),
            Task(title = "Study for Midterms", dueDate = "2024-08-20", description = "Cover chapters 4-6."),
            Task(title = "Team Meeting", dueDate = "2024-08-12", description = "Discuss project progress.")
        )
    }

    // Dialog visibility state
    var showDialog by remember { mutableStateOf(false) }
    // Currently editing task
    var editingTask by remember { mutableStateOf<Task?>(null) }
    // Sorting order state
    var sortOrder by remember { mutableStateOf(SortOrder.NONE) }
    // Selected date from picker
    var selectedDate by remember { mutableStateOf("") }
    // Selected time from picker
    var selectedTime by remember { mutableStateOf("") }

    // Search query text
    var searchQuery by remember { mutableStateOf("") }
    // Checkbox for filtering today's tasks
    var filterToday by remember { mutableStateOf(false) }

    // Scaffold layout containing top bar, FAB, and content
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task & Schedule Manager") }, // App title
                navigationIcon = {
                    IconButton(onClick = onBack) { // Back button
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Sorting dropdown menu
                    SortDropDown(sortOrder = sortOrder, onSortOrderChange = { sortOrder = it })
                }
            )
        },
        floatingActionButton = {
            // Floating button to add new task
            FloatingActionButton(onClick = {
                editingTask = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->

        // Get today's date for filtering
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Filter tasks based on search query
        val filteredTasks = tasks.filter {
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
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search tasks") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = filterToday,
                        onCheckedChange = { filterToday = it }
                    )
                    Text("Show today's tasks only")
                }
            }

            // List of tasks
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Date and Time picker component
                item {
                    DateTimePicker(selectedDate, selectedTime) { date, time ->
                        selectedDate = date
                        selectedTime = time
                    }
                }

                // Display message if no tasks found
                if (sortedTasks.isEmpty()) {
                    item {
                        Text(
                            text = "No tasks found.",
                            style = MaterialTheme.typography.bodyLarge
                        )
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
                        onDelete = { tasks.remove(task) }
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
                if (editingTask == null) {
                    tasks.add(task)
                } else {
                    val index = tasks.indexOfFirst { it.id == task.id }
                    if (index != -1) tasks[index] = task
                }
                showDialog = false
                selectedDate = ""
                selectedTime = ""
            },
            initialDate = selectedDate,
            initialTime = selectedTime
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
    onSave: (Task) -> Unit,
    initialDate: String,
    initialTime: String
) {
    // Title input state
    var title by remember { mutableStateOf(task?.title ?: "") }
    // Due date state
    var dueDate by remember(task, initialDate, initialTime) {
        mutableStateOf(task?.dueDate ?: "$initialDate $initialTime".trim())
    }
    // Description input state
    var description by remember { mutableStateOf(task?.description ?: "") }

    // Dialog UI
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Add Task" else "Edit Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isBlank()) return@Button
                if (dueDate.isBlank()) return@Button

                val newTask = task?.copy(title = title, dueDate = dueDate, description = description)
                    ?: Task(title = title, dueDate = dueDate, description = description)

                onSave(newTask)
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun DateTimePicker(
    selectedDate: String,
    selectedTime: String,
    onDateTimeSelected: (String, String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Date picker dialog
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.set(year, month, dayOfMonth)
            onDateTimeSelected(sdf.format(calendar.time), selectedTime)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Time picker dialog
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay: Int, minute: Int ->
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            onDateTimeSelected(selectedDate, sdf.format(calendar.time))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    // Row containing date and time fields
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Date field
        Box(
            modifier = Modifier.weight(1f).clickable { datePickerDialog.show() }
        ) {
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                label = { Text("Date") },
                placeholder = { Text("Select Date") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Date") },
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Time field
        Box(
            modifier = Modifier.weight(1f).clickable { timePickerDialog.show() }
        ) {
            OutlinedTextField(
                value = selectedTime,
                onValueChange = {},
                label = { Text("Time") },
                placeholder = { Text("Select Time") },
                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = "Time") },
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TaskItem(task: Task, onEdit: () -> Unit, onDelete: () -> Unit) {
    // Card layout for each task
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, fontWeight = FontWeight.Bold)
                Text("Due: ${task.dueDate}")
                Text(task.description)
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit") }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleScreenPreview() {
    SmartCampusCompanionAppTheme {
        ScheduleScreen(onBack = {})
    }
}
