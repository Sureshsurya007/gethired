package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.GradLaunchViewModel
import com.example.ui.components.JobCard
import com.example.ui.components.JobDetailDialog

@Composable
fun JobsScreen(
    viewModel: GradLaunchViewModel,
    modifier: Modifier = Modifier
) {
    val jobs by viewModel.filteredJobs.collectAsStateWithLifecycle()
    val searchQuery by viewModel.jobSearchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedWorkMode by viewModel.selectedWorkMode.collectAsStateWithLifecycle()
    val onlySaved by viewModel.onlySavedJobs.collectAsStateWithLifecycle()
    val selectedJob by viewModel.selectedJob.collectAsStateWithLifecycle()

    val categories = listOf(
        "All",
        "Engineering & Tech",
        "Design & UX",
        "Finance & Business",
        "Data & AI",
        "Marketing & Growth"
    )

    val workModes = listOf("All", "Hybrid", "Remote", "On-site")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search & Filter Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setJobSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("job_search_input"),
                placeholder = { Text("Search entry-level jobs, titles, skills...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setJobSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Categories horizontal scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setJobCategory(category) },
                        label = { Text(category, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("filter_category_${category.replace(" ", "_")}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Work mode and Saved toggle row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                workModes.forEach { mode ->
                    val isSelected = selectedWorkMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setJobWorkMode(mode) },
                        label = { Text(if (mode == "All") "Mode: All" else mode, fontSize = 12.sp) },
                        modifier = Modifier.testTag("filter_mode_$mode")
                    )
                }

                FilterChip(
                    selected = onlySaved,
                    onClick = { viewModel.toggleOnlySavedJobs() },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    label = { Text("Saved Only", fontSize = 12.sp) },
                    modifier = Modifier.testTag("filter_saved_only")
                )
            }
        }

        // Job List Section
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${jobs.size} Opportunities Found",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Tailored for Recent Grads",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (jobs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No matching jobs found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try clearing your filters or search keywords to discover more entry-level positions.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(jobs, key = { it.id }) { job ->
                    JobCard(
                        job = job,
                        onJobClick = { viewModel.selectJob(job) },
                        onToggleSave = { viewModel.toggleSaveJob(job) },
                        onStatusChange = { newStatus ->
                            viewModel.updateJobApplicationStatus(
                                jobId = job.id,
                                status = newStatus,
                                jobTitle = job.title,
                                company = job.company
                            )
                        }
                    )
                }
            }
        }
    }

    // Detail dialog
    selectedJob?.let { job ->
        JobDetailDialog(
            job = job,
            onDismiss = { viewModel.selectJob(null) },
            onToggleSave = { viewModel.toggleSaveJob(job) },
            onStatusChange = { newStatus ->
                viewModel.updateJobApplicationStatus(
                    jobId = job.id,
                    status = newStatus,
                    jobTitle = job.title,
                    company = job.company
                )
            }
        )
    }
}
