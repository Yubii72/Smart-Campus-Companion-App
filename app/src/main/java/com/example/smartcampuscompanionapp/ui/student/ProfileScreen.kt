package com.example.smartcampuscompanionapp.ui.student

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.smartcampuscompanionapp.R
import com.example.smartcampuscompanionapp.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    studentNumber: String,
    onBack: () -> Unit,
    viewModel: ProfileViewModel,
    showBackButton: Boolean = true,
    onSettingsClick: (() -> Unit)? = null
) {
    LaunchedEffect(studentNumber) {
        viewModel.loadProfile(studentNumber)
    }

    LaunchedEffect(viewModel.isFatherGuardian, viewModel.fatherFirstName, viewModel.fatherMiddleName, viewModel.fatherLastName) {
        viewModel.syncGuardianFromFather()
    }

    LaunchedEffect(viewModel.isMotherGuardian, viewModel.motherFirstName, viewModel.motherMiddleName, viewModel.motherLastName) {
        viewModel.syncGuardianFromMother()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (onSettingsClick != null) {
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                    if (viewModel.isEditMode) {
                        TextButton(onClick = { viewModel.saveProfile() }) {
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!viewModel.isEditMode) {
                FloatingActionButton(
                    onClick = { viewModel.isEditMode = true },
                    modifier = Modifier.padding(bottom = 80.dp) // Adjust padding to be above navigation bar
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Detail")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.padding(vertical = 16.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.irang),
                    contentDescription = "Student Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                if (viewModel.isEditMode) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Change Avatar",
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            if (!viewModel.isEditMode) {
                OverviewSection(viewModel)
            } else {
                EditSection(viewModel)
            }
        }
    }
}

@Composable
fun OverviewDetailCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun OverviewSection(viewModel: ProfileViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ProfileSectionHeader("Personal Information", Icons.Default.Person)
        OverviewDetailCard("Basic Info") {
            OverviewDetail("First Name", viewModel.firstName)
            OverviewDetail("Last Name", viewModel.lastName)
            OverviewDetail("Student Number", viewModel.studentNumber)
            OverviewDetail("Sex at Birth", viewModel.sexAtBirth)
            OverviewDetail("Civil Status", viewModel.civilStatus)
            OverviewDetail("Residency", viewModel.residency)
            OverviewDetail("Nationality", viewModel.nationality)
            OverviewDetail("Religion", viewModel.religion)
            OverviewDetail("Date of Birth", viewModel.dateOfBirth)
            OverviewDetail("Place of Birth", viewModel.placeOfBirth)
        }

        ProfileSectionHeader("Contact Information", Icons.Default.ContactPhone)
        OverviewDetailCard("Addresses & Contact") {
            OverviewDetail("Present Address", "${viewModel.presentHouse} ${viewModel.presentBarangay}, ${viewModel.presentCity}, ${viewModel.presentProvince}")
            OverviewDetail("Permanent Address", "${viewModel.permanentHouse} ${viewModel.permanentBarangay}, ${viewModel.permanentCity}, ${viewModel.permanentProvince}")
            OverviewDetail("Primary Mobile Number", viewModel.primaryMobileNumber)
            OverviewDetail("Alternate Mobile Number", viewModel.alternateMobileNumber)
            OverviewDetail("Primary Email Address", viewModel.primaryEmailAddress)
            OverviewDetail("Alternate Email Address", viewModel.alternateEmailAddress)
        }

        ProfileSectionHeader("Family Background", Icons.Default.Groups)
        OverviewDetailCard("Family") {
            OverviewDetail("Father's Name", "${viewModel.fatherFirstName} ${viewModel.fatherMiddleName} ${viewModel.fatherLastName}")
            OverviewDetail("Father's Occupation", viewModel.fatherOccupation)
            OverviewDetail("Father's Date of Birth", viewModel.fatherDateOfBirth)
            OverviewDetail("Father's Sex at Birth", viewModel.fatherSexAtBirth)
            OverviewDetail("Mother's Name", "${viewModel.motherFirstName} ${viewModel.motherMiddleName} ${viewModel.motherLastName}")
            OverviewDetail("Mother's Occupation", viewModel.motherOccupation)
            OverviewDetail("Mother's Date of Birth", viewModel.motherDateOfBirth)
            OverviewDetail("Mother's Sex at Birth", viewModel.motherSexAtBirth)
            OverviewDetail("Number of Siblings", viewModel.numberOfSiblings)
            OverviewDetail("Family's Annual Income", "Php ${"%,.2f".format(viewModel.familyAnnualIncome.toDoubleOrNull() ?: 0.0)} (Php ${"%,.2f".format((viewModel.familyAnnualIncome.toDoubleOrNull() ?: 0.0) / 12)} per month)")
            OverviewDetail("Guardian's Name", "${viewModel.guardianFirstName} ${viewModel.guardianMiddleName} ${viewModel.guardianLastName}")
            OverviewDetail("Relation to Guardian", viewModel.relationToGuardian)
            OverviewDetail("Guardian's Contact Number", viewModel.guardianContactNumber)
        }

        ProfileSectionHeader("Educational Background", Icons.Default.School)
        OverviewDetailCard("Education") {
            OverviewDetail("Last School Attended", viewModel.lastSchoolAttended)
            OverviewDetail("Last Year Attended", viewModel.lastYearAttended)
            OverviewDetail("Learner Reference Number", viewModel.learnerReferenceNumber)
            OverviewDetail("Honor/s Received", viewModel.honorsReceived)
        }

        ProfileSectionHeader("Enrollment Details", Icons.Default.School)
        OverviewDetailCard("Enrollment") {
            OverviewDetail("College", viewModel.college)
            OverviewDetail("Program", viewModel.program)
            OverviewDetail("Curriculum", viewModel.curriculum)
            OverviewDetail("Year Level", viewModel.yearLevel)
            OverviewDetail("Section", viewModel.section)
        }
    }
}

@Composable
fun EditSection(viewModel: ProfileViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ProfileSectionHeader("Full Name", Icons.Default.Person)
        EditField("First Name", viewModel.firstName, { viewModel.firstName = it }, isEditable = false)
        EditField("Name Extension", viewModel.nameExtension, { viewModel.nameExtension = it }, isEditable = false)
        EditField("Middle Name", viewModel.middleName, { viewModel.middleName = it }, isEditable = false)
        EditField("Last Name", viewModel.lastName, { viewModel.lastName = it }, isEditable = false)

        ProfileSectionHeader("Personal Information", Icons.Default.Info)
        EditField("Civil Status", viewModel.civilStatus, { viewModel.civilStatus = it }, isEditable = false)
        EditField("Sex at Birth *", viewModel.sexAtBirth, { viewModel.sexAtBirth = it })
        EditField("Nationality *", viewModel.nationality, { viewModel.nationality = it })
        EditField("Date of Birth", viewModel.dateOfBirth, { viewModel.dateOfBirth = it }, isEditable = false)
        EditField("Place of Birth *", viewModel.placeOfBirth, { viewModel.placeOfBirth = it })
        EditField("Residency", viewModel.residency, { viewModel.residency = it }, isEditable = false)
        EditField("Religion *", viewModel.religion, { viewModel.religion = it })

        ProfileSectionHeader("Present Address", Icons.Default.Home)
        EditField("Province *", viewModel.presentProvince, { viewModel.presentProvince = it })
        EditField("City / Municipality *", viewModel.presentCity, { viewModel.presentCity = it })
        EditField("Barangay *", viewModel.presentBarangay, { viewModel.presentBarangay = it })
        EditField("House Number / Street / Subdivision / Sitio *", viewModel.presentHouse, { viewModel.presentHouse = it })
        EditField("ZIP Code *", viewModel.presentZip, { viewModel.presentZip = it }, keyboardType = KeyboardType.Number)

        ProfileSectionHeader("Permanent Address", Icons.Default.LocationOn)
        EditField("Province *", viewModel.permanentProvince, { viewModel.permanentProvince = it })
        EditField("City / Municipality *", viewModel.permanentCity, { viewModel.permanentCity = it })
        EditField("Barangay *", viewModel.permanentBarangay, { viewModel.permanentBarangay = it })
        EditField("House Number / Street / Subdivision / Sitio *", viewModel.permanentHouse, { viewModel.permanentHouse = it })
        EditField("ZIP Code *", viewModel.permanentZip, { viewModel.permanentZip = it }, keyboardType = KeyboardType.Number)

        ProfileSectionHeader("Contact Information", Icons.Default.ContactPhone)
        EditField("Primary Mobile Number *", viewModel.primaryMobileNumber, { viewModel.primaryMobileNumber = it })
        EditField("Alternate Mobile Number", viewModel.alternateMobileNumber, { viewModel.alternateMobileNumber = it })
        EditField("Primary Email Address", viewModel.primaryEmailAddress, { viewModel.primaryEmailAddress = it }, isEditable = false)
        EditField("Alternate Email Address", viewModel.alternateEmailAddress, { viewModel.alternateEmailAddress = it })

        ProfileSectionHeader("Father's Information", Icons.Default.AccountCircle)
        EditField("First Name *", viewModel.fatherFirstName, { viewModel.fatherFirstName = it })
        EditField("Middle Name *", viewModel.fatherMiddleName, { viewModel.fatherMiddleName = it })
        EditField("Last Name *", viewModel.fatherLastName, { viewModel.fatherLastName = it })
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = viewModel.isFatherGuardian, onCheckedChange = { viewModel.isFatherGuardian = it })
            Text("Guardian")
        }

        EditField("Sex at Birth *", viewModel.fatherSexAtBirth, { viewModel.fatherSexAtBirth = it })
        EditField("Date of Birth *", viewModel.fatherDateOfBirth, { viewModel.fatherDateOfBirth = it })
        EditField("Occupation *", viewModel.fatherOccupation, { viewModel.fatherOccupation = it })

        ProfileSectionHeader("Mother's Information", Icons.Default.AccountBox)
        EditField("First Name *", viewModel.motherFirstName, { viewModel.motherFirstName = it })
        EditField("Middle Name *", viewModel.motherMiddleName, { viewModel.motherMiddleName = it })
        EditField("Last Name *", viewModel.motherLastName, { viewModel.motherLastName = it })

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = viewModel.isMotherGuardian, onCheckedChange = { viewModel.isMotherGuardian = it })
            Text("Guardian")
        }

        EditField("Sex at Birth *", viewModel.motherSexAtBirth, { viewModel.motherSexAtBirth = it })
        EditField("Date of Birth *", viewModel.motherDateOfBirth, { viewModel.motherDateOfBirth = it })
        EditField("Occupation *", viewModel.motherOccupation, { viewModel.motherOccupation = it })

        ProfileSectionHeader("Family Background", Icons.Default.Groups)
        EditField("Number of Siblings *", viewModel.numberOfSiblings, { viewModel.numberOfSiblings = it }, keyboardType = KeyboardType.Number)
        EditField("Family's Annual Income *", viewModel.familyAnnualIncome, { viewModel.familyAnnualIncome = it }, keyboardType = KeyboardType.Decimal)

        ProfileSectionHeader("Guardian's Information", Icons.Default.Shield)
        EditField("First Name *", viewModel.guardianFirstName, { 
            viewModel.guardianFirstName = it 
            viewModel.onGuardianManualEdit()
        })
        EditField("Middle Name *", viewModel.guardianMiddleName, { 
            viewModel.guardianMiddleName = it 
            viewModel.onGuardianManualEdit()
        })
        EditField("Last Name *", viewModel.guardianLastName, { 
            viewModel.guardianLastName = it 
            viewModel.onGuardianManualEdit()
        })
        EditField("Relationship to the Student *", viewModel.relationToGuardian, { 
            viewModel.relationToGuardian = it 
            viewModel.onGuardianManualEdit()
        })
        EditField("Contact Number *", viewModel.guardianContactNumber, { 
            viewModel.guardianContactNumber = it 
            viewModel.onGuardianManualEdit()
        })

        ProfileSectionHeader("Educational Background", Icons.Default.School)
        EditField("Last School Attended *", viewModel.lastSchoolAttended, { viewModel.lastSchoolAttended = it })
        EditField("Last Year Attended *", viewModel.lastYearAttended, { viewModel.lastYearAttended = it }, keyboardType = KeyboardType.Number)
        EditField("Learner Reference Number *", viewModel.learnerReferenceNumber, { viewModel.learnerReferenceNumber = it }, keyboardType = KeyboardType.Number)
        EditField("Honor/s Received", viewModel.honorsReceived, { viewModel.honorsReceived = it })
    }
}

@Composable
fun ProfileSectionHeader(title: String, icon: ImageVector?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun OverviewDetail(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
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
    OutlinedTextField(
        value = value,
        onValueChange = { if (isEditable) onValueChange(it) },
        label = { Text(label) },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        readOnly = !isEditable,
        enabled = isEditable,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledTrailingIconColor = Color.Red
        ),
        trailingIcon = {
            if (!isEditable) {
                Icon(Icons.Default.Lock, contentDescription = "Locked")
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
