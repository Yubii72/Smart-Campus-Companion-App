package com.example.smartcampuscompanionapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanionapp.data.local.entities.Student
import com.example.smartcampuscompanionapp.data.repository.StudentRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: StudentRepository) : ViewModel() {
    var isEditMode by mutableStateOf(false)

    // State for all fields
    var firstName by mutableStateOf("")
    var middleName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var nameExtension by mutableStateOf("")

    var studentNumber by mutableStateOf("")
    var password by mutableStateOf("") 
    
    var sexAtBirth by mutableStateOf("")
    var civilStatus by mutableStateOf("")
    var residency by mutableStateOf("")
    var nationality by mutableStateOf("")
    var religion by mutableStateOf("")
    var dateOfBirth by mutableStateOf("")
    var placeOfBirth by mutableStateOf("")

    // Address state
    var presentProvince by mutableStateOf("")
    var presentCity by mutableStateOf("")
    var presentBarangay by mutableStateOf("")
    var presentHouse by mutableStateOf("")
    var presentZip by mutableStateOf("")

    var permanentProvince by mutableStateOf("")
    var permanentCity by mutableStateOf("")
    var permanentBarangay by mutableStateOf("")
    var permanentHouse by mutableStateOf("")
    var permanentZip by mutableStateOf("")

    var primaryMobileNumber by mutableStateOf("")
    var alternateMobileNumber by mutableStateOf("")
    var primaryEmailAddress by mutableStateOf("")
    var alternateEmailAddress by mutableStateOf("")

    // Father
    var fatherFirstName by mutableStateOf("")
    var fatherMiddleName by mutableStateOf("")
    var fatherLastName by mutableStateOf("")
    var fatherOccupation by mutableStateOf("")
    var fatherDateOfBirth by mutableStateOf("")
    var fatherSexAtBirth by mutableStateOf("")
    var isFatherGuardian by mutableStateOf(false)

    // Mother
    var motherFirstName by mutableStateOf("")
    var motherMiddleName by mutableStateOf("")
    var motherLastName by mutableStateOf("")
    var motherOccupation by mutableStateOf("")
    var motherDateOfBirth by mutableStateOf("")
    var motherSexAtBirth by mutableStateOf("")
    var isMotherGuardian by mutableStateOf(false)

    var numberOfSiblings by mutableStateOf("")
    var familyAnnualIncome by mutableStateOf("")

    // Guardian
    var guardianFirstName by mutableStateOf("")
    var guardianMiddleName by mutableStateOf("")
    var guardianLastName by mutableStateOf("")
    var relationToGuardian by mutableStateOf("")
    var guardianContactNumber by mutableStateOf("")

    // Educational
    var lastSchoolAttended by mutableStateOf("")
    var lastYearAttended by mutableStateOf("")
    var learnerReferenceNumber by mutableStateOf("")
    var honorsReceived by mutableStateOf("")

    // Enrollment
    var college by mutableStateOf("")
    var program by mutableStateOf("")
    var curriculum by mutableStateOf("")
    var yearLevel by mutableStateOf("")
    var section by mutableStateOf("")

    fun loadProfile(studentNum: String) {
        viewModelScope.launch {
            repository.getStudentByNumber(studentNum)?.let { student ->
                studentNumber = student.studentNumber
                password = student.password
                firstName = student.firstName
                lastName = student.lastName
                
                sexAtBirth = student.sexAtBirth
                civilStatus = student.civilStatus
                residency = student.residency
                nationality = student.nationality
                religion = student.religion
                dateOfBirth = student.dateOfBirth
                placeOfBirth = student.placeOfBirth

                // Parse addresses - ensuring we don't crash if format is unexpected
                val presentParts = student.presentAddress.split(", ")
                presentHouse = presentParts.getOrNull(0) ?: ""
                presentBarangay = presentParts.getOrNull(1) ?: ""
                presentCity = presentParts.getOrNull(2) ?: ""
                presentProvince = presentParts.getOrNull(3) ?: ""
                
                val permanentParts = student.permanentAddress.split(", ")
                permanentHouse = permanentParts.getOrNull(0) ?: ""
                permanentBarangay = permanentParts.getOrNull(1) ?: ""
                permanentCity = permanentParts.getOrNull(2) ?: ""
                permanentProvince = permanentParts.getOrNull(3) ?: ""

                primaryMobileNumber = student.primaryMobileNumber
                alternateMobileNumber = student.alternateMobileNumber
                primaryEmailAddress = student.primaryEmailAddress
                alternateEmailAddress = student.alternateEmailAddress

                // Family - Father
                val fNames = student.fathersName.split(" ")
                fatherFirstName = fNames.getOrNull(0) ?: ""
                fatherMiddleName = fNames.getOrNull(1) ?: ""
                fatherLastName = fNames.getOrNull(2) ?: ""
                fatherOccupation = student.fathersOccupation
                fatherDateOfBirth = student.fathersDateOfBirth
                fatherSexAtBirth = student.fathersSexAtBirth

                // Family - Mother
                val mNames = student.mothersName.split(" ")
                motherFirstName = mNames.getOrNull(0) ?: ""
                motherMiddleName = mNames.getOrNull(1) ?: ""
                motherLastName = mNames.getOrNull(2) ?: ""
                motherOccupation = student.mothersOccupation
                motherDateOfBirth = student.mothersDateOfBirth
                motherSexAtBirth = student.mothersSexAtBirth

                numberOfSiblings = student.numberOfSiblings.toString()
                familyAnnualIncome = student.familyAnnualIncome.toString()

                // Guardian
                val gNames = student.guardiansName.split(" ")
                guardianFirstName = gNames.getOrNull(0) ?: ""
                guardianMiddleName = gNames.getOrNull(1) ?: ""
                guardianLastName = gNames.getOrNull(2) ?: ""
                relationToGuardian = student.relationToGuardian
                guardianContactNumber = student.guardiansContactNumber
                
                isFatherGuardian = student.guardiansName.isNotEmpty() && student.guardiansName == student.fathersName
                isMotherGuardian = student.guardiansName.isNotEmpty() && student.guardiansName == student.mothersName

                lastSchoolAttended = student.lastSchoolAttended
                lastYearAttended = student.lastYearAttended
                learnerReferenceNumber = student.learnerReferenceNumber
                honorsReceived = student.honorsReceived

                college = student.college
                program = student.program
                curriculum = student.curriculum
                yearLevel = student.yearLevel
                section = student.section
            }
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val updatedStudent = Student(
                studentNumber = studentNumber,
                password = password,
                firstName = firstName,
                lastName = lastName,
                sexAtBirth = sexAtBirth,
                civilStatus = civilStatus,
                residency = residency,
                nationality = nationality,
                religion = religion,
                dateOfBirth = dateOfBirth,
                placeOfBirth = placeOfBirth,
                presentAddress = "$presentHouse, $presentBarangay, $presentCity, $presentProvince",
                permanentAddress = "$permanentHouse, $permanentBarangay, $permanentCity, $permanentProvince",
                primaryMobileNumber = primaryMobileNumber,
                alternateMobileNumber = alternateMobileNumber,
                primaryEmailAddress = primaryEmailAddress,
                alternateEmailAddress = alternateEmailAddress,
                fathersName = "$fatherFirstName $fatherMiddleName $fatherLastName".trim(),
                fathersOccupation = fatherOccupation,
                fathersDateOfBirth = fatherDateOfBirth,
                fathersSexAtBirth = fatherSexAtBirth,
                mothersName = "$motherFirstName $motherMiddleName $motherLastName".trim(),
                mothersOccupation = motherOccupation,
                mothersDateOfBirth = motherDateOfBirth,
                mothersSexAtBirth = motherSexAtBirth,
                numberOfSiblings = numberOfSiblings.toIntOrNull() ?: 0,
                familyAnnualIncome = familyAnnualIncome.toDoubleOrNull() ?: 0.0,
                guardiansName = "$guardianFirstName $guardianMiddleName $guardianLastName".trim(),
                relationToGuardian = relationToGuardian,
                guardiansContactNumber = guardianContactNumber,
                lastSchoolAttended = lastSchoolAttended,
                lastYearAttended = lastYearAttended,
                learnerReferenceNumber = learnerReferenceNumber,
                honorsReceived = honorsReceived,
                college = college,
                program = program,
                curriculum = curriculum,
                yearLevel = yearLevel,
                section = section
            )
            repository.insertStudent(updatedStudent)
            isEditMode = false
        }
    }

    fun syncGuardianFromFather() {
        if (isFatherGuardian) {
            guardianFirstName = fatherFirstName
            guardianMiddleName = fatherMiddleName
            guardianLastName = fatherLastName
            relationToGuardian = "Father"
            isMotherGuardian = false
        }
    }

    fun syncGuardianFromMother() {
        if (isMotherGuardian) {
            guardianFirstName = motherFirstName
            guardianMiddleName = motherMiddleName
            guardianLastName = motherLastName
            relationToGuardian = "Mother"
            isFatherGuardian = false
        }
    }

    fun onGuardianManualEdit() {
        isFatherGuardian = false
        isMotherGuardian = false
    }
}

class ProfileViewModelFactory(private val repository: StudentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
