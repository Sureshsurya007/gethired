package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert_preferences")
data class AlertPreferenceEntity(
    @PrimaryKey val id: Int = 1,
    val targetRoleKeywords: String = "Software, Design, Analyst, Associate",
    val preferredCity: String = "Chicago, IL",
    val preferredWorkMode: String = "All", // "All", "Remote", "Hybrid", "On-site"
    val minSalary: Int = 65000,
    val instantJobAlerts: Boolean = true,
    val localEventAlerts: Boolean = true,
    val alertFrequency: String = "Instant" // "Instant", "Daily Digest", "Weekly"
)

@Entity(tableName = "alert_notifications")
data class AlertNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val type: String, // "JOB", "EVENT", "CAREER_TIP"
    val referenceId: Int, // Job ID or Event ID
    val timeAgo: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
