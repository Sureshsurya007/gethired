package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.interview.InterviewAnalysisResult
import com.example.data.interview.InterviewQuestion
import com.example.data.interview.LengthVerdict
import com.example.data.interview.StarComponentStatus
import com.example.data.local.InterviewPracticeEntity
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentBlueContainer
import com.example.ui.theme.AccentBlueOnContainer
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentCoralContainer
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentGreenContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Animated Audio Waveform Visualizer ---
@Composable
fun AudioWaveformVisualizer(
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_transition")
    val heights = listOf(0.4f, 0.7f, 0.3f, 0.9f, 0.5f, 0.85f, 0.45f, 0.95f, 0.6f, 0.35f, 0.8f, 0.5f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.forEachIndexed { index, baseHeight ->
            val scale by infiniteTransition.animateFloat(
                initialValue = if (isRecording) 0.2f else 0.15f,
                targetValue = if (isRecording) baseHeight else 0.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 350 + (index * 45),
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 2.5.dp)
                    .width(4.dp)
                    .height((48 * scale).dp.coerceAtLeast(6.dp))
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (isRecording) {
                            if (index % 2 == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        }
                    )
            )
        }
    }
}

// --- Active Interview Question Header Card ---
@Composable
fun InterviewQuestionCard(
    question: InterviewQuestion,
    currentIndex: Int,
    totalCount: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedGuidance by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("interview_question_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Category Pill & Navigation Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = question.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_prev_question")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Question",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "${currentIndex + 1} of $totalCount",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = onNext,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_next_question")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Question",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Question Text
            Text(
                text = "\"${question.questionText}\"",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Expandable Interviewer Expectations Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { expandedGuidance = !expandedGuidance },
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "What Interviewers Look For",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Icon(
                            imageVector = if (expandedGuidance) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    AnimatedVisibility(visible = expandedGuidance) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = question.guidanceTip,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Key Elements to Include:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            question.expectedElements.forEach { element ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = element,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Recording & Input Controls Panel ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecordingControlPanel(
    isRecording: Boolean,
    recordingSeconds: Int,
    userResponseText: String,
    onTextChange: (String) -> Unit,
    onStartRecording: () -> Unit,
    onPauseRecording: () -> Unit,
    onReset: () -> Unit,
    onFinishAndAnalyze: () -> Unit,
    onLoadSampleAnswer: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val minutes = recordingSeconds / 60
    val seconds = recordingSeconds % 60
    val formattedTime = String.format(Locale.US, "%02d:%02d", minutes, seconds)
    val wordCount = if (userResponseText.isBlank()) 0 else userResponseText.trim().split(Regex("\\s+")).size

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recording_control_panel"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Timer & Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) AccentCoral else MaterialTheme.colorScheme.outline)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRecording) "Recording Answer..." else if (recordingSeconds > 0) "Paused" else "Ready to Record",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRecording) AccentCoral else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Timer Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedTime,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Audio Waveform Equalizer
            AudioWaveformVisualizer(isRecording = isRecording)

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Mic & Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                OutlinedButton(
                    onClick = onReset,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("btn_reset_recording"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Recording",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Central Record / Pause Button
                Button(
                    onClick = {
                        if (isRecording) onPauseRecording() else onStartRecording()
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) AccentCoral else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .size(64.dp)
                        .testTag("btn_toggle_recording")
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Pause else Icons.Default.Mic,
                        contentDescription = if (isRecording) "Pause" else "Record",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Finish / Check Button
                Button(
                    onClick = onFinishAndAnalyze,
                    enabled = userResponseText.isNotBlank() || recordingSeconds > 0,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("btn_quick_analyze"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Analyze",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Response Text Field (Live Transcript or Typed Answer)
            OutlinedTextField(
                value = userResponseText,
                onValueChange = onTextChange,
                label = { Text("Your Spoken Response / Transcript") },
                placeholder = { Text("Speak or type your answer here. You can also tap the sample answer buttons below to test response analysis...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .testTag("input_interview_response"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Word count and Quick Samples Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$wordCount words • est. ${((wordCount / 140.0) * 60).toInt()}s",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row {
                    Text(
                        text = "Try sample: ",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Text(
                        text = "High-Scoring",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onLoadSampleAnswer(true) }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .testTag("btn_sample_high_scoring")
                    )
                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                    Text(
                        text = "Too Short",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCoral,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onLoadSampleAnswer(false) }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .testTag("btn_sample_too_short")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Finish & Analyze Full Button
            Button(
                onClick = onFinishAndAnalyze,
                enabled = userResponseText.isNotBlank() || recordingSeconds > 0,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_finish_and_analyze"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Analyze Response Length & Quality",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// --- Comprehensive Feedback & Analysis Card ---
@Composable
fun InterviewAnalysisCard(
    result: InterviewAnalysisResult,
    onNextQuestion: () -> Unit,
    onRetake: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("interview_analysis_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Overall Readiness Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Response Analysis",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time evaluation & personalized coaching",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Overall Score Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                result.overallScore >= 80 -> AccentGreenContainer
                                result.overallScore >= 60 -> AccentBlueContainer
                                else -> AccentCoralContainer
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${result.overallScore}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = when {
                                result.overallScore >= 80 -> AccentGreen
                                result.overallScore >= 60 -> AccentBlue
                                else -> AccentCoral
                            }
                        )
                        Text(
                            text = "READINESS",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                result.overallScore >= 80 -> AccentGreen
                                result.overallScore >= 60 -> AccentBlue
                                else -> AccentCoral
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Response Length & Pacing Evaluation (KEY REQUIREMENT)
            Text(
                text = "1. Response Length & Speaking Duration",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${result.wordCount} words",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• ~${result.estimatedSpeakingDurationSeconds}s speaking time",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Verdict Badge
                        val verdictColor = when (result.lengthVerdict) {
                            LengthVerdict.OPTIMAL -> AccentGreen
                            LengthVerdict.SLIGHTLY_LONG -> AccentBlue
                            LengthVerdict.TOO_SHORT, LengthVerdict.TOO_LONG -> AccentCoral
                        }
                        val verdictBg = when (result.lengthVerdict) {
                            LengthVerdict.OPTIMAL -> AccentGreenContainer
                            LengthVerdict.SLIGHTLY_LONG -> AccentBlueContainer
                            LengthVerdict.TOO_SHORT, LengthVerdict.TOO_LONG -> AccentCoralContainer
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(verdictBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = when (result.lengthVerdict) {
                                    LengthVerdict.OPTIMAL -> "Optimal Length"
                                    LengthVerdict.TOO_SHORT -> "Underdeveloped"
                                    LengthVerdict.SLIGHTLY_LONG -> "Slightly Long"
                                    LengthVerdict.TOO_LONG -> "Too Long"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = verdictColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = result.lengthFeedback,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Specific Examples & Evidence (KEY REQUIREMENT)
            Text(
                text = "2. Specific Examples & Tangible Evidence",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Specificity Score: ${result.specificExamplesScore}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (result.specificExamplesScore >= 70) AccentGreen else MaterialTheme.colorScheme.primary
                        )
                        LinearProgressIndicator(
                            progress = { result.specificExamplesScore / 100f },
                            modifier = Modifier
                                .width(100.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (result.specificExamplesScore >= 70) AccentGreen else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = result.specificExamplesFeedback,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. STAR Framework Breakdown
            Text(
                text = "3. STAR Method Structure",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                result.starBreakdown.forEach { comp ->
                    StarComponentBadge(
                        status = comp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Actionable Areas for Improvement
            Text(
                text = "Suggested Areas for Improvement",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            result.actionableSuggestions.forEachIndexed { index, suggestion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = suggestion,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Verbal Delivery & Filler Words
            if (result.fillerWordCount > 0) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = AccentCoralContainer.copy(alpha = 0.35f)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AccentCoral,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = result.fillerWordFeedback,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom Actions: Retake vs Next Question
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRetake,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_retake_interview")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Re-record", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNextQuestion,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_next_interview_question"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Next Question", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StarComponentBadge(
    status: StarComponentStatus,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (status.isPresent) AccentGreenContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            1.dp,
            if (status.isPresent) AccentGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (status.isPresent) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (status.isPresent) AccentGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = status.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (status.isPresent) AccentGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Past Interview Session History Card ---
@Composable
fun PastInterviewSessionCard(
    session: InterviewPracticeEntity,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()) }
    val formattedDate = remember(session.timestamp) { dateFormat.format(Date(session.timestamp)) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("past_session_${session.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category & Date
                Column {
                    Text(
                        text = session.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Score pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (session.overallScore >= 75) AccentGreenContainer else MaterialTheme.colorScheme.primaryContainer
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${session.overallScore}% Score",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (session.overallScore >= 75) AccentGreen else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Question summary
            Text(
                text = session.questionText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = if (expanded) 10 else 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${session.wordCount} words • ${session.durationSeconds}s duration • ${session.lengthVerdict}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(
                    onClick = { onDelete(session.id) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Session",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "Your Recorded Answer:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = session.userResponse,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Suggestions:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = session.suggestionsSummary,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Expand / Collapse toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "Show Less" else "View Full Answer & Analysis",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
