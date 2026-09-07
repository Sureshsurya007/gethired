package com.example.data.interview

data class StarComponentStatus(
    val title: String,
    val isPresent: Boolean,
    val feedback: String
)

data class InterviewAnalysisResult(
    val wordCount: Int,
    val characterCount: Int,
    val estimatedSpeakingDurationSeconds: Int,
    val lengthVerdict: LengthVerdict,
    val lengthFeedback: String,
    val specificExamplesScore: Int, // 0 to 100
    val specificExamplesFeedback: String,
    val starBreakdown: List<StarComponentStatus>,
    val starScore: Int, // 0 to 100
    val fillerWordCount: Int,
    val detectedFillerWords: List<String>,
    val fillerWordFeedback: String,
    val overallScore: Int, // 0 to 100
    val actionableSuggestions: List<String>,
    val strengths: List<String>
)

enum class LengthVerdict(val label: String) {
    TOO_SHORT("Underdeveloped (< 60 words)"),
    OPTIMAL("Optimal Length (60–260 words)"),
    SLIGHTLY_LONG("Slightly Long (260–380 words)"),
    TOO_LONG("Overly Long (> 380 words)")
}

object InterviewAnalysisEngine {

    private val fillerWordsRegex = Regex(
        "\\b(um|uh|like|basically|actually|literally|sort of|kind of|you know|honestly|i mean)\\b",
        RegexOption.IGNORE_CASE
    )

    private val metricsAndNumbersRegex = Regex(
        "(\\d+%|\\d+\\s*(users|percent|seconds|hours|days|weeks|months|members|engineers|students|screens|sprints)|\\b(increased|reduced|decreased|accelerated|improved|optimized|delivered|achieved|saved)\\b|\\b\\d+\\b)",
        RegexOption.IGNORE_CASE
    )

    private val techAndToolsRegex = Regex(
        "\\b(kotlin|java|python|javascript|typescript|c\\+\\+|sql|room|compose|react|figma|git|github|docker|aws|firebase|api|rest|jira|notion|scrum|agile|unit test|mvvm|profiler)\\b",
        RegexOption.IGNORE_CASE
    )

    private val situationKeywords = Regex(
        "\\b(when|during|internship|project|semester|class|capstone|coursework|team|company|client|started|last year|junior|senior|university)\\b",
        RegexOption.IGNORE_CASE
    )

    private val taskKeywords = Regex(
        "\\b(task|goal|objective|needed to|had to|responsible for|assigned|challenge|problem|requirement|deadline|milestone)\\b",
        RegexOption.IGNORE_CASE
    )

    private val actionKeywords = Regex(
        "\\b(i built|i developed|i created|i designed|i implemented|i proposed|i researched|i investigated|i isolated|i organized|i scheduled|i refactored|i tested|i collaborated|i analyzed|i reached out)\\b",
        RegexOption.IGNORE_CASE
    )

    private val resultKeywords = Regex(
        "\\b(result|outcome|learned|achieved|successfully|completed|delivered|improved|resolved|reduced|eliminated|grade|score|won|taught me|praised|positive feedback)\\b",
        RegexOption.IGNORE_CASE
    )

