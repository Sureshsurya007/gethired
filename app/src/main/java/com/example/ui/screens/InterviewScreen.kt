package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.interview.InterviewQuestionsBank
import com.example.ui.GradLaunchViewModel
import com.example.ui.components.InterviewAnalysisCard
import com.example.ui.components.InterviewQuestionCard
import com.example.ui.components.PastInterviewSessionCard
import com.example.ui.components.RecordingControlPanel
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentGreenContainer

@Composable
fun InterviewScreen(
    viewModel: GradLaunchViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val subTab by viewModel.interviewSubTab.collectAsStateWithLifecycle()
    val currentQuestion by viewModel.currentInterviewQuestion.collectAsStateWithLifecycle()
    val userResponseText by viewModel.userResponseText.collectAsStateWithLifecycle()
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val recordingSeconds by viewModel.recordingSeconds.collectAsStateWithLifecycle()
    val analysisResult by viewModel.analysisResult.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedInterviewCategory.collectAsStateWithLifecycle()
    val filteredQuestions by viewModel.filteredInterviewQuestions.collectAsStateWithLifecycle()
    val pastSessions by viewModel.pastInterviewSessions.collectAsStateWithLifecycle()

    val allQuestions = viewModel.interviewQuestions
    val currentIndex = allQuestions.indexOfFirst { it.id == currentQuestion.id }.coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("screen_interview_simulator")
    ) {
        // Top Section: Sub-Tabs (Simulator / Questions / History)
        TabRow(
            selectedTabIndex = subTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[subTab]),
                    color = MaterialTheme.colorScheme.primary,
                    height = 3.dp
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = subTab == 0,
                onClick = { viewModel.selectInterviewSubTab(0) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simulator", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                },
                modifier = Modifier.testTag("tab_interview_simulator")
            )
            Tab(
                selected = subTab == 1,
                onClick = { viewModel.selectInterviewSubTab(1) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QuestionAnswer,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Question Bank", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                },
                modifier = Modifier.testTag("tab_interview_questions")
            )
            Tab(
                selected = subTab == 2,
                onClick = { viewModel.selectInterviewSubTab(2) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (pastSessions.isNotEmpty()) "History (${pastSessions.size})" else "History",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                },
                modifier = Modifier.testTag("tab_interview_history")
            )
        }

        when (subTab) {
            0 -> {
                // Active Simulator View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Banner
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Entry-Level Mock Interview Simulator",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Record responses to common graduate questions & receive real-time analysis on response length and examples.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Question Card
                    item {
                        InterviewQuestionCard(
                            question = currentQuestion,
                            currentIndex = currentIndex,
                            totalCount = allQuestions.size,
                            onPrevious = { viewModel.previousInterviewQuestion() },
                            onNext = { viewModel.nextInterviewQuestion() }
                        )
                    }

                    // Recording Controls & Input
                    item {
                        RecordingControlPanel(
                            isRecording = isRecording,
                            recordingSeconds = recordingSeconds,
                            userResponseText = userResponseText,
                            onTextChange = { viewModel.updateUserResponseText(it) },
                            onStartRecording = {
                                viewModel.startRecording()
                                Toast.makeText(context, "Recording started... Speak your answer.", Toast.LENGTH_SHORT).show()
                            },
                            onPauseRecording = {
                                viewModel.pauseRecording()
                                Toast.makeText(context, "Recording paused.", Toast.LENGTH_SHORT).show()
                            },
                            onReset = {
                                viewModel.resetRecording()
                                Toast.makeText(context, "Simulator reset.", Toast.LENGTH_SHORT).show()
                            },
                            onFinishAndAnalyze = {
                                viewModel.analyzeCurrentResponse()
                            },
                            onLoadSampleAnswer = { isHighScoring ->
                                viewModel.loadSampleAnswer(isHighScoring)
                                Toast.makeText(
                                    context,
                                    if (isHighScoring) "Loaded STAR Graduate Sample Answer" else "Loaded Brief Answer Sample",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                    // Analyzing Indicator
                    if (isAnalyzing) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.5.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Evaluating response length, specific metrics, & STAR structure...",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Detailed Analysis & Coaching Feedback
                    analysisResult?.let { result ->
                        item {
                            InterviewAnalysisCard(
                                result = result,
                                onNextQuestion = {
                                    viewModel.nextInterviewQuestion()
                                    Toast.makeText(context, "Moved to next question", Toast.LENGTH_SHORT).show()
                                },
                                onRetake = {
                                    viewModel.resetRecording()
                                    Toast.makeText(context, "Ready to re-record answer", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

            1 -> {
                // Questions Bank View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Common Entry-Level Questions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Select any question to practice your response in the simulator.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Filter Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InterviewQuestionsBank.categories.forEach { category ->
                                val selected = selectedCategory.equals(category, ignoreCase = true)
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.selectInterviewCategory(category) },
                                    label = { Text(category, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    items(filteredQuestions, key = { it.id }) { q ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("question_item_${q.id}")
                                .clickable {
                                    viewModel.selectInterviewQuestion(q)
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(
                                1.dp,
                                if (q.id == currentQuestion.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = q.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }

                                    Text(
                                        text = q.difficulty,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = q.questionText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = q.guidanceTip,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp,
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        viewModel.selectInterviewQuestion(q)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (q.id == currentQuestion.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (q.id == currentQuestion.id) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (q.id == currentQuestion.id) "Currently Active • Practice Now" else "Practice This Question",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Practice History View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Practice History",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Track improvement across simulated interview sessions",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (pastSessions.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.clearAllInterviewSessions()
                                        Toast.makeText(context, "History cleared", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("Clear All", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    if (pastSessions.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "No Practice Sessions Yet",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Head to the Simulator to record your first answer and see detailed feedback.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { viewModel.selectInterviewSubTab(0) },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Start First Simulation", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        items(pastSessions, key = { it.id }) { session ->
                            PastInterviewSessionCard(
                                session = session,
                                onDelete = { viewModel.deleteInterviewSession(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
