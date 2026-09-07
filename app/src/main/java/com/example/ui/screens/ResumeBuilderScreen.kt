package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.EducationItem
import com.example.data.local.ExperienceItem
import com.example.data.local.InitialData
import com.example.data.local.ProjectItem
import com.example.data.local.ResumeConverters
import com.example.ui.GradLaunchViewModel

@Composable
fun ResumeBuilderScreen(
    viewModel: GradLaunchViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.resumeProfile.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedViewMode by remember { mutableIntStateOf(0) } // 0: Edit Sections, 1: Live ATS Preview

    val strengthScore = viewModel.calculateResumeStrength(profile)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Tab Switcher: Builder vs Preview
        TabRow(
            selectedTabIndex = selectedViewMode,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedViewMode == 0,
                onClick = { selectedViewMode = 0 },
                text = { Text("Resume Editor", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_resume_editor")
            )
            Tab(
                selected = selectedViewMode == 1,
                onClick = { selectedViewMode = 1 },
                text = { Text("Live ATS Preview", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_resume_preview")
            )
        }

        if (selectedViewMode == 0) {
            // Builder Mode
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Strength Score Meter Card
                item {
                    ResumeStrengthCard(
                        score = strengthScore,
                        onResetSample = {
                            viewModel.resetToSampleProfile()
                            Toast.makeText(context, "Loaded New Grad Sample Profile!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Action Verbs Helper Chips
                item {
                    ActionVerbsHelperSection(
                        onVerbClick = { verb ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Action Verb", verb))
                            Toast.makeText(context, "Copied '$verb' to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Section 1: Contact & Summary
                item {
                    ContactAndSummarySection(
                        profile = profile,
                        onSaveBasics = { name, role, email, phone, loc, link, git, port, summ ->
                            viewModel.saveResumeBasics(name, role, email, phone, loc, link, git, port, summ)
                            Toast.makeText(context, "Basics updated!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Section 2: Education
                item {
                    EducationSection(
                        profile = profile,
                        onSaveEducationList = { viewModel.saveEducationList(it) }
                    )
                }

                // Section 3: Experience & Internships
                item {
                    ExperienceSection(
                        profile = profile,
                        onSaveExperienceList = { viewModel.saveExperienceList(it) }
                    )
                }

                // Section 4: Projects & Portfolio
                item {
                    ProjectsSection(
                        profile = profile,
                        onSaveProjectsList = { viewModel.saveProjectsList(it) }
                    )
                }

                // Section 5: Skills & Certifications
                item {
                    SkillsSection(
                        profile = profile,
                        onSaveSkills = { tech, soft, certs ->
                            viewModel.saveSkills(tech, soft, certs)
                            Toast.makeText(context, "Skills updated!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            // Live ATS Preview Mode
            LiveAtsPreviewView(
                profile = profile,
                onCopy = {
                    val fullText = viewModel.generatePlainTextResume(profile)
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("GradLaunch Resume", fullText))
                    Toast.makeText(context, "Formatted resume copied to clipboard!", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}

@Composable
fun ResumeStrengthCard(
    score: Int,
    onResetSample: () -> Unit
) {
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
                            .background(com.example.ui.theme.AccentGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = com.example.ui.theme.AccentGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Resume Strength Meter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (score >= 90) "ATS-Ready & Highly Competitive" else "Add details to boost interview callbacks",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "$score%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (score >= 80) com.example.ui.theme.AccentGreen else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (score >= 80) com.example.ui.theme.AccentGreen else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💡 Tip: Include quantified metrics and technical keywords.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f)
                )

                OutlinedButton(
                    onClick = onResetSample,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("reset_sample_resume_button")
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Load Sample", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun ActionVerbsHelperSection(onVerbClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Action Verb Suggester (Tap to Copy)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                InitialData.actionVerbs.forEach { verb ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { onVerbClick(verb) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = verb,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactAndSummarySection(
    profile: com.example.data.local.ResumeProfileEntity?,
    onSaveBasics: (String, String, String, String, String, String, String, String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    var fullName by remember(profile) { mutableStateOf(profile?.fullName ?: "") }
    var targetRole by remember(profile) { mutableStateOf(profile?.targetRole ?: "") }
    var email by remember(profile) { mutableStateOf(profile?.email ?: "") }
    var phone by remember(profile) { mutableStateOf(profile?.phone ?: "") }
    var location by remember(profile) { mutableStateOf(profile?.location ?: "") }
    var linkedin by remember(profile) { mutableStateOf(profile?.linkedin ?: "") }
    var github by remember(profile) { mutableStateOf(profile?.github ?: "") }
    var portfolio by remember(profile) { mutableStateOf(profile?.portfolio ?: "") }
    var summary by remember(profile) { mutableStateOf(profile?.summary ?: "") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1. Personal Contact & Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("input_full_name"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = targetRole,
                        onValueChange = { targetRole = it },
                        label = { Text("Target Role / Headline") },
                        modifier = Modifier.fillMaxWidth().testTag("input_target_role"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.weight(1f).testTag("input_email"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.weight(1f).testTag("input_phone"),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location (City, State / Relocation)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = linkedin,
                            onValueChange = { linkedin = it },
                            label = { Text("LinkedIn") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = github,
                            onValueChange = { github = it },
                            label = { Text("GitHub / Portfolio") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Recent Grad Starter Templates:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StarterSummaryChip("Software Engineer") {
                            summary = "Motivated Computer Science graduate with hands-on internship experience designing scalable applications, modern APIs, and clean mobile interfaces. Quick learner dedicated to writing robust, maintainable code in an agile team."
                        }
                        StarterSummaryChip("Product Designer") {
                            summary = "User-obsessed Interaction Design graduate skilled in Figma prototyping, design systems, and user research. Passionate about crafting accessible digital products that solve real human challenges."
                        }
                        StarterSummaryChip("Financial Analyst") {
                            summary = "Analytical Finance & Economics graduate with strong background in financial modeling, variance reporting, and corporate valuation. Eager to drive strategic growth and capital forecasting."
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("Professional Summary") },
                        modifier = Modifier.fillMaxWidth().testTag("input_summary"),
                        minLines = 3,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            onSaveBasics(fullName, targetRole, email, phone, location, linkedin, github, portfolio, summary)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_basics_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Personal Info & Summary")
                    }
                }
            }
        }
    }
}

@Composable
fun StarterSummaryChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun EducationSection(
    profile: com.example.data.local.ResumeProfileEntity?,
    onSaveEducationList: (List<EducationItem>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val educationList = remember(profile) {
        profile?.let { ResumeConverters.parseEducation(it.educationJson) } ?: emptyList()
    }
    var showAddDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2. Education & Honors (${educationList.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    educationList.forEachIndexed { index, item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.degree} in ${item.major}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${item.institution} • ${item.gradYear}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (item.gpa.isNotBlank()) {
                                        Text(text = "GPA: ${item.gpa}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        val updated = educationList.toMutableList()
                                        updated.removeAt(index)
                                        onSaveEducationList(updated)
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth().testTag("add_education_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Degree / Education")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEducationDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newEdu ->
                val updated = educationList.toMutableList()
                updated.add(newEdu)
                onSaveEducationList(updated)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddEducationDialog(
    onDismiss: () -> Unit,
    onAdd: (EducationItem) -> Unit
) {
    var degree by remember { mutableStateOf("") }
    var major by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var gradYear by remember { mutableStateOf("") }
    var gpa by remember { mutableStateOf("") }
    var honors by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Add Education", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = degree, onValueChange = { degree = it }, label = { Text("Degree (e.g. BS, BA, MS)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = major, onValueChange = { major = it }, label = { Text("Major / Focus") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = school, onValueChange = { school = it }, label = { Text("University / Institution") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = gradYear, onValueChange = { gradYear = it }, label = { Text("Graduation Year") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = gpa, onValueChange = { gpa = it }, label = { Text("GPA (optional)") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = honors, onValueChange = { honors = it }, label = { Text("Honors / Coursework") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (school.isNotBlank() && major.isNotBlank()) {
                                onAdd(EducationItem(degree, major, school, gradYear, gpa, honors))
                            }
                        }
                    ) { Text("Add") }
                }
            }
        }
    }
}

@Composable
fun ExperienceSection(
    profile: com.example.data.local.ResumeProfileEntity?,
    onSaveExperienceList: (List<ExperienceItem>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val expList = remember(profile) {
        profile?.let { ResumeConverters.parseExperience(it.experienceJson) } ?: emptyList()
    }
    var showAddDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "3. Work & Internship Experience (${expList.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    expList.forEachIndexed { index, item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.role,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${item.company} • ${item.dates}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            val updated = expList.toMutableList()
                                            updated.removeAt(index)
                                            onSaveExperienceList(updated)
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                item.highlights.split("\n").filter { it.isNotBlank() }.forEach { b ->
                                    Text(text = "• ${b.trim()}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth().testTag("add_experience_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Internship / Work Experience")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddExperienceDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newExp ->
                val updated = expList.toMutableList()
                updated.add(newExp)
                onSaveExperienceList(updated)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddExperienceDialog(
    onDismiss: () -> Unit,
    onAdd: (ExperienceItem) -> Unit
) {
    var role by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var dates by remember { mutableStateOf("") }
    var highlights by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                Text("Add Experience / Internship", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Job / Internship Title") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("Company / Organization") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = dates, onValueChange = { dates = it }, label = { Text("Dates (e.g. Summer 2025)") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = highlights,
                    onValueChange = { highlights = it },
                    label = { Text("Accomplishments (One per line)") },
                    placeholder = { Text("Engineered...\nOptimized payload by 30%...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (role.isNotBlank() && company.isNotBlank()) {
                                onAdd(ExperienceItem(role, company, location, dates, highlights))
                            }
                        }
                    ) { Text("Add") }
                }
            }
        }
    }
}

@Composable
fun ProjectsSection(
    profile: com.example.data.local.ResumeProfileEntity?,
    onSaveProjectsList: (List<ProjectItem>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val projList = remember(profile) {
        profile?.let { ResumeConverters.parseProjects(it.projectsJson) } ?: emptyList()
    }
    var showAddDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "4. Academic & Personal Projects (${projList.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    projList.forEachIndexed { index, item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text(text = "Stack: ${item.techStack}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    Text(text = item.description, style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(
                                    onClick = {
                                        val updated = projList.toMutableList()
                                        updated.removeAt(index)
                                        onSaveProjectsList(updated)
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth().testTag("add_project_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Project")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProjectDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newProj ->
                val updated = projList.toMutableList()
                updated.add(newProj)
                onSaveProjectsList(updated)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddProjectDialog(
    onDismiss: () -> Unit,
    onAdd: (ProjectItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var techStack by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Add Project", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Project Title") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = techStack, onValueChange = { techStack = it }, label = { Text("Tools / Tech Stack") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description & Impact") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = link, onValueChange = { link = it }, label = { Text("GitHub or Demo URL") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onAdd(ProjectItem(title, techStack, description, link))
                            }
                        }
                    ) { Text("Add") }
                }
            }
        }
    }
}

@Composable
fun SkillsSection(
    profile: com.example.data.local.ResumeProfileEntity?,
    onSaveSkills: (String, String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var technical by remember(profile) { mutableStateOf(profile?.skillsTechnical ?: "") }
    var soft by remember(profile) { mutableStateOf(profile?.skillsSoft ?: "") }
    var certs by remember(profile) { mutableStateOf(profile?.certifications ?: "") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "5. Skills & Certifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    OutlinedTextField(
                        value = technical,
                        onValueChange = { technical = it },
                        label = { Text("Technical Skills (Comma-separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = soft,
                        onValueChange = { soft = it },
                        label = { Text("Professional & Soft Skills") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = certs,
                        onValueChange = { certs = it },
                        label = { Text("Certifications / Courses") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onSaveSkills(technical, soft, certs) },
                        modifier = Modifier.fillMaxWidth().testTag("save_skills_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Skills")
                    }
                }
            }
        }
    }
}

@Composable
fun LiveAtsPreviewView(
    profile: com.example.data.local.ResumeProfileEntity?,
    onCopy: () -> Unit
) {
    val eduList = remember(profile) {
        profile?.let { ResumeConverters.parseEducation(it.educationJson) } ?: emptyList()
    }
    val expList = remember(profile) {
        profile?.let { ResumeConverters.parseExperience(it.experienceJson) } ?: emptyList()
    }
    val projList = remember(profile) {
        profile?.let { ResumeConverters.parseProjects(it.projectsJson) } ?: emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ATS Resume Formatted View",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Standard clean format compliant with screening systems",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onCopy,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("copy_resume_button")
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Resume", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Formatted Document Canvas
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Name & Headline
                Text(
                    text = profile?.fullName?.uppercase() ?: "YOUR NAME",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    letterSpacing = 1.sp
                )
                Text(
                    text = profile?.targetRole ?: "Target Role",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2563EB)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${profile?.email} • ${profile?.phone} • ${profile?.location}",
                    fontSize = 11.sp,
                    color = Color(0xFF475569)
                )
                if (!profile?.linkedin.isNullOrBlank() || !profile?.github.isNullOrBlank()) {
                    Text(
                        text = "LinkedIn: ${profile?.linkedin} | Portfolio: ${profile?.github}",
                        fontSize = 11.sp,
                        color = Color(0xFF475569)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                // Summary
                Text(text = "PROFESSIONAL SUMMARY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = profile?.summary ?: "", fontSize = 11.sp, color = Color(0xFF334155), lineHeight = 16.sp)

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                // Education
                Text(text = "EDUCATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(4.dp))
                eduList.forEach { edu ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "${edu.institution}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                        Text(text = edu.gradYear, fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    Text(text = "${edu.degree} in ${edu.major}", fontSize = 11.sp, color = Color(0xFF334155))
                    if (edu.gpa.isNotBlank()) Text(text = "GPA: ${edu.gpa} | Honors: ${edu.honors}", fontSize = 10.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                // Experience
                Text(text = "EXPERIENCE & INTERNSHIPS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(4.dp))
                expList.forEach { exp ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "${exp.role} — ${exp.company}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                        Text(text = exp.dates, fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    exp.highlights.split("\n").filter { it.isNotBlank() }.forEach { b ->
                        Text(text = "• ${b.trim()}", fontSize = 11.sp, color = Color(0xFF334155), lineHeight = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                // Projects
                Text(text = "PROJECTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(4.dp))
                projList.forEach { proj ->
                    Text(text = "${proj.title} [${proj.techStack}]", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    Text(text = proj.description, fontSize = 11.sp, color = Color(0xFF334155))
                    if (proj.link.isNotBlank()) Text(text = "Link: ${proj.link}", fontSize = 10.sp, color = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                // Skills
                Text(text = "TECHNICAL & PROFESSIONAL SKILLS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Technical: ${profile?.skillsTechnical}", fontSize = 11.sp, color = Color(0xFF334155))
                Text(text = "Professional: ${profile?.skillsSoft}", fontSize = 11.sp, color = Color(0xFF334155))
                if (!profile?.certifications.isNullOrBlank()) {
                    Text(text = "Certifications: ${profile?.certifications}", fontSize = 11.sp, color = Color(0xFF334155))
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}
