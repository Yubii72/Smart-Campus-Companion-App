package com.example.smartcampuscompanionapp.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcampuscompanionapp.R
import com.example.smartcampuscompanionapp.data.local.AppDatabase
import com.example.smartcampuscompanionapp.data.repository.StudentRepository
import com.example.smartcampuscompanionapp.ui.theme.SmartCampusCompanionAppTheme
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    studentNumber: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = StudentRepository(database.studentDao())
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(repository))

    // Load profile data when screen opens
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
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!viewModel.isEditMode) {
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
            )
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
            // AVATAR IN CENTER
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
                            .clickable { /* Handle change avatar */ }
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
                // OVERVIEW MODE
                OverviewSection(
                    studentNumber = viewModel.studentNumber,
                    sexAtBirth = viewModel.sexAtBirth,
                    civilStatus = viewModel.civilStatus,
                    residency = viewModel.residency,
                    nationality = viewModel.nationality,
                    religion = viewModel.religion,
                    dateOfBirth = viewModel.dateOfBirth,
                    placeOfBirth = viewModel.placeOfBirth,
                    presentProvince = viewModel.presentProvince,
                    presentZIP = viewModel.presentZip,
                    presentCity = viewModel.presentCity,
                    presentBarangay = viewModel.presentBarangay,
                    presentHouse = viewModel.presentHouse,
                    permanentProvince = viewModel.permanentProvince,
                    permanentZIP = viewModel.permanentZip,
                    permanentCity = viewModel.permanentCity,
                    permanentBarangay = viewModel.permanentBarangay,
                    permanentHouse = viewModel.permanentHouse,
                    primaryMobileNumber = viewModel.primaryMobileNumber,
                    alternateMobileNumber = viewModel.alternateMobileNumber,
                    primaryEmailAddress = viewModel.primaryEmailAddress,
                    alternateEmailAddress = viewModel.alternateEmailAddress,
                    fatherFirstName = viewModel.fatherFirstName,
                    fatherMiddleName = viewModel.fatherMiddleName,
                    fatherLastName = viewModel.fatherLastName,
                    fatherOccupation = viewModel.fatherOccupation,
                    fatherDateOfBirth = viewModel.fatherDateOfBirth,
                    fatherSexAtBirth = viewModel.fatherSexAtBirth,
                    motherFirstName = viewModel.motherFirstName,
                    motherMiddleName = viewModel.motherMiddleName,
                    motherLastName = viewModel.motherLastName,
                    motherOccupation = viewModel.motherOccupation,
                    motherDateOfBirth = viewModel.motherDateOfBirth,
                    motherSexAtBirth = viewModel.motherSexAtBirth,
                    numberOfSiblings = viewModel.numberOfSiblings,
                    familyAnnualIncome = viewModel.familyAnnualIncome,
                    guardianFirstName = viewModel.guardianFirstName,
                    guardianMiddleName = viewModel.guardianMiddleName,
                    guardianLastName = viewModel.guardianLastName,
                    relationToGuardian = viewModel.relationToGuardian,
                    guardianContactNumber = viewModel.guardianContactNumber,
                    lastSchoolAttended = viewModel.lastSchoolAttended,
                    lastYearAttended = viewModel.lastYearAttended,
                    learnerReferenceNumber = viewModel.learnerReferenceNumber,
                    honorsReceived = viewModel.honorsReceived,
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

@Composable
fun OverviewSection(
    studentNumber: String, sexAtBirth: String, civilStatus: String, residency: String,
    nationality: String, religion: String, dateOfBirth: String, placeOfBirth: String,
    presentProvince: String, presentZIP: String, presentCity: String, presentBarangay: String, presentHouse: String,
    permanentProvince: String, permanentZIP: String, permanentCity: String, permanentBarangay: String, permanentHouse: String,
    primaryMobileNumber: String, alternateMobileNumber: String, primaryEmailAddress: String,
    alternateEmailAddress: String, fatherFirstName: String, fatherMiddleName: String,
    fatherLastName: String, fatherOccupation: String, fatherDateOfBirth: String,
    fatherSexAtBirth: String, motherFirstName: String, motherMiddleName: String,
    motherLastName: String, motherOccupation: String, motherDateOfBirth: String,
    motherSexAtBirth: String, numberOfSiblings: String, familyAnnualIncome: String,
    guardianFirstName: String, guardianMiddleName: String, guardianLastName: String,
    relationToGuardian: String, guardianContactNumber: String, lastSchoolAttended: String,
    lastYearAttended: String, learnerReferenceNumber: String, honorsReceived: String,
    college: String, program: String, curriculum: String, yearLevel: String, section: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OverviewDetail("Student Number", studentNumber)
        OverviewDetail("Sex at Birth", sexAtBirth)
        OverviewDetail("Civil Status", civilStatus)
        OverviewDetail("Residency", residency)
        OverviewDetail("Nationality", nationality)
        OverviewDetail("Religion", religion)
        OverviewDetail("Date of Birth", dateOfBirth)
        OverviewDetail("Place of Birth", placeOfBirth)

        ProfileSectionHeader("Contact Information", null)
        OverviewDetail("Present Address", "$presentHouse $presentBarangay, $presentCity, $presentProvince")
        OverviewDetail("Permanent Address", "$permanentHouse $permanentBarangay, $permanentCity, $permanentProvince")
        OverviewDetail("Primary Mobile Number", primaryMobileNumber)
        OverviewDetail("Alternate Mobile Number", alternateMobileNumber)
        OverviewDetail("Primary Email Address", primaryEmailAddress)
        OverviewDetail("Alternate Email Address", alternateEmailAddress)

        ProfileSectionHeader("Family Background", null)
        OverviewDetail("Father's Name", "$fatherFirstName $fatherMiddleName $fatherLastName")
        OverviewDetail("Father's Occupation", fatherOccupation)
        OverviewDetail("Father's Date of Birth", fatherDateOfBirth)
        OverviewDetail("Father's Sex at Birth", fatherSexAtBirth)
        OverviewDetail("Mother's Name", "$motherFirstName $motherMiddleName $motherLastName")
        OverviewDetail("Mother's Occupation", motherOccupation)
        OverviewDetail("Mother's Date of Birth", motherDateOfBirth)
        OverviewDetail("Mother's Sex at Birth", motherSexAtBirth)
        OverviewDetail("Number of Siblings", numberOfSiblings)
        OverviewDetail("Family's Annual Income", "Php ${"%,.2f".format(familyAnnualIncome.toDoubleOrNull() ?: 0.0)} (Php ${"%,.2f".format((familyAnnualIncome.toDoubleOrNull() ?: 0.0) / 12)} per month)")
        OverviewDetail("Guardian's Name", "$guardianFirstName $guardianMiddleName $guardianLastName")
        OverviewDetail("Relation to Guardian", relationToGuardian)
        OverviewDetail("Guardian's Contact Number", guardianContactNumber)

        ProfileSectionHeader("Educational Background", null)
        OverviewDetail("Last School Attended", lastSchoolAttended)
        OverviewDetail("Last Year Attended", lastYearAttended)
        OverviewDetail("Learner Reference Number", learnerReferenceNumber)
        OverviewDetail("Honor/s Received", honorsReceived)

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
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(8.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        readOnly = !isEditable,
        enabled = isEditable, // Graying out uneditable fields
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledTrailingIconColor = Color.Red // Keeping the lock icon red
        ),
        trailingIcon = {
            if (!isEditable) {
                Icon(Icons.Default.Lock, contentDescription = "Locked")
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    SmartCampusCompanionAppTheme {
        // Mock Preview
    }
}