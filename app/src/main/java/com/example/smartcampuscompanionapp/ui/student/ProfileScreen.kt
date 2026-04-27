package com.example.smartcampuscompanionapp.ui.student

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.smartcampuscompanionapp.R
import com.example.smartcampuscompanionapp.ui.viewmodel.ProfileViewModel
import com.example.smartcampuscompanionapp.ui.viewmodel.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    studentNumber: String,
    onBack: () -> Unit,
    viewModel: ProfileViewModel,
    showBackButton: Boolean = true,
    onSettingsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.profileImageUrl = it.toString()
        }
    }

    // Load profile data when screen opens
    LaunchedEffect(studentNumber) {
        viewModel.loadProfile(studentNumber)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (uiState is ProfileUiState.Success || uiState is ProfileUiState.Idle) {
                        if (!viewModel.isEditMode) {
                            IconButton(onClick = onSettingsClick) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            }
                            Button(
                                onClick = { viewModel.isEditMode = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Edit Detail")
                            }
                        } else {
                            TextButton(onClick = { viewModel.saveProfile() }) {
                                Text("Save", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (uiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProfileUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Text((uiState as ProfileUiState.Error).message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadProfile(studentNumber) }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // AVATAR IN CENTER
                        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.padding(vertical = 16.dp)) {
                            if (viewModel.profileImageUrl != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(viewModel.profileImageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Student Avatar",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(R.drawable.irang)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.irang),
                                    contentDescription = "Student Avatar",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            if (viewModel.isEditMode) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .clickable { imagePickerLauncher.launch("image/*") }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = "Change Avatar",
                                        tint = Color.White,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }

                        if (!viewModel.isEditMode) {
                            // OVERVIEW MODE
                            OverviewSection(
                                studentNumber = viewModel.studentNumber,
                                sexAtBirth = viewModel.sexAtBirth,
                                nationality = viewModel.nationality,
                                dateOfBirth = viewModel.dateOfBirth,
                                presentProvince = viewModel.presentProvince,
                                presentZIP = viewModel.presentZip,
                                presentCity = viewModel.presentCity,
                                presentBarangay = viewModel.presentBarangay,
                                presentHouse = viewModel.presentHouse,
                                primaryMobileNumber = viewModel.primaryMobileNumber,
                                primaryEmailAddress = viewModel.primaryEmailAddress,
                                fatherFirstName = viewModel.fatherFirstName,
                                fatherMiddleName = viewModel.fatherMiddleName,
                                fatherLastName = viewModel.fatherLastName,
                                motherFirstName = viewModel.motherFirstName,
                                motherMiddleName = viewModel.middleNameMother,
                                motherLastName = viewModel.motherLastName,
                                college = viewModel.college,
                                program = viewModel.program,
                                curriculum = viewModel.curriculum,
                                yearLevel = viewModel.yearLevel,
                                section = viewModel.section
                            )
                        } else {
                            // EDIT MODE
                            EditSection(viewModel)
                        }
                    }
                }
            }
        }

        if (uiState is ProfileUiState.Loading && viewModel.isEditMode) {
            Dialog(
                onDismissRequest = { },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Saving...", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun OverviewSection(
    studentNumber: String, sexAtBirth: String,
    nationality: String, dateOfBirth: String,
    presentProvince: String, presentZIP: String, presentCity: String, presentBarangay: String, presentHouse: String,
    primaryMobileNumber: String, primaryEmailAddress: String, fatherFirstName: String, fatherMiddleName: String,
    fatherLastName: String, motherFirstName: String, motherMiddleName: String,
    motherLastName: String,
    college: String, program: String, curriculum: String, yearLevel: String, section: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OverviewDetail("Student Number", studentNumber)
        OverviewDetail("Sex at Birth", sexAtBirth)
        OverviewDetail("Nationality", nationality)
        OverviewDetail("Date of Birth", dateOfBirth)

        ProfileSectionHeader("Contact Information", null)
        OverviewDetail("Present Address", "$presentHouse $presentBarangay, $presentCity, $presentProvince")
        OverviewDetail("Primary Mobile Number", primaryMobileNumber)
        OverviewDetail("Primary Email Address", primaryEmailAddress)

        ProfileSectionHeader("Family Background (Emergency)", null)
        OverviewDetail("Father's Name", "$fatherFirstName $fatherMiddleName $fatherLastName")
        OverviewDetail("Mother's Name", "$motherFirstName $motherMiddleName $motherLastName")

        ProfileSectionHeader("Enrollment Details", null)
        OverviewDetail("College", college)
        OverviewDetail("Program", program)
        OverviewDetail("Curriculum", curriculum)
        OverviewDetail("Year Level", yearLevel)
        OverviewDetail("Section", section)
    }
}

@Composable
fun EditSection(viewModel: ProfileViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ProfileSectionHeader("Full Name", Icons.Default.Person)
        EditField("First Name", viewModel.firstName, { viewModel.firstName = it }, isEditable = false)
        EditField("Middle Name", viewModel.middleName, { viewModel.middleName = it }, isEditable = false)
        EditField("Last Name", viewModel.lastName, { viewModel.lastName = it }, isEditable = false)

        ProfileSectionHeader("Personal Information", Icons.Default.Info)
        EditField("Student Number *", viewModel.studentNumber, { viewModel.studentNumber = it })
        EditField("Sex at Birth *", viewModel.sexAtBirth, { viewModel.sexAtBirth = it })
        EditField("Nationality *", viewModel.nationality, { viewModel.nationality = it })
        EditField("Date of Birth", viewModel.dateOfBirth, { viewModel.dateOfBirth = it }, isEditable = false)

        ProfileSectionHeader("Contact Information", Icons.Default.ContactPhone)
        EditField("Province *", viewModel.presentProvince, { viewModel.presentProvince = it })
        EditField("City / Municipality *", viewModel.presentCity, { viewModel.presentCity = it })
        EditField("Barangay *", viewModel.presentBarangay, { viewModel.presentBarangay = it })
        EditField("House Number / Street / Subdivision / Sitio *", viewModel.presentHouse, { viewModel.presentHouse = it })
        EditField("ZIP Code *", viewModel.presentZip, { viewModel.presentZip = it }, keyboardType = KeyboardType.Number)

        ProfileSectionHeader("Contact Details", Icons.Default.ContactPhone)
        EditField("Primary Mobile Number *", viewModel.primaryMobileNumber, { viewModel.primaryMobileNumber = it })
        EditField("Primary Email Address", viewModel.primaryEmailAddress, { viewModel.primaryEmailAddress = it })

        ProfileSectionHeader("Father's Information", Icons.Default.AccountCircle)
        EditField("First Name *", viewModel.fatherFirstName, { viewModel.fatherFirstName = it })
        EditField("Middle Name *", viewModel.fatherMiddleName, { viewModel.fatherMiddleName = it })
        EditField("Last Name *", viewModel.fatherLastName, { viewModel.fatherLastName = it })

        ProfileSectionHeader("Mother's Information", Icons.Default.AccountBox)
        EditField("First Name *", viewModel.motherFirstName, { viewModel.motherFirstName = it })
        EditField("Middle Name *", viewModel.middleNameMother, { viewModel.middleNameMother = it })
        EditField("Last Name *", viewModel.motherLastName, { viewModel.motherLastName = it })

        ProfileSectionHeader("Enrollment Details", Icons.Default.Assignment)
        EditField("College", viewModel.college, { viewModel.college = it }, isEditable = false)
        EditField("Program", viewModel.program, { viewModel.program = it }, isEditable = false)
        EditField("Curriculum", viewModel.curriculum, { viewModel.curriculum = it }, isEditable = false)
        EditField("Year Level", viewModel.yearLevel, { viewModel.yearLevel = it }, isEditable = false)
        EditField("Section", viewModel.section, { viewModel.section = it }, isEditable = false)
    }
}

@Composable
fun ProfileSectionHeader(title: String, icon: ImageVector?) {
    Column(modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun OverviewDetail(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditable: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        if (isEditable) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
            )
        } else {
            Text(value, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
