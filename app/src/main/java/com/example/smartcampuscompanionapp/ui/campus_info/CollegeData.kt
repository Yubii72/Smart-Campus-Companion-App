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

data class College(
    val id: String,
    val name: String,
    val fullName: String,
    val logoRes: Int,
    val primaryColor: Color,
    val description: String = "",
    val socialMedia: SocialMedia = SocialMedia(),
    val phoneNumber: String = "N/A"
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