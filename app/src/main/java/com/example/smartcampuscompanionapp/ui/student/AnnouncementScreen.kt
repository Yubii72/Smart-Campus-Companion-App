package com.example.smartcampuscompanionapp.ui.student

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartcampuscompanionapp.data.local.entities.Announcement
import com.example.smartcampuscompanionapp.ui.viewmodel.AnnouncementViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementScreen(
    onBack: () -> Unit,
    viewModel: AnnouncementViewModel,
    isAdmin: Boolean = false
) {
    val announcements by viewModel.allAnnouncements.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var announcementToEdit by remember { mutableStateOf<Announcement?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Announcements") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Announcement")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (announcements.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No announcements yet.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(announcements) { announcement ->
                        AnnouncementCard(
                            announcement = announcement,
                            isAdmin = isAdmin,
                            onEdit = { announcementToEdit = announcement },
                            onDelete = { viewModel.deleteAnnouncement(announcement) },
                            onMarkAsRead = { viewModel.markAsRead(announcement) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AnnouncementDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { title, content ->
                    val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
                    viewModel.addAnnouncement(title, content, date)
                    showAddDialog = false
                }
            )
        }

        announcementToEdit?.let { announcement ->
            AnnouncementDialog(
                initialTitle = announcement.title,
                initialContent = announcement.content,
                onDismiss = { announcementToEdit = null },
                onConfirm = { title, content ->
                    viewModel.updateAnnouncement(announcement.copy(title = title, content = content))
                    announcementToEdit = null
                }
            )
        }
    }
}

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    isAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkAsRead: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(!isAdmin && !announcement.isRead) { onMarkAsRead() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!isAdmin && !announcement.isRead) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = announcement.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (!isAdmin && !announcement.isRead) FontWeight.ExtraBold else FontWeight.Bold
                        )
                        if (!isAdmin && !announcement.isRead) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text("New", color = Color.White, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    Text(
                        text = announcement.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                if (isAdmin) {
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "By: ${announcement.author}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun AnnouncementDialog(
    initialTitle: String = "",
    initialContent: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialTitle.isEmpty()) "New Announcement" else "Edit Announcement") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank() && content.isNotBlank()) onConfirm(title, content) },
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
