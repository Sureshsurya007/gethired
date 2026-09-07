package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val organizer: String,
    val location: String,
    val address: String,
    val city: String,
    val eventType: String, // "Mixer", "Career Fair", "Workshop", "Alumni Night", "Speed Mentoring"
    val dateFormatted: String,
    val timeFormatted: String,
    val isFree: Boolean,
    val hasHiringManagers: Boolean,
    val hasResumeReview: Boolean,
    val description: String,
    val dressCode: String,
    val rsvpStatus: String = "NONE", // "NONE", "ATTENDING", "INTERESTED"
    val attendeeCount: Int = 45
)
