package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.launchExternalYouTube
import com.example.ui.theme.*
import com.example.ui.viewmodel.IshpataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CeoDashboardScreen(
    viewModel: IshpataViewModel,
    modifier: Modifier = Modifier
) {
    val creators by viewModel.creators.collectAsState()
    val submissions by viewModel.submissions.collectAsState()
    val youtubeAccounts by viewModel.youtubeAccounts.collectAsState()
    val youtubeVideos by viewModel.youtubeVideos.collectAsState()
    val films by viewModel.films.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val activeAccountId by viewModel.activeYouTubeAccountId.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Overview", "YouTube Manager", "Submissions", "Creators", "Films", "Audit Logs")

    var showConnectChannelDialog by remember { mutableStateOf(false) }
    var showAddFilmDialog by remember { mutableStateOf(false) }
    var selectedSubmissionForReview by remember { mutableStateOf<CreatorSubmission?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("ceo_dashboard_screen")
    ) {
        // CEO Header
        Card(
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ADMINISTRATIVE CONTROL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = RubyRed,
                                letterSpacing = 1.5.sp
                            )
                        )
                        Text(
                            text = "CEO Dashboard",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = RubyRed.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RubyRed)
                    ) {
                        Text(
                            text = "luckyrehmat07@gmail.com",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Scrollable Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SurfaceNavy,
                    contentColor = RubyRed,
                    edgePadding = 0.dp
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTab == index) RubyRedLight else TextSecondary
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> CeoOverviewTab(creators, submissions, youtubeAccounts, films)
            1 -> CeoYouTubeManagerTab(
                accounts = youtubeAccounts,
                videos = youtubeVideos,
                activeAccountId = activeAccountId,
                onSwitchAccount = { viewModel.selectYouTubeAccount(it) },
                onToggleVisibility = { id, pub -> viewModel.toggleChannelVisibility(id, pub) },
                onSetHomepageDefault = { viewModel.setHomepageDefaultChannel(it) },
                onToggleVideoHidden = { vid, hid -> viewModel.toggleVideoVisibility(vid, hid) },
                onConnectNewChannel = { showConnectChannelDialog = true },
                onSyncChannelVideos = { viewModel.syncChannelVideos(it) },
                onDisconnectAccount = { viewModel.disconnectYouTubeAccount(it) }
            )
            2 -> CeoSubmissionsTab(
                submissions = submissions,
                onReviewClick = { selectedSubmissionForReview = it }
            )
            3 -> CeoCreatorsTab(
                creators = creators,
                onUpdateStatus = { id, st -> viewModel.updateCreatorStatus(id, st) }
            )
            4 -> CeoFilmsTab(
                films = films,
                onAddFilmClick = { showAddFilmDialog = true }
            )
            5 -> CeoAuditLogsTab(auditLogs = auditLogs)
        }
    }

    // Connect YouTube Channel Dialog
    if (showConnectChannelDialog) {
        var label by remember { mutableStateOf("") }
        var channelId by remember { mutableStateOf("") }
        var channelTitle by remember { mutableStateOf("") }
        var isPublic by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showConnectChannelDialog = false },
            containerColor = CardNavy,
            title = { Text("Connect YouTube Channel / Google Account", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Assign a friendly display label (e.g. 'Ishpata Films — Account 2') and channel ID. Videos will be retrieved via YouTube Data API.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        label = { Text("Account Label") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = channelTitle,
                        onValueChange = { channelTitle = it },
                        label = { Text("Channel Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = channelId,
                        onValueChange = { channelId = it },
                        label = { Text("Channel ID (e.g. UC_xxxx)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isPublic,
                            onCheckedChange = { isPublic = it }
                        )
                        Text("Visible to public app visitors", style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (label.isNotBlank() && channelId.isNotBlank()) {
                            viewModel.connectYouTubeAccount(label, channelId, channelTitle.ifBlank { label }, isPublic)
                            showConnectChannelDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed)
                ) {
                    Text("Connect Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConnectChannelDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Submission Review Dialog
    selectedSubmissionForReview?.let { sub ->
        var feedback by remember { mutableStateOf(sub.ceoFeedback) }

        AlertDialog(
            onDismissRequest = { selectedSubmissionForReview = null },
            containerColor = CardNavy,
            title = { Text("Review: ${sub.title}", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Author: ${sub.authorName} (${sub.authorEmail})",
                        style = MaterialTheme.typography.bodySmall.copy(color = KalashBlue)
                    )
                    Text(
                        text = "Type: ${sub.submissionType} • Status: ${sub.status}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                    Text(
                        text = sub.description,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                    if (sub.externalYouTubeId.isNotBlank()) {
                        Text(
                            text = "External YT ID: ${sub.externalYouTubeId}",
                            style = MaterialTheme.typography.labelSmall.copy(color = GoldAccent)
                        )
                    }
                    OutlinedTextField(
                        value = feedback,
                        onValueChange = { feedback = it },
                        label = { Text("CEO Feedback / Notes to Creator") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            viewModel.reviewSubmission(sub.id, "approved", feedback)
                            selectedSubmissionForReview = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusApproved)
                    ) {
                        Text("Approve")
                    }
                    Button(
                        onClick = {
                            viewModel.reviewSubmission(sub.id, "changes_requested", feedback)
                            selectedSubmissionForReview = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusPending)
                    ) {
                        Text("Changes")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.reviewSubmission(sub.id, "rejected", feedback)
                        selectedSubmissionForReview = null
                    }
                ) {
                    Text("Reject", color = StatusRejected)
                }
            }
        )
    }

    // Add Film Dialog
    if (showAddFilmDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Cultural Films") }
        var synopsis by remember { mutableStateOf("") }
        var youtubeId by remember { mutableStateOf("") }
        var director by remember { mutableStateOf("Lucky Rehmat Aspayi") }
        var year by remember { mutableStateOf("2026") }
        var duration by remember { mutableStateOf("20 min") }

        AlertDialog(
            onDismissRequest = { showAddFilmDialog = false },
            containerColor = CardNavy,
            title = { Text("Add Film to Catalog", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Film Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category (e.g. Cultural Films, Documentary)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = youtubeId,
                        onValueChange = { youtubeId = it },
                        label = { Text("YouTube Video ID") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = synopsis,
                        onValueChange = { synopsis = it },
                        label = { Text("Synopsis") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && youtubeId.isNotBlank()) {
                            viewModel.addFilm(
                                Film(
                                    id = films.size + 1,
                                    title = title,
                                    category = category,
                                    synopsis = synopsis,
                                    director = director,
                                    releaseYear = year,
                                    duration = duration,
                                    youtubeVideoId = youtubeId
                                )
                            )
                            showAddFilmDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed)
                ) {
                    Text("Save Film")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFilmDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun CeoOverviewTab(
    creators: List<CreatorProfile>,
    submissions: List<CreatorSubmission>,
    accounts: List<YouTubeAccount>,
    films: List<Film>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Studio Key Metrics",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Pending Reviews",
                    value = submissions.count { it.status == "pending" }.toString(),
                    color = RubyRed,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Connected Channels",
                    value = accounts.size.toString(),
                    color = KalashBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Approved Creators",
                    value = creators.count { it.status == "approved" }.toString(),
                    color = StatusApproved,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Published Films",
                    value = films.count { it.isPublished }.toString(),
                    color = GoldAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Recent Submissions Awaiting Approval",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        }

        val pendingSubs = submissions.filter { it.status == "pending" }
        if (pendingSubs.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "All submissions have been reviewed.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(pendingSubs) { sub ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = sub.title, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "By ${sub.authorName} • Type: ${sub.submissionType}", style = MaterialTheme.typography.bodySmall.copy(color = KalashBlue))
                        Text(text = sub.description, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary), maxLines = 2)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        modifier = modifier.border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
            Text(text = value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = color))
        }
    }
}

@Composable
fun CeoYouTubeManagerTab(
    accounts: List<YouTubeAccount>,
    videos: List<YouTubeVideo>,
    activeAccountId: Int,
    onSwitchAccount: (Int) -> Unit,
    onToggleVisibility: (Int, Boolean) -> Unit,
    onSetHomepageDefault: (Int) -> Unit,
    onToggleVideoHidden: (String, Boolean) -> Unit,
    onConnectNewChannel: () -> Unit,
    onSyncChannelVideos: (Int) -> Unit,
    onDisconnectAccount: (Int) -> Unit
) {
    val context = LocalContext.current
    var syncingAccountId by remember { mutableStateOf<Int?>(null) }

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Connected Google & YouTube Accounts", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Official Google OAuth & YouTube Data API v3 uploads synchronization", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                }
                Button(
                    onClick = onConnectNewChannel,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Account", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        // Account Switching Cards
        items(accounts) { acc ->
            val isActive = acc.id == activeAccountId
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (isActive) CardElevated else CardNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isActive) RubyRed else BorderSubtle, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.clickable { onSwitchAccount(acc.id) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isActive) RubyRed else TextMuted)
                            )
                            Text(
                                text = acc.accountLabel,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                style = MaterialTheme.typography.titleSmall
                            )
                            if (isActive) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = RubyRed.copy(alpha = 0.2f)
                                ) {
                                    Text("ACTIVE", color = RubyRedLight, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                        }

                        if (acc.isHomepageDefault) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text("HOMEPAGE DEFAULT", color = GoldAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }

                    Text(text = "Channel: ${acc.channelTitle} (${acc.channelId})", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = acc.isPublic,
                                onCheckedChange = { onToggleVisibility(acc.id, it) }
                            )
                            Text("Public Visibility", style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary))
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (!isActive) {
                                OutlinedButton(
                                    onClick = { onSwitchAccount(acc.id) },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Switch", fontSize = 11.sp, color = KalashBlue)
                                }
                            }
                            if (!acc.isHomepageDefault) {
                                TextButton(
                                    onClick = { onSetHomepageDefault(acc.id) },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text("Set Default", color = GoldAccent, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Action bar: Sync Videos & Disconnect
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                syncingAccountId = acc.id
                                onSyncChannelVideos(acc.id)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = KalashBlue),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (syncingAccountId == acc.id) "Synchronizing..." else "Sync Channel Videos",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (accounts.size > 1) {
                            TextButton(
                                onClick = { onDisconnectAccount(acc.id) },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text("Disconnect", color = StatusRejected, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Channel Videos
        item {
            Spacer(modifier = Modifier.height(10.dp))
            val currentAcc = accounts.find { it.id == activeAccountId }
            Text(
                text = "Videos for ${currentAcc?.accountLabel ?: "Selected Channel"}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        }

        val channelVideos = videos.filter { it.accountId == activeAccountId }
        if (channelVideos.isEmpty()) {
            item {
                Text("No videos synchronized for this channel yet.", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }
        } else {
            items(channelVideos) { vid ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = vid.title, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                            Text(text = "ID: ${vid.videoId} • ${vid.viewCount} views", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                        }

                        IconButton(onClick = { launchExternalYouTube(context, vid.videoId) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Open YouTube", tint = Color(0xFFCC0000))
                        }

                        IconButton(onClick = { onToggleVideoHidden(vid.videoId, !vid.isHidden) }) {
                            Icon(
                                imageVector = if (vid.isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Visibility",
                                tint = if (vid.isHidden) StatusPending else StatusApproved
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CeoSubmissionsTab(
    submissions: List<CreatorSubmission>,
    onReviewClick: (CreatorSubmission) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(submissions) { sub ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onReviewClick(sub) }
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = sub.title, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (sub.status) {
                                "approved" -> StatusApproved.copy(alpha = 0.2f)
                                "rejected" -> StatusRejected.copy(alpha = 0.2f)
                                else -> StatusPending.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(text = sub.status.uppercase(), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Text(text = "By ${sub.authorName} (${sub.authorEmail}) • Type: ${sub.submissionType}", style = MaterialTheme.typography.bodySmall.copy(color = KalashBlue))
                    Text(text = sub.description, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary), maxLines = 2)
                    Button(
                        onClick = { onReviewClick(sub) },
                        colors = ButtonDefaults.buttonColors(containerColor = KalashBlue),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Review / Action")
                    }
                }
            }
        }
    }
}

@Composable
fun CeoCreatorsTab(
    creators: List<CreatorProfile>,
    onUpdateStatus: (Int, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(creators) { creator ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = creator.fullName, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (creator.status == "approved") StatusApproved.copy(alpha = 0.2f) else StatusPending.copy(alpha = 0.2f)
                        ) {
                            Text(text = creator.status.uppercase(), color = if (creator.status == "approved") StatusApproved else StatusPending, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Text(text = "Village: ${creator.villageOrigin}", style = MaterialTheme.typography.bodySmall.copy(color = KalashBlue))
                    Text(text = creator.bio, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (creator.status != "approved") {
                            Button(
                                onClick = { onUpdateStatus(creator.id, "approved") },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusApproved),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Approve")
                            }
                        }
                        if (creator.status != "suspended") {
                            OutlinedButton(
                                onClick = { onUpdateStatus(creator.id, "suspended") },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Suspend", color = StatusRejected)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CeoFilmsTab(
    films: List<Film>,
    onAddFilmClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Production Catalog", fontWeight = FontWeight.Bold, color = TextPrimary)
                Button(
                    onClick = onAddFilmClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Film")
                }
            }
        }

        items(films) { film ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = film.title, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "${film.category} • Dir: ${film.director} • ${film.releaseYear}", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                    Text(text = "YouTube ID: ${film.youtubeVideoId}", style = MaterialTheme.typography.labelSmall.copy(color = GoldAccent))
                }
            }
        }
    }
}

@Composable
fun CeoAuditLogsTab(
    auditLogs: List<AuditLogEntry>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(auditLogs) { log ->
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = log.action, fontWeight = FontWeight.Bold, color = RubyRed, fontSize = 11.sp)
                        Text(text = log.timestamp, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted), fontSize = 10.sp)
                    }
                    Text(text = log.details, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary))
                    Text(text = "Admin: ${log.adminEmail}", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary), fontSize = 10.sp)
                }
            }
        }
    }
}
