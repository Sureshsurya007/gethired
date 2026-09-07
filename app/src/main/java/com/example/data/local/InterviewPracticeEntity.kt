package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interview_sessions")
data class InterviewPracticeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val questionId: String,
    val questionText: String,
    val category: String,
    val userResponse: String,
    val durationSeconds: Int,
    val wordCount: Int,
    val overallScore: Int,
    val lengthVerdict: String,
    val specificExamplesScore: Int,
    val suggestionsSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)
