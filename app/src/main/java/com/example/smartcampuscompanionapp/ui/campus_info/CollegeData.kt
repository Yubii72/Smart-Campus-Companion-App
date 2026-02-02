package com.example.smartcampuscompanionapp.ui.campus_info

import androidx.compose.ui.graphics.Color
import com.example.smartcampuscompanionapp.R
import com.example.smartcampuscompanionapp.ui.theme.*

data class SocialMedia(
    val email: String? = null,
    val facebook: String? = null,
    val youtube: String? = null,
    val instagram: String? = null
)

data class OrgMember(
    val name: String,
    val position: String,
    val photoRes: Int,
    val subordinates: List<OrgMember> = emptyList(),
    val secretary: OrgMember? = null
)

data class StudentOrg(
    val name: String,
    val logoRes: Int,
    val description: String,
    val socialMedia: SocialMedia? = null
)

data class College(
    val id: String,
    val name: String,
    val fullName: String,
    val logoRes: Int,
    val primaryColor: Color,
    val description: String = "",
    val socialMedia: SocialMedia = SocialMedia(),
    val phoneNumber: String = "N/A",
    val programs: List<String> = emptyList(),
    val studentOrgs: List<StudentOrg> = emptyList(),
    val orgChart: OrgMember? = null
)

val colleges = listOf(
    College(
        id = "CCS",
        name = "CCS",
        fullName = "College of Computing Studies",
        logoRes = R.drawable.ccs,
        primaryColor = ColorCCS,
        socialMedia = SocialMedia(
            email = "ccscsg@pnc.edu.ph",
            facebook = "https://www.facebook.com/PNC.CCS"
        ),
        orgChart = OrgMember(
            name = "Dr. Roberto F. Rañola Jr.",
            position = "OIC University President",
            photoRes = R.drawable.oic_pres,
            subordinates = listOf(
                OrgMember(
                    name = "Dr. Renelina D. Mañabo",
                    position = "Executive Vice President",
                    photoRes = R.drawable.evp,
                    subordinates = listOf(
                        OrgMember(
                            name = "Dr. George V. Lambot",
                            position = "Vice President for Academic Affairs",
                            photoRes = R.drawable.vpass,
                            subordinates = listOf(
                                OrgMember(
                                    name = "Dr. Gima B. Montecillo",
                                    position = "Dean",
                                    photoRes = R.drawable.dean,
                                    secretary = OrgMember(
                                        name = "Ms. Gia Mae L. Gaviola",
                                        position = "College Secretary",
                                        photoRes = R.drawable.sec
                                    ),
                                    subordinates = listOf(
                                        OrgMember(
                                            name = "Asst. Prof. Arcelito C. Quiatchon",
                                            position = "BSIT Department Chair",
                                            photoRes = R.drawable.bsit_dc
                                        ),
                                        OrgMember(
                                            name = "Asst. Prof. Evangelina A. Magaling",
                                            position = "BSCS Department Chair",
                                            photoRes = R.drawable.bscs_dc
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        ),
        programs = listOf(
            "Bachelor of Science in Information Technology",
            "Bachelor of Science in Computer Science"
        ),
        studentOrgs = listOf(
            StudentOrg(
                name = "College of Computing Studies - Student Government (CCS-SG)",
                logoRes = R.drawable.ccs,
                description = "We, the students of Pamantasan ng Cabuyao (University of Cabuyao), beseeching the aid of our Almighty God, to establish a student council that shall promote and safeguard the students’ interests, rights and welfare and shall lead the active involvement of the studentry in achieving the institution’s mission and vision, do hereby ordain and promulgate this constitution."
            ),
            StudentOrg(
                name = "Society of Information Technology Students (SITES)",
                logoRes = R.drawable.sites,
                description = "The purpose of this organization is to encourage students in a good leadership, build social relationship with one another by engaging in activities as well as to provide a good service enable them to learn and to motivate. SITeS abide and supports the PNC policies, USG laws and other recognized organizations of this institutions.",
                socialMedia = SocialMedia(
                    email = "pncccssites202425@gmail.com",
                    facebook = "https://www.facebook.com/PnCSITeS"
                )
            ),
            StudentOrg(
                name = "Association of Computer Science Students (ACSS)",
                logoRes = R.drawable.acss,
                description = "The purpose of founding this organization is to develop cooperation through participating in all school activities and events, build up camaraderie among the members of the organization and educators of the university, and inspire the endeavors of students in pursuit of goals as a computer science student.",
                socialMedia = SocialMedia(
                    email = "acssofficial.uc@gmail.com",
                    facebook = "https://www.facebook.com/ACSS.PNC",
                    instagram = "https://www.instagram.com/acss.pnc"
                )
            )
        )
    ),
    College(
        id = "CAS",
        name = "CAS",
        fullName = "College of Arts and Sciences",
        logoRes = R.drawable.cas,
        primaryColor = ColorCAS,
        socialMedia = SocialMedia(
            email = "pnccas23@gmail.com",
            facebook = "https://www.facebook.com/pnccascsg",
            youtube = "https://www.youtube.com/pnccas23"
        )
    ),
    College(
        id = "COE",
        name = "COE",
        fullName = "College of Engineering",
        logoRes = R.drawable.coe,
        primaryColor = ColorCOE,
        socialMedia = SocialMedia(
            facebook = "https://www.facebook.com/pnccoe",
            instagram = "https://www.instagram.com/pnccoe"
        )
    ),
    College(
        id = "CBAA",
        name = "CBAA",
        fullName = "College of Business, Accountancy and Administration",
        logoRes = R.drawable.cbaa,
        primaryColor = ColorCBAA,
        socialMedia = SocialMedia(
            email = "pnccbaa@gmail.com",
            facebook = "https://www.facebook.com/pnccbaacsg"
        ),
        phoneNumber = "0946 462 6858"
    ),
    College(
        id = "CHAS",
        name = "CHAS",
        fullName = "College of Health and Allied Sciences",
        logoRes = R.drawable.chas,
        primaryColor = ColorCHAS,
        socialMedia = SocialMedia(
            email = "chas.new.email@gmail.com",
            facebook = "https://www.facebook.com/CHASPnC"
        )
    ),
    College(
        id = "COED",
        name = "COED",
        fullName = "College of Education",
        logoRes = R.drawable.coed,
        primaryColor = ColorCOED,
        socialMedia = SocialMedia(
            email = "coedcsg@pnc.edu.ph",
            facebook = "https://www.facebook.com/PnCCOED"
        )
    )
)
