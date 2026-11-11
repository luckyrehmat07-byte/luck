package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.IshpataViewModel
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun MoreScreen(
    viewModel: IshpataViewModel,
    modifier: Modifier = Modifier
) {
    val userSession by viewModel.userSession.collectAsState()
    val ceoSetupState by viewModel.ceoSetupState.collectAsState()
    var showCeoSetupDialog by remember { mutableStateOf(false) }
    var showCeoLoginDialog by remember { mutableStateOf(false) }
    var showCeoRecoveryDialog by remember { mutableStateOf(false) }
    var newlyCreatedRecoveryKey by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("more_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "MORE EXPERIENCES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = RubyRed,
                    letterSpacing = 1.5.sp
                )
            )
            Text(
                text = "Explore Ishpata Films",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            )
        }

        item {
            MoreMenuItem(
                title = "Music & Artists",
                subtitle = "Traditional Kalash flute (kalu), dhol, and folk musicians",
                icon = Icons.Default.MusicNote,
                onClick = { viewModel.navigateTo(ScreenDestination.MusicArtists) }
            )
        }

        item {
            MoreMenuItem(
                title = "News & Updates",
                subtitle = "Studio production announcements, press, and valley news",
                icon = Icons.Default.Newspaper,
                onClick = { viewModel.navigateTo(ScreenDestination.NewsUpdates) }
            )
        }

        item {
            MoreMenuItem(
                title = "Cultural Photo Gallery",
                subtitle = "Archived visual documentation of Bumburet, Rumbur, and Birir",
                icon = Icons.Default.PhotoLibrary,
                onClick = { viewModel.navigateTo(ScreenDestination.CulturalGallery) }
            )
        }

        item {
            MoreMenuItem(
                title = "Support Local Talent",
                subtitle = "Support the next generation • Donations opening soon",
                icon = Icons.Default.VolunteerActivism,
                onClick = { viewModel.navigateTo(ScreenDestination.SupportTalent) }
            )
        }

        item {
            MoreMenuItem(
                title = "About Ishpata Films",
                subtitle = "Mission, ethical cultural documentation & Founder Lucky Rehmat Aspayi",
                icon = Icons.Default.Info,
                onClick = { viewModel.navigateTo(ScreenDestination.AboutIshpata) }
            )
        }

        item {
            MoreMenuItem(
                title = "Contact Us",
                subtitle = "Official verified contact channels and studio inquiries",
                icon = Icons.Default.Mail,
                onClick = { viewModel.navigateTo(ScreenDestination.ContactUs) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BorderSubtle)
            Spacer(modifier = Modifier.height(10.dp))
        }

        // CEO / Creator Authentication Section
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (userSession?.role == "ceo") CardElevated else CardNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (userSession?.role == "ceo") RubyRed else BorderSubtle, RoundedCornerShape(12.dp))
                    .clickable {
                        if (userSession?.role == "ceo") {
                            viewModel.navigateTo(ScreenDestination.CeoDashboard)
                        } else if (!ceoSetupState.isInitialized) {
                            showCeoSetupDialog = true
                        } else {
                            showCeoLoginDialog = true
                        }
                    }
                    .testTag("ceo_portal_menu_item")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .border(1.dp, RubyRed, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = RubyRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (userSession?.role == "ceo") "CEO Dashboard (Active)"
                                       else if (!ceoSetupState.isInitialized) "First-Time CEO Setup Required"
                                       else "CEO / Super Admin Portal",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                        Text(
                            text = if (userSession?.role == "ceo") "Logged in as Lucky Rehmat Aspayi"
                                   else if (!ceoSetupState.isInitialized) "Tap to verify luckyrehmat07@gmail.com and create private password"
                                   else "Authorized access for luckyrehmat07@gmail.com",
                            style = MaterialTheme.typography.bodySmall.copy(color = if (!ceoSetupState.isInitialized) GoldAccent else TextSecondary)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = RubyRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (userSession != null) {
            item {
                OutlinedButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    Text("Log Out (${userSession?.fullName})")
                }
            }
        }
    }

    // 1. FIRST-TIME CEO SETUP DIALOG (Part A)
    if (showCeoSetupDialog) {
        var verifyEmail by remember { mutableStateOf("luckyrehmat07@gmail.com") }
        var setupToken by remember { mutableStateOf("KALASH-ISHPATA-SETUP-TOKEN") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showCeoSetupDialog = false },
            containerColor = CardNavy,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(RubyRed))
                    Text(
                        text = "First-Time CEO Admin Setup",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Official CEO Email: luckyrehmat07@gmail.com\nVerify the deployment token and create your private password. No default password exists.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        lineHeight = 18.sp
                    )

                    OutlinedTextField(
                        value = verifyEmail,
                        onValueChange = { verifyEmail = it },
                        label = { Text("Designated CEO Email") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("ceo_setup_email_input")
                    )

                    OutlinedTextField(
                        value = setupToken,
                        onValueChange = { setupToken = it },
                        label = { Text("Deployment Setup Token") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("ceo_setup_token_input")
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = { Text("Create Admin Password (min 6 chars)") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("ceo_setup_password_input")
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; errorMessage = null },
                        label = { Text("Confirm Admin Password") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("ceo_setup_confirm_password_input")
                    )

                    if (errorMessage != null) {
                        Text(text = errorMessage ?: "", color = StatusRejected, style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (password.length < 6) {
                            errorMessage = "Password must be at least 6 characters."
                        } else if (password != confirmPassword) {
                            errorMessage = "Passwords do not match."
                        } else {
                            viewModel.setupCeoFirstTime(
                                email = verifyEmail,
                                token = setupToken,
                                password = password,
                                confirm = confirmPassword,
                                onSuccess = { key ->
                                    newlyCreatedRecoveryKey = key
                                    showCeoSetupDialog = false
                                    viewModel.navigateTo(ScreenDestination.CeoDashboard)
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed),
                    modifier = Modifier.testTag("ceo_setup_submit_button")
                ) {
                    Text("Initialize Super Admin")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCeoSetupDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // 2. REGULAR CEO LOGIN DIALOG (after first-time setup is complete)
    if (showCeoLoginDialog) {
        var email by remember { mutableStateOf("luckyrehmat07@gmail.com") }
        var password by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showCeoLoginDialog = false },
            containerColor = CardNavy,
            title = {
                Text(
                    text = "CEO Admin Login",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter the private password you created during First-Time Setup.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("CEO Email") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = { Text("Private Password") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("ceo_password_input")
                    )

                    if (errorMessage != null) {
                        Text(text = errorMessage ?: "", color = StatusRejected, style = MaterialTheme.typography.labelSmall)
                    }

                    TextButton(
                        onClick = {
                            showCeoLoginDialog = false
                            showCeoRecoveryDialog = true
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Forgot password? Use Recovery Key", style = MaterialTheme.typography.labelSmall.copy(color = KalashBlue))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (password.isNotBlank()) {
                            viewModel.loginCeo(email, password) {
                                showCeoLoginDialog = false
                                viewModel.navigateTo(ScreenDestination.CeoDashboard)
                            }
                        } else {
                            errorMessage = "Please enter your password."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed),
                    modifier = Modifier.testTag("ceo_login_confirm_button")
                ) {
                    Text("Unlock Dashboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCeoLoginDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // 3. EMERGENCY RECOVERY / RESET DIALOG
    if (showCeoRecoveryDialog) {
        var email by remember { mutableStateOf("luckyrehmat07@gmail.com") }
        var recoveryKey by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmNewPassword by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showCeoRecoveryDialog = false },
            containerColor = CardNavy,
            title = { Text("Reset CEO Password", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter your one-time Account Recovery Key generated during first-time setup to reset your private password.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    OutlinedTextField(
                        value = recoveryKey,
                        onValueChange = { recoveryKey = it },
                        label = { Text("Recovery Key (e.g. ISHPATA-RECOVER-xxxx)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password (min 6 chars)") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorMessage != null) {
                        Text(text = errorMessage ?: "", color = StatusRejected, style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (recoveryKey.isBlank()) {
                            errorMessage = "Recovery key required."
                        } else if (newPassword.length < 6) {
                            errorMessage = "Password must be at least 6 characters."
                        } else if (newPassword != confirmNewPassword) {
                            errorMessage = "Passwords do not match."
                        } else {
                            viewModel.resetCeoPassword(email, recoveryKey, newPassword, confirmNewPassword) {
                                showCeoRecoveryDialog = false
                                viewModel.navigateTo(ScreenDestination.CeoDashboard)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed)
                ) {
                    Text("Reset & Log In")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCeoRecoveryDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // 4. DISPLAY RECOVERY KEY NOTICE (When newly created)
    newlyCreatedRecoveryKey?.let { key ->
        AlertDialog(
            onDismissRequest = { newlyCreatedRecoveryKey = null },
            containerColor = CardNavy,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = GoldAccent)
                    Text("Save Your Emergency Recovery Key", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Your CEO Admin password has been securely configured. If you ever forget your password, this recovery key is the ONLY way to regain access:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DeepNavy,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = key,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldAccent,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                    Text(
                        text = "Store this key in a secure location outside the application.",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { newlyCreatedRecoveryKey = null },
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed)
                ) {
                    Text("I Have Saved My Key")
                }
            }
        )
    }
}

@Composable
fun MoreMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DeepNavy),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = KalashBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
