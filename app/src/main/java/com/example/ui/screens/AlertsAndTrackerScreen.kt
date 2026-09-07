package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.local.AlertNotificationEntity
import com.example.data.local.AlertPreferenceEntity
import com.example.data.local.JobEntity
import com.example.ui.GradLaunchViewModel
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentGreen

@Composable
fun AlertsAndTrackerScreen(
    viewModel: GradLaunchViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentSubTab by remember { mutableIntStateOf(0) } // 0: Personalized Alerts, 1: Career Tracker

    val alertPrefs by viewModel.alertPreferences.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val allJobs by viewModel.rawJobs.collectAsStateWithLifecycle()
    val allEvents by viewModel.rawEvents.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab switcher
        TabRow(
            selectedTabIndex = currentSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = currentSubTab == 0,
                onClick = { currentSubTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Personalized Alerts", fontWeight = FontWeight.Bold)
                        if (unreadCount > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(AccentCoral)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.testTag("tab_alerts")
            )
            Tab(
                selected = currentSubTab == 1,
                onClick = { currentSubTab = 1 },
                text = { Text("Career Tracker", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_career_tracker")
            )
        }

        if (currentSubTab == 0) {
            // Alerts View
            AlertsSubScreen(
                prefs = alertPrefs ?: AlertPreferenceEntity(),
                notifications = notifications,
                onSavePrefs = {
                    viewModel.updateAlertPreferences(it)
                    Toast.makeText(context, "Alert preferences updated!", Toast.LENGTH_SHORT).show()
                },
                onMarkRead = { viewModel.markNotificationAsRead(it) },
                onMarkAllRead = {
                    viewModel.markAllNotificationsAsRead()
                    Toast.makeText(context, "All notifications marked as read.", Toast.LENGTH_SHORT).show()
                },
                onTriggerSimulatedAlert = {
                    viewModel.triggerSimulatedCareerAlert()
                    Toast.makeText(context, "New alert triggered!", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // Tracker View
            CareerTrackerSubScreen(
                jobs = allJobs,
                onStatusChange = { id, status, title, comp ->
                    viewModel.updateJobApplicationStatus(id, status, title, comp)
                },
                onSelectJob = { viewModel.selectJob(it) }
            )
        }
    }
}

@Composable
fun AlertsSubScreen(
    prefs: AlertPreferenceEntity,
    notifications: List<AlertNotificationEntity>,
    onSavePrefs: (AlertPreferenceEntity) -> Unit,
    onMarkRead: (Int) -> Unit,
    onMarkAllRead: () -> Unit,
    onTriggerSimulatedAlert: () -> Unit
) {
    var isEditingPrefs by remember { mutableStateOf(false) }

    var keywords by remember(prefs) { mutableStateOf(prefs.targetRoleKeywords) }
    var city by remember(prefs) { mutableStateOf(prefs.preferredCity) }
    var minSalary by remember(prefs) { mutableStateOf(prefs.minSalary.toFloat()) }
    var instantJobs by remember(prefs) { mutableStateOf(prefs.instantJobAlerts) }
    var eventAlerts by remember(prefs) { mutableStateOf(prefs.localEventAlerts) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Preferences Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Personalized Match Criteria",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Alerts triggered whenever new roles match",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = { isEditingPrefs = !isEditingPrefs },
                            modifier = Modifier.testTag("toggle_edit_alerts_button")
                        ) {
                            Icon(
                                imageVector = if (isEditingPrefs) Icons.Default.Check else Icons.Default.Settings,
                                contentDescription = "Edit Alerts",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isEditingPrefs) {
                        // Summary View
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Target Roles: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(prefs.targetRoleKeywords, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Metro Location: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(prefs.preferredCity, fontSize = 12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Min Desired Salary: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("$${String.format("%,d", prefs.minSalary)} / yr", fontSize = 12.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Alert Channels: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(
                                    "${if (prefs.instantJobAlerts) "Instant Jobs" else ""}${if (prefs.instantJobAlerts && prefs.localEventAlerts) " + " else ""}${if (prefs.localEventAlerts) "Local Events" else ""}",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        // Editing Inputs
                        Column {
                            OutlinedTextField(
                                value = keywords,
                                onValueChange = { keywords = it },
                                label = { Text("Target Roles / Keywords") },
                                modifier = Modifier.fillMaxWidth().testTag("input_alert_keywords"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("Preferred Metro City") },
                                modifier = Modifier.fillMaxWidth().testTag("input_alert_city"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Minimum Desired Starting Salary: $${String.format("%,d", minSalary.toInt())}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Slider(
                                value = minSalary,
                                onValueChange = { minSalary = it },
                                valueRange = 50000f..120000f,
                                steps = 13,
                                modifier = Modifier.fillMaxWidth().testTag("slider_min_salary")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Instant Entry-Level Job Alerts", style = MaterialTheme.typography.bodyMedium)
                                Switch(
                                    checked = instantJobs,
                                    onCheckedChange = { instantJobs = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Local Networking Event Alerts", style = MaterialTheme.typography.bodyMedium)
                                Switch(
                                    checked = eventAlerts,
                                    onCheckedChange = { eventAlerts = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.secondary)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    onSavePrefs(
                                        prefs.copy(
                                            targetRoleKeywords = keywords,
                                            preferredCity = city,
                                            minSalary = minSalary.toInt(),
                                            instantJobAlerts = instantJobs,
                                            localEventAlerts = eventAlerts
                                        )
                                    )
                                    isEditingPrefs = false
                                },
                                modifier = Modifier.fillMaxWidth().testTag("save_alert_prefs_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Save Alert Preferences")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Test instant notification engine:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        OutlinedButton(
                            onClick = onTriggerSimulatedAlert,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("trigger_test_alert_button")
                        ) {
                            Text("Trigger Alert", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Notification List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Alert Notifications Feed (${notifications.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                if (notifications.any { !it.isRead }) {
                    Text(
                        text = "Mark all as read",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable(onClick = onMarkAllRead)
                            .testTag("mark_all_read_button")
                    )
                }
            }
        }

        items(notifications, key = { it.id }) { item ->
            AlertNotificationCard(
                notification = item,
                onMarkRead = { onMarkRead(item.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AlertNotificationCard(
    notification: AlertNotificationEntity,
    onMarkRead: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onMarkRead)
            .testTag("alert_item_${notification.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (notification.type) {
                            "JOB" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            "EVENT" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.type) {
                        "JOB" -> Icons.Default.Work
                        "EVENT" -> Icons.Default.Event
                        else -> Icons.Default.NotificationsActive
                    },
                    contentDescription = null,
                    tint = when (notification.type) {
                        "JOB" -> MaterialTheme.colorScheme.primary
                        "EVENT" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.tertiary
                    },
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold
                    )
                    Text(
                        text = notification.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CareerTrackerSubScreen(
    jobs: List<JobEntity>,
    onStatusChange: (Int, String, String, String) -> Unit,
    onSelectJob: (JobEntity) -> Unit
) {
    val appliedJobs = jobs.filter { it.applicationStatus == "APPLIED" }
    val interviewingJobs = jobs.filter { it.applicationStatus == "INTERVIEWING" }
    val offeredJobs = jobs.filter { it.applicationStatus == "OFFERED" }
    val savedJobs = jobs.filter { it.isSaved || it.applicationStatus == "SAVED" }

    val activeTrackedJobs = jobs.filter { it.applicationStatus != "NONE" || it.isSaved }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Funnel Pipeline Dashboard
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Career Application Pipeline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Track your recent graduate journey from lead to offer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PipelineStageColumn(label = "Saved", count = savedJobs.size, color = MaterialTheme.colorScheme.primary)
                        PipelineStageColumn(label = "Applied", count = appliedJobs.size, color = AccentBlue)
                        PipelineStageColumn(label = "Interview", count = interviewingJobs.size, color = MaterialTheme.colorScheme.tertiary)
                        PipelineStageColumn(label = "Offers 🎉", count = offeredJobs.size, color = AccentGreen)
                    }
                }
            }
        }

        item {
            Text(
                text = "Tracked Opportunities (${activeTrackedJobs.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (activeTrackedJobs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No tracked applications yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Browse entry-level jobs and tap 'Apply' or 'Save' to monitor your career progress here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(activeTrackedJobs, key = { it.id }) { job ->
                TrackedJobItem(
                    job = job,
                    onStatusChange = { newStatus ->
                        onStatusChange(job.id, newStatus, job.title, job.company)
                    },
                    onSelectJob = { onSelectJob(job) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PipelineStageColumn(label: String, count: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TrackedJobItem(
    job: JobEntity,
    onStatusChange: (String) -> Unit,
    onSelectJob: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Column(modifier = Modifier.weight(1f).clickable(onClick = onSelectJob)) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${job.company} • ${job.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Stage Changer Dropdown
                Box {
                    OutlinedButton(
                        onClick = { showMenu = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp).testTag("status_pill_${job.id}")
                    ) {
                        Text(
                            text = if (job.applicationStatus == "NONE") "Saved" else job.applicationStatus,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        listOf("SAVED", "APPLIED", "INTERVIEWING", "OFFERED", "NONE").forEach { statusOption ->
                            DropdownMenuItem(
                                text = { Text(statusOption) },
                                onClick = {
                                    onStatusChange(statusOption)
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Salary: ${job.salaryRange}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "View details →",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onSelectJob)
                )
            }
        }
    }
}
