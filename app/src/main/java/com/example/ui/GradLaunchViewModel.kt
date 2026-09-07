package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.interview.InterviewAnalysisEngine
import com.example.data.interview.InterviewAnalysisResult
import com.example.data.interview.InterviewQuestion
import com.example.data.interview.InterviewQuestionsBank
import com.example.data.local.AlertNotificationEntity
import com.example.data.local.AlertPreferenceEntity
import com.example.data.local.EducationItem
import com.example.data.local.EventEntity
import com.example.data.local.ExperienceItem
import com.example.data.local.GradLaunchDatabase
import com.example.data.local.GradLaunchRepository
import com.example.data.local.InitialData
import com.example.data.local.InterviewPracticeEntity
import com.example.data.local.JobEntity
import com.example.data.local.ProjectItem
import com.example.data.local.ResumeConverters
import com.example.data.local.ResumeProfileEntity
import kotlinx.coroutines.Job as CoroutineJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GradLaunchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GradLaunchRepository

    init {
        val database = GradLaunchDatabase.getDatabase(application)
        repository = GradLaunchRepository(database.dao())
        viewModelScope.launch {
            repository.initializeIfEmpty()
        }
    }

    // --- Active Tab Navigation ---
    private val _selectedTab = MutableStateFlow(0) // 0: Jobs, 1: Events, 2: Resume, 3: Alerts & Tracker
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    // --- Search & Filter States for Jobs ---
    private val _jobSearchQuery = MutableStateFlow("")
    val jobSearchQuery: StateFlow<String> = _jobSearchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedWorkMode = MutableStateFlow("All")
    val selectedWorkMode: StateFlow<String> = _selectedWorkMode.asStateFlow()

    private val _onlySavedJobs = MutableStateFlow(false)
    val onlySavedJobs: StateFlow<Boolean> = _onlySavedJobs.asStateFlow()

    val rawJobs = repository.allJobs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredJobs: StateFlow<List<JobEntity>> = combine(
        rawJobs,
        _jobSearchQuery,
        _selectedCategory,
        _selectedWorkMode,
        _onlySavedJobs
    ) { jobs, query, category, workMode, onlySaved ->
        jobs.filter { job ->
            val matchesQuery = query.isBlank() ||
                job.title.contains(query, ignoreCase = true) ||
                job.company.contains(query, ignoreCase = true) ||
                job.location.contains(query, ignoreCase = true) ||
                job.requirements.contains(query, ignoreCase = true)

            val matchesCategory = category == "All" || job.category.equals(category, ignoreCase = true)
            val matchesWorkMode = workMode == "All" || job.workMode.equals(workMode, ignoreCase = true)
            val matchesSaved = !onlySaved || job.isSaved

            matchesQuery && matchesCategory && matchesWorkMode && matchesSaved
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setJobSearchQuery(query: String) {
        _jobSearchQuery.value = query
    }

    fun setJobCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setJobWorkMode(mode: String) {
        _selectedWorkMode.value = mode
    }

    fun toggleOnlySavedJobs() {
        _onlySavedJobs.value = !_onlySavedJobs.value
    }

    // Selected Job Detail Modal
    private val _selectedJob = MutableStateFlow<JobEntity?>(null)
    val selectedJob: StateFlow<JobEntity?> = _selectedJob.asStateFlow()

    fun selectJob(job: JobEntity?) {
        _selectedJob.value = job
    }

    fun toggleSaveJob(job: JobEntity) {
        viewModelScope.launch {
            val newSavedState = !job.isSaved
            repository.toggleSaveJob(job.id, newSavedState)
            if (_selectedJob.value?.id == job.id) {
                _selectedJob.value = _selectedJob.value?.copy(isSaved = newSavedState)
            }
        }
    }

    fun updateJobApplicationStatus(jobId: Int, status: String, jobTitle: String, company: String) {
        viewModelScope.launch {
            repository.updateJobApplicationStatus(jobId, status)
            if (_selectedJob.value?.id == jobId) {
                _selectedJob.value = _selectedJob.value?.copy(applicationStatus = status)
            }
            if (status == "APPLIED") {
                repository.createCustomAlert(
                    title = "Application Submitted!",
                    message = "Successfully marked application for $jobTitle at $company. Track your progress in Career Tracker.",
                    type = "JOB",
                    referenceId = jobId
                )
            }
        }
    }

    // --- Search & Filter States for Events ---
    private val _eventSearchQuery = MutableStateFlow("")
    val eventSearchQuery: StateFlow<String> = _eventSearchQuery.asStateFlow()

    private val _selectedEventType = MutableStateFlow("All")
    val selectedEventType: StateFlow<String> = _selectedEventType.asStateFlow()

    private val _onlyHiringEvents = MutableStateFlow(false)
    val onlyHiringEvents: StateFlow<Boolean> = _onlyHiringEvents.asStateFlow()

    val rawEvents = repository.allEvents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredEvents: StateFlow<List<EventEntity>> = combine(
        rawEvents,
        _eventSearchQuery,
        _selectedEventType,
        _onlyHiringEvents
    ) { events, query, type, onlyHiring ->
        events.filter { event ->
            val matchesQuery = query.isBlank() ||
                event.title.contains(query, ignoreCase = true) ||
                event.organizer.contains(query, ignoreCase = true) ||
                event.location.contains(query, ignoreCase = true) ||
                event.city.contains(query, ignoreCase = true)

            val matchesType = type == "All" || event.eventType.equals(type, ignoreCase = true)
            val matchesHiring = !onlyHiring || event.hasHiringManagers

            matchesQuery && matchesType && matchesHiring
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setEventSearchQuery(query: String) {
        _eventSearchQuery.value = query
    }

    fun setEventType(type: String) {
        _selectedEventType.value = type
    }

    fun toggleOnlyHiringEvents() {
        _onlyHiringEvents.value = !_onlyHiringEvents.value
    }

    private val _selectedEvent = MutableStateFlow<EventEntity?>(null)
    val selectedEvent: StateFlow<EventEntity?> = _selectedEvent.asStateFlow()

    fun selectEvent(event: EventEntity?) {
        _selectedEvent.value = event
    }

    fun updateEventRsvp(event: EventEntity, newStatus: String) {
        viewModelScope.launch {
            val countDiff = when {
                event.rsvpStatus != "ATTENDING" && newStatus == "ATTENDING" -> 1
                event.rsvpStatus == "ATTENDING" && newStatus != "ATTENDING" -> -1
                else -> 0
            }
            val newCount = (event.attendeeCount + countDiff).coerceAtLeast(0)
            repository.updateEventRsvp(event.id, newStatus, newCount)
            if (_selectedEvent.value?.id == event.id) {
                _selectedEvent.value = _selectedEvent.value?.copy(
                    rsvpStatus = newStatus,
                    attendeeCount = newCount
                )
            }
            if (newStatus == "ATTENDING") {
                repository.createCustomAlert(
                    title = "RSVP Confirmed!",
                    message = "You're attending '${event.title}' on ${event.dateFormatted}. Dress code: ${event.dressCode}.",
                    type = "EVENT",
                    referenceId = event.id
                )
            }
        }
    }

    // --- Personalized Alerts & Preferences ---
    val alertPreferences = repository.alertPreferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AlertPreferenceEntity()
    )

    fun updateAlertPreferences(newPref: AlertPreferenceEntity) {
        viewModelScope.launch {
            repository.updateAlertPreferences(newPref)
            repository.createCustomAlert(
                title = "Alert Preferences Saved",
                message = "Personalized alerts updated for '${newPref.targetRoleKeywords}' in ${newPref.preferredCity}.",
                type = "CAREER_TIP",
                referenceId = 0
            )
        }
    }

    val notifications: StateFlow<List<AlertNotificationEntity>> = repository.allNotifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun markNotificationAsRead(id: Int) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun triggerSimulatedCareerAlert() {
        viewModelScope.launch {
            repository.createCustomAlert(
                title = "⚡ New Local Alert Triggered",
                message = "Beacon Global Capital just opened 2 new Associate Rotational slots matching your target preferences!",
                type = "JOB",
                referenceId = 3
            )
        }
    }

    // --- Resume Profile State & Tools ---
    val resumeProfile: StateFlow<ResumeProfileEntity?> = repository.resumeProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun saveResumeBasics(
        fullName: String,
        targetRole: String,
        email: String,
        phone: String,
        location: String,
        linkedin: String,
        github: String,
        portfolio: String,
        summary: String
    ) {
        viewModelScope.launch {
            val current = resumeProfile.value ?: ResumeProfileEntity()
            val updated = current.copy(
                fullName = fullName,
                targetRole = targetRole,
                email = email,
                phone = phone,
                location = location,
                linkedin = linkedin,
                github = github,
                portfolio = portfolio,
                summary = summary
            )
            repository.saveResumeProfile(updated)
        }
    }

    fun saveEducationList(list: List<EducationItem>) {
        viewModelScope.launch {
            val current = resumeProfile.value ?: ResumeProfileEntity()
            val json = ResumeConverters.serializeEducation(list)
            repository.saveResumeProfile(current.copy(educationJson = json))
        }
    }

    fun saveExperienceList(list: List<ExperienceItem>) {
        viewModelScope.launch {
            val current = resumeProfile.value ?: ResumeProfileEntity()
            val json = ResumeConverters.serializeExperience(list)
            repository.saveResumeProfile(current.copy(experienceJson = json))
        }
    }

    fun saveProjectsList(list: List<ProjectItem>) {
        viewModelScope.launch {
            val current = resumeProfile.value ?: ResumeProfileEntity()
            val json = ResumeConverters.serializeProjects(list)
            repository.saveResumeProfile(current.copy(projectsJson = json))
        }
    }

    fun saveSkills(technical: String, soft: String, certs: String) {
        viewModelScope.launch {
            val current = resumeProfile.value ?: ResumeProfileEntity()
            repository.saveResumeProfile(
                current.copy(
                    skillsTechnical = technical,
                    skillsSoft = soft,
                    certifications = certs
                )
            )
        }
    }

    fun resetToSampleProfile() {
        viewModelScope.launch {
            val eduJson = ResumeConverters.serializeEducation(InitialData.initialEducation)
            val expJson = ResumeConverters.serializeExperience(InitialData.initialExperience)
            val projJson = ResumeConverters.serializeProjects(InitialData.initialProjects)
            val resetProfile = ResumeProfileEntity(
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
            repository.saveResumeProfile(resetProfile)
        }
    }

    // Helper to calculate resume strength (0..100)
    fun calculateResumeStrength(profile: ResumeProfileEntity?): Int {
        if (profile == null) return 0
        var score = 0
        if (profile.fullName.isNotBlank() && profile.email.isNotBlank()) score += 15
        if (profile.summary.isNotBlank() && profile.summary.length > 40) score += 15
        val eduList = ResumeConverters.parseEducation(profile.educationJson)
        if (eduList.isNotEmpty()) score += 20
        val expList = ResumeConverters.parseExperience(profile.experienceJson)
        if (expList.isNotEmpty()) score += 25
        val projList = ResumeConverters.parseProjects(profile.projectsJson)
        if (projList.isNotEmpty()) score += 15
        if (profile.skillsTechnical.isNotBlank()) score += 10
        return score.coerceIn(0, 100)
    }

    // Formatted ATS text generator for export/copy
    fun generatePlainTextResume(profile: ResumeProfileEntity?): String {
        if (profile == null) return ""
        val eduList = ResumeConverters.parseEducation(profile.educationJson)
        val expList = ResumeConverters.parseExperience(profile.experienceJson)
        val projList = ResumeConverters.parseProjects(profile.projectsJson)

        val sb = StringBuilder()
        sb.append("${profile.fullName.uppercase()}\n")
        sb.append("${profile.targetRole}\n")
        sb.append("${profile.email} | ${profile.phone} | ${profile.location}\n")
        if (profile.linkedin.isNotBlank()) sb.append("LinkedIn: ${profile.linkedin} | ")
        if (profile.github.isNotBlank()) sb.append("GitHub: ${profile.github} | ")
        if (profile.portfolio.isNotBlank()) sb.append("Portfolio: ${profile.portfolio}")
        sb.append("\n\n")

        sb.append("PROFESSIONAL SUMMARY\n")
        sb.append("--------------------\n")
        sb.append("${profile.summary}\n\n")

        sb.append("EDUCATION\n")
        sb.append("---------\n")
        eduList.forEach { edu ->
            sb.append("${edu.institution} — ${edu.degree} in ${edu.major} (${edu.gradYear})\n")
            if (edu.gpa.isNotBlank()) sb.append("GPA: ${edu.gpa} | ")
            if (edu.honors.isNotBlank()) sb.append("Honors: ${edu.honors}\n")
            sb.append("\n")
        }

        sb.append("EXPERIENCE & INTERNSHIPS\n")
        sb.append("------------------------\n")
        expList.forEach { exp ->
            sb.append("${exp.role} | ${exp.company} — ${exp.location} (${exp.dates})\n")
            exp.highlights.split("\n").filter { it.isNotBlank() }.forEach { bullet ->
                sb.append("• ${bullet.trim()}\n")
            }
            sb.append("\n")
        }

        sb.append("PROJECTS\n")
        sb.append("--------\n")
        projList.forEach { proj ->
            sb.append("${proj.title} [${proj.techStack}]\n")
            sb.append("${proj.description}\n")
            if (proj.link.isNotBlank()) sb.append("Link: ${proj.link}\n")
            sb.append("\n")
        }

        sb.append("TECHNICAL & PROFESSIONAL SKILLS\n")
        sb.append("-------------------------------\n")
        sb.append("Technical: ${profile.skillsTechnical}\n")
        sb.append("Professional: ${profile.skillsSoft}\n")
        if (profile.certifications.isNotBlank()) {
            sb.append("Certifications: ${profile.certifications}\n")
        }

        return sb.toString()
    }

    // ==========================================
    // --- Mock Interview Simulator Feature ---
    // ==========================================

    // Sub-tabs: 0: Simulator / Active Practice, 1: Questions Bank, 2: History
    private val _interviewSubTab = MutableStateFlow(0)
    val interviewSubTab: StateFlow<Int> = _interviewSubTab.asStateFlow()

    fun selectInterviewSubTab(tab: Int) {
        _interviewSubTab.value = tab
    }

    // Interview Questions Bank
    val interviewQuestions: List<InterviewQuestion> = InterviewQuestionsBank.questions

    private val _selectedInterviewCategory = MutableStateFlow("All Categories")
    val selectedInterviewCategory: StateFlow<String> = _selectedInterviewCategory.asStateFlow()

    fun selectInterviewCategory(category: String) {
        _selectedInterviewCategory.value = category
    }

    val filteredInterviewQuestions: StateFlow<List<InterviewQuestion>> = _selectedInterviewCategory.combine(
        MutableStateFlow(interviewQuestions)
    ) { category, allQuestions ->
        if (category == "All Categories") {
            allQuestions
        } else {
            allQuestions.filter { it.category.equals(category, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = interviewQuestions
    )

    // Active Question for Simulation
    private val _currentInterviewQuestion = MutableStateFlow<InterviewQuestion>(InterviewQuestionsBank.questions.first())
    val currentInterviewQuestion: StateFlow<InterviewQuestion> = _currentInterviewQuestion.asStateFlow()

    fun selectInterviewQuestion(question: InterviewQuestion) {
        _currentInterviewQuestion.value = question
        resetRecording()
        _interviewSubTab.value = 0
    }

    fun nextInterviewQuestion() {
        val list = interviewQuestions
        val currentIndex = list.indexOfFirst { it.id == _currentInterviewQuestion.value.id }
        val nextIndex = if (currentIndex in 0 until list.lastIndex) currentIndex + 1 else 0
        selectInterviewQuestion(list[nextIndex])
    }

    fun previousInterviewQuestion() {
        val list = interviewQuestions
        val currentIndex = list.indexOfFirst { it.id == _currentInterviewQuestion.value.id }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else list.lastIndex
        selectInterviewQuestion(list[prevIndex])
    }

    // User Response & Audio Recording Simulator
    private val _userResponseText = MutableStateFlow("")
    val userResponseText: StateFlow<String> = _userResponseText.asStateFlow()

    fun updateUserResponseText(text: String) {
        _userResponseText.value = text
    }

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()

    private var recordingJob: CoroutineJob? = null

    fun startRecording() {
        if (_isRecording.value) return
        _isRecording.value = true
        recordingJob?.cancel()
        recordingJob = viewModelScope.launch {
            while (_isRecording.value) {
                delay(1000)
                _recordingSeconds.value += 1
            }
        }
    }

    fun pauseRecording() {
        _isRecording.value = false
        recordingJob?.cancel()
    }

    fun resetRecording() {
        _isRecording.value = false
        recordingJob?.cancel()
        _recordingSeconds.value = 0
        _userResponseText.value = ""
        _analysisResult.value = null
    }

    // Analysis State
    private val _analysisResult = MutableStateFlow<InterviewAnalysisResult?>(null)
    val analysisResult: StateFlow<InterviewAnalysisResult?> = _analysisResult.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    fun analyzeCurrentResponse() {
        val response = _userResponseText.value.trim()
        val question = _currentInterviewQuestion.value

        _isAnalyzing.value = true
        viewModelScope.launch {
            // Short delay for visual smoothness
            delay(400)
            val result = InterviewAnalysisEngine.analyzeResponse(response, question)
            _analysisResult.value = result
            _isAnalyzing.value = false

            // Save to room history if response has substance
            if (response.isNotBlank()) {
                val duration = if (_recordingSeconds.value > 0) {
                    _recordingSeconds.value
                } else {
                    result.estimatedSpeakingDurationSeconds
                }

                repository.saveInterviewSession(
                    InterviewPracticeEntity(
                        questionId = question.id,
                        questionText = question.questionText,
                        category = question.category,
                        userResponse = response,
                        durationSeconds = duration,
                        wordCount = result.wordCount,
                        overallScore = result.overallScore,
                        lengthVerdict = result.lengthVerdict.label,
                        specificExamplesScore = result.specificExamplesScore,
                        suggestionsSummary = result.actionableSuggestions.joinToString(" • ")
                    )
                )
            }
        }
    }

    fun loadSampleAnswer(isHighScoring: Boolean) {
        val question = _currentInterviewQuestion.value
        val sampleText = if (isHighScoring) question.sampleAnswerGood else question.sampleAnswerBrief
        _userResponseText.value = sampleText
        val words = sampleText.split(Regex("\\s+")).size
        _recordingSeconds.value = ((words / 140.0) * 60).toInt().coerceAtLeast(15)
        _analysisResult.value = null
    }

    // Practice History
    val pastInterviewSessions: StateFlow<List<InterviewPracticeEntity>> = repository.interviewSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun deleteInterviewSession(id: Int) {
        viewModelScope.launch {
            repository.deleteInterviewSession(id)
        }
    }

    fun clearAllInterviewSessions() {
        viewModelScope.launch {
            repository.clearAllInterviewSessions()
        }
    }
}
