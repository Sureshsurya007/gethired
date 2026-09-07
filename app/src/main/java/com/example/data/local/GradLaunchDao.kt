package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GradLaunchDao {
    // --- Jobs ---
    @Query("SELECT * FROM jobs ORDER BY matchScore DESC, id ASC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id")
    fun getJobById(id: Int): Flow<JobEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobEntity>)

    @Update
    suspend fun updateJob(job: JobEntity)

    @Query("UPDATE jobs SET isSaved = :isSaved WHERE id = :id")
    suspend fun toggleSaveJob(id: Int, isSaved: Boolean)

    @Query("UPDATE jobs SET applicationStatus = :status WHERE id = :id")
    suspend fun updateJobApplicationStatus(id: Int, status: String)

    @Query("SELECT COUNT(*) FROM jobs")
    suspend fun getJobCount(): Int

    // --- Events ---
    @Query("SELECT * FROM events ORDER BY id ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: Int): Flow<EventEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Query("UPDATE events SET rsvpStatus = :status, attendeeCount = :attendeeCount WHERE id = :id")
    suspend fun updateEventRsvp(id: Int, status: String, attendeeCount: Int)

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getEventCount(): Int

    // --- Alert Preferences ---
    @Query("SELECT * FROM alert_preferences WHERE id = 1 LIMIT 1")
    fun getAlertPreferences(): Flow<AlertPreferenceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAlertPreferences(pref: AlertPreferenceEntity)

    // --- Alert Notifications ---
    @Query("SELECT * FROM alert_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AlertNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AlertNotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<AlertNotificationEntity>)

    @Query("UPDATE alert_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Int)

    @Query("UPDATE alert_notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM alert_notifications")
    suspend fun clearAllNotifications()

    @Query("SELECT COUNT(*) FROM alert_notifications WHERE isRead = 0")
    fun getUnreadNotificationsCount(): Flow<Int>

    // --- Resume Profile ---
    @Query("SELECT * FROM resume_profile WHERE id = 1 LIMIT 1")
    fun getResumeProfile(): Flow<ResumeProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateResume(profile: ResumeProfileEntity)

    // --- Interview Practice Sessions ---
    @Query("SELECT * FROM interview_sessions ORDER BY timestamp DESC")
    fun getAllInterviewSessions(): Flow<List<InterviewPracticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterviewSession(session: InterviewPracticeEntity): Long

    @Query("DELETE FROM interview_sessions WHERE id = :id")
    suspend fun deleteInterviewSession(id: Int)

    @Query("DELETE FROM interview_sessions")
    suspend fun clearAllInterviewSessions()
}