    fun analyzeResponse(responseText: String, question: InterviewQuestion? = null): InterviewAnalysisResult {
        val trimmed = responseText.trim()
        val words = if (trimmed.isBlank()) emptyList() else trimmed.split(Regex("\\s+"))
        val wordCount = words.size
        val charCount = trimmed.length

        // Speaking duration: standard conversational pacing is ~135-150 words per minute (~2.3 words/sec)
        val estimatedDuration = if (wordCount == 0) 0 else ((wordCount / 140.0) * 60).toInt().coerceAtLeast(5)

        // 1. Length Analysis
        val (lengthVerdict, lengthFeedback) = when {
            wordCount < 35 -> LengthVerdict.TOO_SHORT to
                "Your answer is very brief (${wordCount} words, ~${estimatedDuration}s). Entry-level hiring managers need sufficient context and technical depth to evaluate your problem-solving abilities."
            wordCount < 60 -> LengthVerdict.TOO_SHORT to
                "Your response is on the short side (${wordCount} words, ~${estimatedDuration}s). While concise, adding more context on your specific actions and results will make a much stronger impression."
            wordCount <= 260 -> LengthVerdict.OPTIMAL to
                "Ideal response length (${wordCount} words, ~${formatDuration(estimatedDuration)}). You provided meaningful depth while maintaining a focused, engaging pace suited for interviewers."
            wordCount <= 380 -> LengthVerdict.SLIGHTLY_LONG to
                "Comprehensive answer (${wordCount} words, ~${formatDuration(estimatedDuration)}). Good detail, but be cautious of losing the interviewer's attention on minor implementation points."
            else -> LengthVerdict.TOO_LONG to
                "Your answer is quite lengthy (${wordCount} words, ~${formatDuration(estimatedDuration)}). In live interviews, responses exceeding 3 minutes can sound like rambling. Try to summarize earlier phases."
        }

        // 2. Specific Examples & Evidence Detection
        val metricMatches = metricsAndNumbersRegex.findAll(trimmed).map { it.value }.toList()
        val techMatches = techAndToolsRegex.findAll(trimmed).map { it.value }.toList()
        val hasMetrics = metricMatches.isNotEmpty()
        val hasTechTools = techMatches.isNotEmpty()

        val specificExamplesScore = when {
            hasMetrics && hasTechTools && wordCount >= 60 -> 95
            (hasMetrics || hasTechTools) && wordCount >= 50 -> 75
            wordCount >= 40 -> 45
            else -> 25
        }

        val specificExamplesFeedback = when {
            specificExamplesScore >= 85 ->
                "Outstanding specificity! You supported your claims with concrete indicators (${metricMatches.take(3).joinToString(", ")}) and relevant technologies (${techMatches.take(3).joinToString(", ")})."
            specificExamplesScore >= 60 ->
                "Moderate specificity. You mentioned useful context, but could enhance credibility by adding quantified outcomes (e.g., 'reduced memory spikes' or 'collaborated with a team of 4')."
            else ->
                "Lacks concrete evidence. Avoid speaking in generalities like 'I did some work' or 'it was fixed'. Name the specific coursework, tool names (e.g., Git, Kotlin), and measurable results."
        }

        // 3. STAR Framework Analysis
        val hasSituation = situationKeywords.containsMatchIn(trimmed)
        val hasTask = taskKeywords.containsMatchIn(trimmed)
        val hasAction = actionKeywords.containsMatchIn(trimmed) || Regex("\\b(i\\s+\\w+ed)\\b", RegexOption.IGNORE_CASE).containsMatchIn(trimmed)
        val hasResult = resultKeywords.containsMatchIn(trimmed)

        val starList = listOf(
            StarComponentStatus(
                title = "Situation",
                isPresent = hasSituation,
                feedback = if (hasSituation) "Clear context & setting established." else "Missing clear scenario or project context."
            ),
            StarComponentStatus(
                title = "Task",
                isPresent = hasTask,
                feedback = if (hasTask) "Identified the core objective or problem." else "Clarify your specific responsibility or goal."
            ),
            StarComponentStatus(
                title = "Action",
                isPresent = hasAction,
                feedback = if (hasAction) "Highlighted your direct contributions." else "Focus on 'I' statements describing steps you personally executed."
            ),
            StarComponentStatus(
                title = "Result",
                isPresent = hasResult,
                feedback = if (hasResult) "Concluded with the impact or learning." else "State the final outcome, metric, or key takeaway."
            )
        )

        val starPresentCount = starList.count { it.isPresent }
        val starScore = (starPresentCount * 25).coerceIn(0, 100)

        // 4. Filler Words Analysis
        val fillerMatches = fillerWordsRegex.findAll(trimmed).map { it.value.lowercase() }.toList()
        val fillerWordCount = fillerMatches.size
        val uniqueFillers = fillerMatches.distinct()

        val fillerWordFeedback = when {
            fillerWordCount == 0 -> "Clean delivery! No distracting verbal crutches detected."
            fillerWordCount <= 2 -> "Good verbal poise with minimal filler phrasing."
            else -> "Detected $fillerWordCount filler words (${uniqueFillers.joinToString(", ")}). Practice comfortable, silent pauses instead of filler words."
        }

        // 5. Strengths & Actionable Improvement Suggestions
        val strengths = mutableListOf<String>()
        val suggestions = mutableListOf<String>()

        if (lengthVerdict == LengthVerdict.OPTIMAL) {
            strengths.add("Well-calibrated speaking pace and duration (approx. ${formatDuration(estimatedDuration)}).")
        }
        if (hasMetrics) {
            strengths.add("Quantified your impact with concrete figures or measurable outcomes.")
        }
        if (hasTechTools) {
            strengths.add("Referenced industry-standard tools and technologies appropriately.")
        }
        if (hasAction) {
            strengths.add("Emphasized proactive personal initiative rather than passive involvement.")
        }
        if (strengths.isEmpty()) {
            strengths.add("Good baseline start! The framework is ready to be expanded with richer details.")
        }

        // Generate tailored suggestions
        if (lengthVerdict == LengthVerdict.TOO_SHORT) {
            suggestions.add("Expand your answer: Aim for 100–200 words. Describe what challenges you faced and why your solution mattered.")
        } else if (lengthVerdict == LengthVerdict.TOO_LONG) {
            suggestions.add("Be more concise: Condense introductory setup and get to your primary action and result faster.")
        }

        if (specificExamplesScore < 70) {
            suggestions.add("Provide concrete examples: Name the exact project, tools (e.g., Git, SQL, Kotlin), or metrics (e.g., 'improved speed by 20%') instead of general statements.")
        }

        if (!hasResult) {
            suggestions.add("Highlight the Result: Conclude with what was delivered, grade received, client feedback, or a key lesson learned.")
        }

        if (!hasAction) {
            suggestions.add("Use strong action verbs: Use 'I investigated', 'I proposed', 'I refactored' to clearly communicate your individual role.")
        }

        if (fillerWordCount >= 3) {
            suggestions.add("Reduce filler words: Replace words like '${uniqueFillers.firstOrNull() ?: "basically"}' with deliberate 1-second pauses.")
        }

        if (suggestions.isEmpty()) {
            suggestions.add("Polish your transition: In live interviews, conclude smoothly with: 'And that experience solidified my passion for this domain.'")
        }

        // Calculate Overall Score
        var rawScore = 0
        // Length factor (30% weight)
        rawScore += when (lengthVerdict) {
            LengthVerdict.OPTIMAL -> 30
            LengthVerdict.SLIGHTLY_LONG -> 24
            LengthVerdict.TOO_SHORT -> if (wordCount < 40) 10 else 18
            LengthVerdict.TOO_LONG -> 18
        }
        // Specific Examples factor (35% weight)
        rawScore += (specificExamplesScore * 0.35).toInt()
        // STAR factor (25% weight)
        rawScore += (starScore * 0.25).toInt()
        // Filler words factor (10% weight)
        rawScore += when {
            fillerWordCount == 0 -> 10
            fillerWordCount <= 2 -> 7
            else -> 4
        }

        val overallScore = rawScore.coerceIn(15, 98)

        return InterviewAnalysisResult(
            wordCount = wordCount,
            characterCount = charCount,
            estimatedSpeakingDurationSeconds = estimatedDuration,
            lengthVerdict = lengthVerdict,
            lengthFeedback = lengthFeedback,
            specificExamplesScore = specificExamplesScore,
            specificExamplesFeedback = specificExamplesFeedback,
            starBreakdown = starList,
            starScore = starScore,
            fillerWordCount = fillerWordCount,
            detectedFillerWords = uniqueFillers,
            fillerWordFeedback = fillerWordFeedback,
            overallScore = overallScore,
            actionableSuggestions = suggestions.take(3),
            strengths = strengths.take(3)
        )
    }

    private fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
    }
}
