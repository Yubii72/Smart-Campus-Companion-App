package com.example.smartcampuscompanionapp.ui.campus_info

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegeInfoScreen(
    college: College,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = college.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = college.primaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()) // Only one scrollable parent
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            HeaderSection(college)
            ContactSection(college)

            if (college.orgChart != null) {
                OrgChartSection(college.orgChart, college.primaryColor)
            }

            if (college.undergraduatePrograms.isNotEmpty() || college.graduatePrograms.isNotEmpty()) {
                ProgramsSection(college.undergraduatePrograms, college.graduatePrograms)
            }

            if (college.studentOrgs.isNotEmpty()) {
                StudentOrgsSection(college.studentOrgs)
            }
        }
    }
}

@Composable
fun HeaderSection(college: College) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = college.logoRes),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = college.fullName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = college.primaryColor
        )
    }
}

@Composable
fun ContactSection(college: College) {
    InfoCard(title = "Contact Information", icon = Icons.Default.ContactPage) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ContactItem(Icons.Default.Email, college.socialMedia.email ?: "N/A")
            ContactItem(Icons.Default.Phone, college.phoneNumber)
            college.socialMedia.facebook?.let { ContactItem(Icons.Default.Facebook, it) }
            college.socialMedia.youtube?.let { ContactItem(Icons.Default.VideoLibrary, it) }
            college.socialMedia.instagram?.let { ContactItem(Icons.Default.CameraAlt, it) }
        }
    }
}

@Composable
fun ContactItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun OrgChartSection(root: OrgMember, primaryColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccountTree, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Organizational Chart", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // President
            OrgMemberNode(root, primaryColor)
            VerticalLine()

            // EVP
            val evp = root.subordinates.firstOrNull()
            if (evp != null) {
                OrgMemberNode(evp, primaryColor)
                VerticalLine()

                // VPAA
                val vpaa = evp.subordinates.firstOrNull()
                if (vpaa != null) {
                    OrgMemberNode(vpaa, primaryColor)

                    // Dean Level logic
                    val dean = vpaa.subordinates.firstOrNull()
                    if (dean != null) {
                        VerticalLine()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            OrgMemberNode(dean, primaryColor)

                            if (dean.secretary != null) {
                                // Horizontal connection to Secretary
                                Box(modifier = Modifier
                                    .width(30.dp)
                                    .height(4.dp)
                                    .background(Color.LightGray))
                                OrgMemberNode(dean.secretary, primaryColor)
                            }
                        }

                        // Dept Chairs Level
                        if (dean.subordinates.isNotEmpty()) {
                            VerticalLine()
                            // Horizontal branching bar
                            Box(modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(4.dp)
                                .background(Color.LightGray))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                dean.subordinates.forEach { chair ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        VerticalLine()
                                        OrgMemberNode(chair, primaryColor)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VerticalLine() {
    Box(modifier = Modifier
        .width(2.dp)
        .height(24.dp)
        .background(Color.LightGray))
}

@Composable
fun OrgMemberNode(member: OrgMember, backgroundColor: Color) {
    Card(
        modifier = Modifier.size(150.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = member.photoRes),
                contentDescription = member.name,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = member.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White,
                maxLines = 2,
                lineHeight = 11.sp
            )
            Text(
                text = member.position,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 8.sp,
                lineHeight = 9.sp
            )
        }
    }
}

@Composable
fun ProgramsSection(undergraduate: List<String>, graduate: List<String>) {
    InfoCard(title = "Programs Offered", icon = Icons.AutoMirrored.Filled.List) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // College-Offered Programs
            if (undergraduate.isNotEmpty()) {
                Text(
                    text = "College–Offered Programs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                undergraduate.forEach { program ->
                    ProgramItem(program)
                }
            }
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            // Graduate Studies-Offered Programs
            if (graduate.isNotEmpty()) {
                Text(
                    text = "Graduate Studies–Offered Programs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                graduate.forEach { program ->
                    ProgramItem(program)
                }
            }
        }
    }
}

@Composable
fun ProgramItem(name: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF4CAF50))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StudentOrgsSection(orgs: List<StudentOrg>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Student Organizations",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        orgs.forEach { org ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = org.logoRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = org.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = org.description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify,
                        lineHeight = 20.sp
                    )

                    org.socialMedia?.let { sm ->
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        sm.email?.let { ContactItem(Icons.Default.Email, it) }
                        sm.facebook?.let { ContactItem(Icons.Default.Facebook, it) }
                        sm.instagram?.let { ContactItem(Icons.Default.CameraAlt, it) }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Box(modifier = Modifier.padding(20.dp)) {
                content()
            }
        }
    }
}