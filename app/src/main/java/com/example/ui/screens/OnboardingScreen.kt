package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssistantState
import com.example.ui.components.FuturisticOrb
import com.example.ui.components.GlassCard
import com.example.ui.components.HapticFeedbackHelper
import com.example.ui.components.ParticleBackground
import com.example.ui.theme.AlertRed
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.GlassBorderActive
import com.example.ui.theme.GlassWhite10
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.VoidBlack
import com.example.viewmodel.TarunViewModel

@Composable
fun OnboardingScreen(
    viewModel: TarunViewModel,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val appSettings by viewModel.appSettings.collectAsState()

    val micLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.refreshPermissions()
    }

    val notifPostLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.refreshPermissions()
    }

    val contactsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.refreshPermissions()
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.refreshPermissions()
    }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.refreshPermissions()
    }

    LaunchedEffect(Unit) {
        viewModel.refreshPermissions()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0C162A),
                        VoidBlack
                    )
                )
            )
            .testTag("onboarding_screen")
    ) {
        ParticleBackground(particleCount = 30)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                FuturisticOrb(
                    state = AssistantState.SPEAKING,
                    audioAmplitude = 0.35f,
                    size = 120.dp
                )
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Namaste ${appSettings.bossTitle} 👋",
                        color = NeonCyan,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Main Tarun hoon. Aapka futuristic voice-first personal AI assistant.",
                        color = Slate100,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }

            // Core Required Permissions Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderGlow = true
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "CORE REQUIRED PERMISSIONS",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // 1. Microphone
                        val hasMic = viewModel.permissionManager.isAudioPermissionGranted()
                        PermissionWizardStepRow(
                            stepNumber = "1",
                            icon = Icons.Default.Mic,
                            title = "Microphone (Mandatory)",
                            subtitle = "Tarun ko voice commands sunne ke liye microphone permission zaroori hai.",
                            isGranted = hasMic,
                            isRequired = true,
                            onGrant = {
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                micLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 2. Notification Posting
                        val hasNotifPost = viewModel.permissionManager.isNotificationPostGranted()
                        PermissionWizardStepRow(
                            stepNumber = "2",
                            icon = Icons.Default.Notifications,
                            title = "Notifications (Alerts)",
                            subtitle = "Tarun ko alerts aur status announce karne ke liye permission chahiye.",
                            isGranted = hasNotifPost,
                            isRequired = true,
                            onGrant = {
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notifPostLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. Notification Listener Access (WhatsApp)
                        val hasNotifAccess = viewModel.permissionManager.isNotificationAccessGranted()
                        PermissionWizardStepRow(
                            stepNumber = "3",
                            icon = Icons.Default.Hearing,
                            title = "Notification Access (WhatsApp)",
                            subtitle = "WhatsApp aur device notifications read karke announce karne ke liye.",
                            isGranted = hasNotifAccess,
                            isRequired = false,
                            onGrant = {
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                viewModel.permissionManager.openNotificationListenerSettings()
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 4. Accessibility Service
                        val hasAccessibility = viewModel.permissionManager.isAccessibilityServiceEnabled()
                        PermissionWizardStepRow(
                            stepNumber = "4",
                            icon = Icons.Default.TouchApp,
                            title = "Accessibility Service",
                            subtitle = "Back, Home, Recent Apps & device automation ke liye required hai.",
                            isGranted = hasAccessibility,
                            isRequired = false,
                            onGrant = {
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                viewModel.permissionManager.openAccessibilitySettings()
                            }
                        )
                    }
                }
            }

            // Optional Capabilities Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "OPTIONAL DEVICE CAPABILITIES",
                            color = Slate400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 5. Contacts
                        val hasContacts = viewModel.permissionManager.isPermissionGranted(Manifest.permission.READ_CONTACTS)
                        PermissionWizardStepRow(
                            stepNumber = "5",
                            icon = Icons.Default.Contacts,
                            title = "Contacts",
                            subtitle = "Name se phone call lagane ke liye.",
                            isGranted = hasContacts,
                            isRequired = false,
                            onGrant = {
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                contactsLauncher.launch(Manifest.permission.READ_CONTACTS)
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 6. Camera
                        val hasCamera = viewModel.permissionManager.isPermissionGranted(Manifest.permission.CAMERA)
                        PermissionWizardStepRow(
                            stepNumber = "6",
                            icon = Icons.Default.CameraAlt,
                            title = "Camera & Flashlight",
                            subtitle = "Camera kholne aur torch toggle karne ke liye.",
                            isGranted = hasCamera,
                            isRequired = false,
                            onGrant = {
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                cameraLauncher.launch(Manifest.permission.CAMERA)
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 7. Location
                        val hasLocation = viewModel.permissionManager.isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
                        PermissionWizardStepRow(
                            stepNumber = "7",
                            icon = Icons.Default.LocationOn,
                            title = "Location (Weather)",
                            subtitle = "Local weather aur location-aware assistance ke liye.",
                            isGranted = hasLocation,
                            isRequired = false,
                            onGrant = {
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                locationLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                            }
                        )
                    }
                }
            }

            // Finish Onboarding Button
            item {
                val isMicGranted = viewModel.permissionManager.isAudioPermissionGranted()
                Button(
                    onClick = {
                        HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                        viewModel.completeOnboarding()
                        onFinish()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("get_started_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMicGranted) Cyan500 else NeonCyan,
                        contentColor = VoidBlack
                    )
                ) {
                    Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, tint = VoidBlack)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isMicGranted) "LET'S START, BOSS" else "CONTINUE TO TARUN AI",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun PermissionWizardStepRow(
    stepNumber: String,
    icon: ImageVector,
    title: String,
    subtitle: String,
    isGranted: Boolean,
    isRequired: Boolean,
    onGrant: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    if (isGranted) HologramTeal.copy(alpha = 0.2f) else GlassBorderActive.copy(alpha = 0.2f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isGranted) Icons.Default.CheckCircle else icon,
                contentDescription = null,
                tint = if (isGranted) HologramTeal else Cyan500,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = Slate100,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (isRequired && !isGranted) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = AlertRed.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "REQUIRED",
                            color = AlertRed,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = subtitle,
                color = Slate400,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (isGranted) {
            Text(
                text = "✓ OK",
                color = HologramTeal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        } else {
            OutlinedButton(
                onClick = onGrant,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Cyan400)
            ) {
                Text(
                    text = "Enable",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
