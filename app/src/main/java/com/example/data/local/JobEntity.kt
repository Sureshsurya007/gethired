package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val company: String,
    val location: String,
    val workMode: String, // "On-site", "Hybrid", "Remote"
    val jobType: String,  // "Full-time", "Rotational Program", "Apprenticeship", "Fellowship"
    val category: String, // "Engineering & Tech", "Design & UX", "Finance & Business", "Marketing & Growth", "Data & AI"
    val salaryRange: String,
    val minSalary: Int,
    val experienceRequired: String,
    val description: String,
    val whyGreatForGrads: String,
    val requirements: String, // newline separated
    val responsibilities: String, // newline separated
    val benefits: String, // newline separated
    val applicationStatus: String = "NONE", // "NONE", "SAVED", "APPLIED", "INTERVIEWING", "OFFERED"
    val isSaved: Boolean = false,
    val datePosted: String,
    val matchScore: Int = 90
)
