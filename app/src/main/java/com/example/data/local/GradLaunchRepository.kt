package com.example.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GradLaunchRepository(private val dao: GradLaunchDao) {

    // Initialize initial sample data if tables are empty
    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        if (dao.getJobCount() == 0) {
            dao.insertJobs(InitialData.sampleJobs)
        }
        if (dao.getEventCount() == 0) {
            dao.insertEvents(InitialData.sampleEvents)
        }
        // Initialize default alert preferences if null
        dao.insertOrUpdateAlertPreferences(
            AlertPreferenceEntity(
                id = 1,
                targetRoleKeywords = "Software, Design, Analyst, Associate",
                preferredCity = "Chicago, IL",
                preferredWorkMode = "All",
                minSalary = 65000,
                instantJobAlerts = true,
                localEventAlerts = true,
                alertFrequency = "Instant"
            )
        )
        // Initialize sample notifications if empty
        dao.insertNotifications(InitialData.sampleNotifications)

        // Initialize default resume profile
        val eduJson = ResumeConverters.serializeEducation(InitialData.initialEducation)
        val expJson = ResumeConverters.serializeExperience(InitialData.initialExperience)
        val projJson = ResumeConverters.serializeProjects(InitialData.initialProjects)

        val defaultProfile = ResumeProfileEntity(
            id = 1,
            fullName = "Alex Morgan",
            targetRole = "Associate Software Engineer / Tech Analyst",
            email = "alex.morgan@alumni.edu",
            phone = "(312) 555-0194",
            location = "Chicago, IL (Open to Relocate)",
            linkedin = "linkedin.com/in/alexmorgan-grad",
            github = "github.com/alexmorgan-code",
            portfolio = "alexmorgan.dev",
            summary = "Recent Computer Science & Business Analytics graduate with hands-on internship experience in full-stack mobile development, cloud services, and collaborative agile workflows. Eager to contribute disciplined problem-solving and rapid learning to an innovative entry-level engineering team.",
            educationJson = eduJson,
            experienceJson = expJson,
            projectsJson = projJson,
            skillsTechnical = "Kotlin, Jetpack Compose, Java, Python, SQL, REST APIs, Git, Docker",
            skillsSoft = "Cross-functional Collaboration, Agile / Scrum, Technical Communication, Rapid Problem Solving",
            certifications = "AWS Certified Cloud Practitioner, Google Analytics Certified"
        )
        dao.insertOrUpdateResume(defaultProfile)
    }

    // --- Jobs ---
    val allJobs: Flow<List<JobEntity>> = dao.getAllJobs()

    fun getJobById(id: Int): Flow<JobEntity?> = dao.getJobById(id)

    suspend fun toggleSaveJob(id: Int, isSaved: Boolean) = withContext(Dispatchers.IO) {
        dao.toggleSaveJob(id, isSaved)
    }

    suspend fun updateJobApplicationStatus(id: Int, status: String) = withContext(Dispatchers.IO) {
        dao.updateJobApplicationStatus(id, status)
    }

    // --- Events ---
    val allEvents: Flow<List<EventEntity>> = dao.getAllEvents()

    fun getEventById(id: Int): Flow<EventEntity?> = dao.getEventById(id)

    suspend fun updateEventRsvp(id: Int, status: String, newAttendeeCount: Int) = withContext(Dispatchers.IO) {
        dao.updateEventRsvp(id, status, newAttendeeCount)
    }

    // --- Alerts & Notifications ---
    val alertPreferences: Flow<AlertPreferenceEntity?> = dao.getAlertPreferences()

    suspend fun updateAlertPreferences(pref: AlertPreferenceEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateAlertPreferences(pref)
    }

    val allNotifications: Flow<List<AlertNotificationEntity>> = dao.getAllNotifications()
    val unreadCount: Flow<Int> = dao.getUnreadNotificationsCount()

    suspend fun markNotificationAsRead(id: Int) = withContext(Dispatchers.IO) {
        dao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        dao.markAllNotificationsAsRead()
    }

    suspend fun createCustomAlert(title: String, message: String, type: String, referenceId: Int) = withContext(Dispatchers.IO) {
        dao.insertNotification(
            AlertNotificationEntity(
                title = title,
                message = message,
                type = type,
                referenceId = referenceId,
                timeAgo = "Just now",
                isRead = false,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // --- Resume Profile ---
    val resumeProfile: Flow<ResumeProfileEntity?> = dao.getResumeProfile()

    suspend fun saveResumeProfile(profile: ResumeProfileEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateResume(profile.copy(lastUpdated = System.currentTimeMillis()))
    }

    // --- Interview Practice Sessions ---
    val interviewSessions: Flow<List<InterviewPracticeEntity>> = dao.getAllInterviewSessions()

    suspend fun saveInterviewSession(session: InterviewPracticeEntity): Long = withContext(Dispatchers.IO) {
        dao.insertInterviewSession(session)
    }

    suspend fun deleteInterviewSession(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteInterviewSession(id)
    }

    suspend fun clearAllInterviewSessions() = withContext(Dispatchers.IO) {
        dao.clearAllInterviewSessions()
    }
}
