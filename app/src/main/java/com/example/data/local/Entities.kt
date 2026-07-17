package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boussole")
data class BoussoleEntity(
    @PrimaryKey val id: Int = 1,
    val nordVision: String = "",
    val sudValeurs: String = "",
    val estRessources: String = "",
    val ouestMenaces: String = ""
)

@Entity(tableName = "decide_entries")
data class DecideEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val probleme: String,
    val optionA: String,
    val optionB: String,
    val consultant: String,
    val intuitionIa: String,
    val decision: String,
    val evaluation: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "level_assessments")
data class LevelAssessmentEntity(
    @PrimaryKey val level: Int,
    val checked: Boolean
)

@Entity(tableName = "module_progress")
data class ModuleProgressEntity(
    @PrimaryKey val moduleId: String,
    val completed: Boolean = false,
    val downloaded: Boolean = false
)

@Entity(tableName = "prospects")
data class ProspectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyName: String,
    val contactEmail: String,
    val sector: String, // e.g. "PME", "Grande Entreprise", "ONG", "Public"
    val opportunityDescription: String,
    val emailSubject: String,
    val emailBody: String,
    val testEmailSent: Boolean = false,
    val realEmailSent: Boolean = false,
    val isEmailValid: Boolean = true,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

