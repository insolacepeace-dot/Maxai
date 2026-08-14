package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LanguageMode
import com.example.ui.components.GlassCard
import com.example.ui.components.HapticFeedbackHelper
import com.example.ui.theme.AlertRed
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.GlassWhite10
import com.example.ui.theme.GlassWhite5
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.VoidBlack
import com.example.viewmodel.TarunViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperModeScreen(
    viewModel: TarunViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val appSettings by viewModel.appSettings.collectAsState()
    val lastCommandName by viewModel.lastCommandName.collectAsState()
    val lastStructuredIntent by viewModel.lastStructuredIntent.collectAsState()
    val lastExecutionResult by viewModel.lastExecutionResult.collectAsState()
    val liveTranscription by viewModel.liveTranscription.collectAsState()
    val geminiTestStatus by viewModel.geminiTestStatus.collectAsState()

    val isNotificationGranted = viewModel.permissionManager.isNotificationAccessGranted()
    val isAccessibilityGranted = viewModel.permissionManager.isAccessibilityServiceEnabled()
    val isAudioGranted = viewModel.permissionManager.isAudioPermissionGranted()

    var testCustomPrompt by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DEVELOPER DIAGNOSTICS",
                        color = Slate100,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                            onBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Cyan400
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                            viewModel.testGeminiApi()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Test AI Connection",
                            tint = Cyan400
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VoidBlack)
            )
        },
        containerColor = VoidBlack,
        modifier = Modifier.testTag("developer_mode_screen")
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "SYSTEM SUBSYSTEM TELEMETRY",
                    color = Cyan500,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Subsystem Health Grid
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SubsystemStatusRow("AI Engine", if (appSettings.geminiEnabled && appSettings.geminiApiKey.isNotBlank()) "Gemini Cloud (Active)" else "Local Rule Engine (Free Mode)", true)
                        SubsystemStatusRow("Speech Recognizer (STT)", if (isAudioGranted) "Android SpeechRecognizer (Active)" else "Permission Denied", isAudioGranted)
                        SubsystemStatusRow("Text-To-Speech (TTS)", "Android TextToSpeech (Ready)", true)
                        SubsystemStatusRow("Notification Listener", if (isNotificationGranted) "Enabled (Bound)" else "Service Disabled", isNotificationGranted)
                        SubsystemStatusRow("Accessibility Service", if (isAccessibilityGranted) "Active (Gestures Ready)" else "Service Disabled", isAccessibilityGranted)
                    }
                }
            }

            // Quick Diagnostic Action Triggers
            item {
                Text(
                    text = "QUICK TEST TRIGGERS",
                    color = Cyan500,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                    viewModel.speakText("Namaste ${appSettings.bossTitle}, Tarun AI audio system operational hai.", "hi")
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Cyan400)
                            ) {
                                Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test TTS Hindi", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                    viewModel.speakText("નમસ્તે ${appSettings.bossTitle}, તારુણ AI ઓડિયો સિસ્ટમ સંપૂર્ણ કાર્યરત છે.", "gu")
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = HologramTeal)
                            ) {
                                Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test TTS Guj", fontSize = 11.sp)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                    viewModel.executeLocalTest("torch toggle")
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate100)
                            ) {
                                Icon(imageVector = Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test Local Torch", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                    viewModel.testGeminiApiDetailed()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Cyan400)
                            ) {
                                Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test Gemini AI", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            if (geminiTestStatus != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Cyan500.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Cyan500.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = geminiTestStatus ?: "",
                            color = Cyan400,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Interactive Command Dispatch Playground
            item {
                Text(
                    text = "COMMAND DISPATCH PLAYGROUND",
                    color = Cyan500,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = testCustomPrompt,
                            onValueChange = { testCustomPrompt = it },
                            placeholder = { Text("e.g. WhatsApp kholo or Torch on kar", color = Slate500, fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Cyan500,
                                unfocusedBorderColor = GlassWhite10,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            )
                        )

                        Button(
                            onClick = {
                                if (testCustomPrompt.isNotBlank()) {
                                    HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                    viewModel.handleUserInput(testCustomPrompt.trim())
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Cyan500)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = VoidBlack)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dispatch to Agent Router", color = VoidBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Last Live Speech Utterance
            item {
                Text(
                    text = "LAST SPEECH UTTERANCE",
                    color = Slate500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (liveTranscription.isNotBlank()) "“$liveTranscription”" else "(No utterance captured yet)",
                        color = if (liveTranscription.isNotBlank()) Slate100 else Slate500,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Last Structured Intent JSON
            item {
                Text(
                    text = "LAST STRUCTURED INTENT JSON",
                    color = Slate500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF070B14),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassWhite10),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(14.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = lastStructuredIntent,
                            color = HologramTeal,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Last Execution Result
            item {
                Text(
                    text = "LAST ANDROID EXECUTION RESULT",
                    color = Slate500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Command Type", color = Slate400, fontSize = 12.sp)
                            Text(lastCommandName, color = Cyan400, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = lastExecutionResult,
                            color = if (lastExecutionResult.startsWith("SUCCESS")) HologramTeal else Slate100,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun SubsystemStatusRow(name: String, status: String, isOk: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, color = Slate400, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isOk) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isOk) HologramTeal else AlertRed,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status,
                color = if (isOk) Slate100 else AlertRed,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
