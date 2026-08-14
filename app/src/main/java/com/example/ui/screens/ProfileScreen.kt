package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CorePulseViolet
import com.example.ui.theme.Cyan400
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassWhite10
import com.example.ui.theme.GlassWhite5
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.SpaceDarkBackground
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.TarunViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: TarunViewModel,
    onNavigateToGemini: () -> Unit,
    onNavigateToVoice: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToMemory: () -> Unit,
    onNavigateToExport: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToDeveloper: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val appSettings by viewModel.appSettings.collectAsState()
    val isBgActive by viewModel.isBackgroundServiceActive.collectAsState()
    var showEditBossDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PROFILE & CONTROL CENTER",
                        color = Slate100,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("profile_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate100)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SpaceDarkBackground
                )
            )
        },
        containerColor = SpaceDarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User / Boss Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassSurfaceDark),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(NeonCyan, CorePulseViolet))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = SpaceDarkBackground,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = appSettings.bossTitle,
                                color = Slate100,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Authorized Commander",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Button(
                            onClick = { showEditBossDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GlassWhite10)
                        ) {
                            Text("Edit", color = NeonCyan, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Background Service Quick Toggle
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GlassSurfaceDark),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isBgActive) NeonEmerald.copy(alpha = 0.2f) else GlassWhite5),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = if (isBgActive) NeonEmerald else Slate400,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Background Voice Assistant", color = Slate100, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Persistent notification & wake word", color = Slate400, fontSize = 12.sp)
                            }
                        }
                        Switch(
                            checked = isBgActive,
                            onCheckedChange = { active ->
                                if (active) {
                                    viewModel.startBackgroundAssistant()
                                } else {
                                    viewModel.stopBackgroundAssistant()
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonEmerald,
                                checkedTrackColor = NeonEmerald.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            // Core Navigation Hubs
            item {
                Text(
                    text = "ASSISTANT ARCHITECTURE & MODULES",
                    color = Slate400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Psychology,
                    iconTint = CorePulseViolet,
                    title = "Google Gemini AI Settings",
                    subtitle = if (appSettings.geminiApiKey.isNotBlank()) "Online: ${appSettings.geminiModel}" else "Configure API Key & Models",
                    badge = if (appSettings.geminiApiKey.isNotBlank()) "ACTIVE" else "SETUP",
                    badgeColor = if (appSettings.geminiApiKey.isNotBlank()) NeonEmerald else Color(0xFFEAB308),
                    onClick = onNavigateToGemini
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.GraphicEq,
                    iconTint = NeonCyan,
                    title = "Voice & Speech Engine",
                    subtitle = "Multilingual TTS, Hindi/Gujarati pitches & rates",
                    onClick = onNavigateToVoice
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.VerifiedUser,
                    iconTint = HologramTeal,
                    title = "System Permission Center",
                    subtitle = "Real Android permission statuses & deep links",
                    onClick = onNavigateToPermissions
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.AutoAwesome,
                    iconTint = CorePulseViolet,
                    title = "Memory & Fact Vault",
                    subtitle = "Manage stored user facts and contextual knowledge",
                    onClick = onNavigateToMemory
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    iconTint = NeonCyan,
                    title = "APK Build & GitHub Actions Center",
                    subtitle = "CI/CD pipeline, Gradle wrapper & ZIP export",
                    onClick = onNavigateToExport
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Security,
                    iconTint = HologramTeal,
                    title = "Privacy & Local Data Protection",
                    subtitle = "Zero-telemetry policy, local encryption, data clear",
                    onClick = onNavigateToPrivacy
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.BugReport,
                    iconTint = Color(0xFFEAB308),
                    title = "Developer Mode & Diagnostics",
                    subtitle = "Live structured JSON intent inspection & logs",
                    onClick = onNavigateToDeveloper
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Info,
                    iconTint = Slate400,
                    title = "About TARUN AI",
                    subtitle = "Version 2.0 • Architecture & Credits",
                    onClick = onNavigateToAbout
                )
            }
        }
    }

    if (showEditBossDialog) {
        var tempTitle by remember { mutableStateOf(appSettings.bossTitle) }
        AlertDialog(
            onDismissRequest = { showEditBossDialog = false },
            title = { Text("Set Boss / Commander Title", color = Slate100, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = tempTitle,
                    onValueChange = { tempTitle = it },
                    label = { Text("Title (e.g. Boss, Sir, Tarun)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempTitle.isNotBlank()) {
                            viewModel.updateAppSettings(appSettings.copy(bossTitle = tempTitle.trim()))
                        }
                        showEditBossDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = SpaceDarkBackground)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditBossDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = SpaceDarkBackground
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    badge: String? = null,
    badgeColor: Color = NeonCyan,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassSurfaceDark),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, color = Slate100, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    if (badge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeColor.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(badge, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
                Text(subtitle, color = Slate400, fontSize = 12.sp)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate500)
        }
    }
}
