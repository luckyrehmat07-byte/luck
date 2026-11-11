package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CreatorProfile
import com.example.data.model.CreatorSubmission
import com.example.ui.components.EmptyStateNotice
import com.example.ui.theme.*
import com.example.ui.viewmodel.IshpataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorsScreen(
    viewModel: IshpataViewModel,
    modifier: Modifier = Modifier
) {
    val userSession by viewModel.userSession.collectAsState()
    val creators by viewModel.creators.collectAsState()
    val submissions by viewModel.submissions.collectAsState()

    var showRegisterDialog by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var showSubmitDialog by remember { mutableStateOf(false) }

    val approvedCreators = remember(creators) { creators.filter { it.status == "approved" } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("creators_screen")
    ) {
        // Top Banner / Status
        Card(
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CREATOR COMMUNITY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = RubyRed,
                                letterSpacing = 1.5.sp
                            )
                        )
                        Text(
                            text = if (userSession != null) "Creator Studio" else "Meet Our Creators",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        )
                    }

                    if (userSession == null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showLoginDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Login", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                            Button(
                                onClick = { showRegisterDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RubyRed),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Join", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    } else {
                        Button(
                            onClick = { showSubmitDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RubyRed),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Submit Content", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        // Body Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // If logged in: Show Creator Studio Submissions & Dashboard
            if (userSession != null) {
                item {
                    Text(
                        text = "My Studio Submissions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                if (submissions.isEmpty()) {
                    item {
                        EmptyStateNotice("No submissions yet. Tap 'Submit Content' above to submit your creative work.")
                    }
                } else {
                    items(submissions) { sub ->
                        SubmissionStatusCard(sub = sub)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Public Creator Directory",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
            }

            // Public Creator Directory
            if (approvedCreators.isEmpty()) {
                item {
                    EmptyStateNotice("Creator directory is being updated.")
                }
            } else {
                items(approvedCreators) { creator ->
                    CreatorProfileCard(creator = creator)
                }
            }
        }
    }

    // Register Dialog
    if (showRegisterDialog) {
        var fullName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var bio by remember { mutableStateOf("") }
        var village by remember { mutableStateOf("Bumburet Valley") }
        var portfolio by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            containerColor = CardNavy,
            title = {
                Text(
                    text = "Request Creator Account",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Join the Ishpata Films creative network. New creator accounts require CEO approval before publishing permissions are granted.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = village,
                        onValueChange = { village = it },
                        label = { Text("Valley / Village") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Artistic Discipline & Bio") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fullName.isNotBlank() && email.isNotBlank()) {
                            viewModel.registerCreator(fullName, email, bio, village, portfolio)
                            showRegisterDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed)
                ) {
                    Text("Submit Application")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Login Dialog
    if (showLoginDialog) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            containerColor = CardNavy,
            title = {
                Text(
                    text = "Creator Login",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter your approved creator credentials to access Creator Studio.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Creator Email") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (email.isNotBlank()) {
                            viewModel.loginCreator(email) {
                                showLoginDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed)
                ) {
                    Text("Log In")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Submit Content Dialog
    if (showSubmitDialog) {
        var subType by remember { mutableStateOf("youtube_link") }
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var externalId by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            containerColor = CardNavy,
            title = {
                Text(
                    text = "Submit Content to Studio",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Select content type and describe your work. It will enter the CEO review queue.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = subType == "youtube_link",
                            onClick = { subType = "youtube_link" },
                            label = { Text("YouTube Link") }
                        )
                        FilterChip(
                            selected = subType == "photo",
                            onClick = { subType = "photo" },
                            label = { Text("Photo") }
                        )
                        FilterChip(
                            selected = subType == "article",
                            onClick = { subType = "article" },
                            label = { Text("Article") }
                        )
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    if (subType == "youtube_link") {
                        OutlinedTextField(
                            value = externalId,
                            onValueChange = { externalId = it },
                            label = { Text("YouTube Video ID (e.g. dQw4w9WgXcQ)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Cultural Context") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.submitContent(subType, title, description, externalId, "")
                            showSubmitDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed)
                ) {
                    Text("Submit for Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun CreatorProfileCard(
    creator: CreatorProfile,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(DeepNavy)
                    .border(1.dp, KalashBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = KalashBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = creator.fullName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = KalashBlue.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = creator.villageOrigin,
                            style = MaterialTheme.typography.labelSmall.copy(color = CyanAccent),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = creator.bio,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun SubmissionStatusCard(
    sub: CreatorSubmission,
    modifier: Modifier = Modifier
) {
    val statusColor = when (sub.status.lowercase()) {
        "approved" -> StatusApproved
        "published" -> StatusPublished
        "changes_requested" -> StatusPending
        "rejected" -> StatusRejected
        else -> StatusPending
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sub.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor)
                ) {
                    Text(
                        text = sub.status.uppercase().replace('_', ' '),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = sub.description,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (sub.ceoFeedback.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DeepNavy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "CEO Feedback: ${sub.ceoFeedback}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldAccent,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Text(
                text = "Submitted: ${sub.submittedAt} • Type: ${sub.submissionType}",
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
            )
        }
    }
}
