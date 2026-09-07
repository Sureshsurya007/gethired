package com.example

import com.example.data.interview.InterviewAnalysisEngine
import com.example.data.interview.InterviewQuestionsBank
import com.example.data.interview.LengthVerdict
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InterviewAnalysisEngineTest {

    @Test
    fun `questions bank has common entry-level questions with categories and guidance`() {
        val questions = InterviewQuestionsBank.questions
        assertTrue("Questions bank should have questions", questions.size >= 5)

        questions.forEach { q ->
            assertTrue("Question ID should not be blank", q.id.isNotBlank())
            assertTrue("Question text should not be blank", q.questionText.isNotBlank())
            assertTrue("Category should not be blank", q.category.isNotBlank())
            assertTrue("Guidance tip should not be blank", q.guidanceTip.isNotBlank())
            assertTrue("Expected elements should not be empty", q.expectedElements.isNotEmpty())
            assertTrue("Good sample answer should not be blank", q.sampleAnswerGood.isNotBlank())
            assertTrue("Brief sample answer should not be blank", q.sampleAnswerBrief.isNotBlank())
        }

        assertTrue(InterviewQuestionsBank.categories.contains("Behavioral & Teamwork"))
        assertTrue(InterviewQuestionsBank.categories.contains("General & Fit"))
    }

    @Test
    fun `short response triggers TOO_SHORT verdict and suggestions to expand`() {
        val briefText = "I graduated with a CS degree. I did some projects and want to work here."
        val result = InterviewAnalysisEngine.analyzeResponse(briefText)

        assertEquals(LengthVerdict.TOO_SHORT, result.lengthVerdict)
        assertTrue("Word count should be small", result.wordCount < 60)
        assertTrue("Estimated speaking duration should be short", result.estimatedSpeakingDurationSeconds < 30)
        assertTrue(
            "Actionable suggestions should recommend expanding or adding examples",
            result.actionableSuggestions.any { it.contains("Expand", ignoreCase = true) || it.contains("examples", ignoreCase = true) }
        )
    }

    @Test
    fun `optimal response with metrics and tools scores high on specificity and STAR`() {
        val detailedText = """
            During my senior year capstone project, our team of four was tasked with building an Android campus 
            event tracker. We noticed that user queries were taking over 3 seconds to return. 
            I investigated the issue and refactored our Room database indexing and implemented Kotlin coroutines 
            with Flow for background caching. As a result, we reduced query latency by 45%, successfully demoed 
            the application to 120 students, and received the department's top senior design award.
        """.trimIndent()

        val result = InterviewAnalysisEngine.analyzeResponse(detailedText)

        assertEquals(LengthVerdict.OPTIMAL, result.lengthVerdict)
        assertTrue("Word count should be in optimal range", result.wordCount in 60..260)
        assertTrue("Specific examples score should be high due to metrics and tools", result.specificExamplesScore >= 70)
        assertTrue("STAR score should reflect components", result.starScore >= 50)
        assertTrue("Overall score should be strong", result.overallScore >= 70)
        assertTrue("Strengths should mention metrics or pace", result.strengths.isNotEmpty())
    }

    @Test
    fun `filler word detection identifies common verbal crutches`() {
        val textWithFillers = """
            Um, basically during my internship, like, I was working on, you know, a website. 
            And actually, it was kind of hard, but like, I figured it out.
        """.trimIndent()

        val result = InterviewAnalysisEngine.analyzeResponse(textWithFillers)

        assertTrue("Should detect filler words", result.fillerWordCount >= 3)
        assertTrue("Should list detected filler words", result.detectedFillerWords.contains("like"))
        assertTrue(
            "Should include filler word guidance in feedback or suggestions",
            result.fillerWordFeedback.contains("filler", ignoreCase = true) ||
                result.actionableSuggestions.any { it.contains("filler", ignoreCase = true) }
        )
    }
}
