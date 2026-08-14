package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssistantState
import com.example.data.model.CommandType
import com.example.ui.components.FuturisticOrb
import com.example.ui.components.GlassCard
import com.example.ui.components.HapticFeedbackHelper
import com.example.ui.components.ParticleBackground
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.AlertRed
import com.example.ui.theme.Blue600
import com.example.ui.theme.CorePulseViolet
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.GlassBorderActive
import com.example.ui.theme.GlassWhite10
import com.example.ui.theme.GlassWhite5
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBlack
import com.example.viewmodel.TarunViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainAiScreen(
    viewModel: TarunViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val assistantState by viewModel.assistantState.collectAsState()
    val audioAmplitude by viewModel.audioAmplitude.collectAsState()
    val liveTranscription by viewModel.liveTranscription.collectAsState()
    val lastAssistantReply by viewModel.lastAssistantReply.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val pendingConfirmation by viewModel.pendingConfirmationCommand.collectAsState()

    var currentTimeString by remember { mutableStateOf("12:45") }
    var currentAmPmString by remember { mutableStateOf("PM") }
    var currentDateString by remember { mutableStateOf("OCT 24, 2024") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            val timeFmt = SimpleDateFormat("hh:mm", Locale.getDefault())
            val amPmFmt = SimpleDateFormat("a", Locale.getDefault())
            val dateFmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            currentTimeString = timeFmt.format(now)
            currentAmPmString = amPmFmt.format(now).uppercase()
            currentDateString = dateFmt.format(now).uppercase()
            delay(1000)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_anim")
    val subtitlePulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "subtitle_alpha"
    )

    val quickSuggestions = listOf(
        "WhatsApp message check karo",
        "Torch on karo",
        "Volume badhao",
        "Wi-Fi settings kholo",
        "Camera kholo",
        "Kem chho Tarun?",
        "Recent apps dikhao"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
            .testTag("main_ai_screen")
    ) {
        // Large Immersive Background Glowing Auras (cyan-500/10 blur-3xl and blue-600/10 blur-2xl)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(340.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Cyan500.copy(alpha = 0.12f),
                            Blue600.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        ParticleBackground(particleCount = 28)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Immersive Header: System Status & Time/Date
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SYSTEM STATUS",
                        color = Cyan500,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = when (assistantState) {
                            AssistantState.IDLE -> "Online • Secure"
                            AssistantState.LISTENING -> "Online • Audio Active"
                            AssistantState.THINKING -> "Online • Computing"
                            AssistantState.SPEAKING -> "Online • Synthesizing"
                        },
                        color = Slate400,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = currentTimeString,
                            color = Slate100,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentAmPmString,
                            color = Slate400,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                    Text(
                        text = currentDateString,
                        color = Slate500,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Central Immersive Orb Reactor
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clickable {
                        HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                        if (assistantState == AssistantState.SPEAKING) {
                            viewModel.stopSpeaking()
                        } else if (assistantState == AssistantState.LISTENING) {
                            viewModel.stopListening()
                        } else {
                            viewModel.startListening()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                FuturisticOrb(
                    state = assistantState,
                    audioAmplitude = audioAmplitude,
                    size = 240.dp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title & Voice Subtitle Prompt
            Text(
                text = "TARUN",
                color = Slate100,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp,
                fontStyle = FontStyle.Italic
            )

            val displaySubtitle = if (assistantState == AssistantState.LISTENING && liveTranscription.isNotBlank()) {
                "\"$liveTranscription\""
            } else if (assistantState == AssistantState.SPEAKING) {
                "\"$lastAssistantReply\""
            } else {
                "\"Yes Boss, boliye.\""
            }

            Text(
                text = displaySubtitle,
                color = Cyan400.copy(alpha = subtitlePulseAlpha),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Audio Equalizer Waveform Bars (Immersive Cyan Equalizer)
            WaveformVisualizer(
                state = assistantState,
                amplitude = audioAmplitude,
                modifier = Modifier.padding(horizontal = 32.dp),
                height = 32.dp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Live Assistant Speech / Transcription Glass Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderGlow = assistantState != AssistantState.IDLE
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (assistantState == AssistantState.LISTENING) "VOICE INPUT BUFFER" else "INTELLIGENCE STREAM",
                            color = Cyan500,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        if (assistantState == AssistantState.SPEAKING) {
                            IconButton(
                                onClick = {
                                    HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                    viewModel.stopSpeaking()
                                },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Stop Speaking",
                                    tint = AlertRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val displayText = if (assistantState == AssistantState.LISTENING && liveTranscription.isNotBlank()) {
                        liveTranscription
                    } else {
                        lastAssistantReply
                    }

                    Text(
                        text = displayText,
                        color = Slate100,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Pending Confirmation Card (e.g. Reply Send)
                    AnimatedVisibility(
                        visible = pendingConfirmation != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Text(
                                text = "Say 'Haan' to confirm or 'Cancel' to abort.",
                                color = Slate400,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.handleUserInput("Haan") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500)
                                ) {
                                    Text("Confirm (Haan)", color = VoidBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { viewModel.handleUserInput("Cancel") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                                ) {
                                    Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Immersive Floating Control Bar (bg-white/5 backdrop-blur-xl border-white/10 rounded-3xl)
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = GlassWhite5,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassWhite10),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Menu Drawer Action Button
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                            onOpenDrawer()
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu Navigation",
                            tint = Slate400,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Center Floating Pulsing Mic Button with Cyan Glow
                    val isListening = assistantState == AssistantState.LISTENING
                    val micBackground = if (isListening) HologramTeal else Cyan500

                    Box(
                        modifier = Modifier.size(72.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Ambient cyan blur halo behind mic
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            micBackground.copy(alpha = 0.5f),
                                            micBackground.copy(alpha = 0.15f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )

                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(micBackground)
                                .clickable {
                                    HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                    if (isListening) {
                                        viewModel.stopListening()
                                    } else {
                                        viewModel.startListening()
                                    }
                                }
                                .testTag("push_to_talk_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Voice Input",
                                tint = VoidBlack,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    // Right Conversation History Action Button
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                            onNavigateToHistory()
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Conversation Memory",
                            tint = Slate400,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom System Telemetry Indicators ("Nav", "Control", "History")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onOpenDrawer() }
                ) {
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0x33FFFFFF))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "NAV",
                        color = Slate500,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                        viewModel.triggerQuickCommand(CommandType.TOGGLE_TORCH, "Torch toggle")
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0x33FFFFFF))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "CONTROL",
                        color = Slate500,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onNavigateToHistory() }
                ) {
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0x33FFFFFF))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "HISTORY",
                        color = Slate500,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Device Shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionIcon(
                    icon = Icons.Default.FlashlightOn,
                    label = "Torch",
                    onClick = {
                        HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                        viewModel.triggerQuickCommand(CommandType.TOGGLE_TORCH, "Torch toggle karo")
                    }
                )
                QuickActionIcon(
                    icon = Icons.Default.VolumeUp,
                    label = "Volume",
                    onClick = {
                        HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                        viewModel.triggerQuickCommand(CommandType.VOLUME_UP, "Volume badhao")
                    }
                )
                QuickActionIcon(
                    icon = Icons.Default.Wifi,
                    label = "Wi-Fi",
                    onClick = {
                        HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                        viewModel.triggerQuickCommand(CommandType.OPEN_WIFI_SETTINGS, "WiFi settings")
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Voice Command Suggestions Header & Chips
            Text(
                text = "VOICE COMMAND SUGGESTIONS",
                color = Slate500,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickSuggestions) { suggestion ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                viewModel.handleUserInput(suggestion)
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = GlassWhite5,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassWhite10)
                    ) {
                        Text(
                            text = suggestion,
                            color = Cyan400,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun QuickActionIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(GlassWhite5)
                .border(1.dp, GlassWhite10, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Cyan400,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Slate400,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

