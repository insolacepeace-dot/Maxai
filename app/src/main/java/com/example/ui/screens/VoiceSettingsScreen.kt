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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.components.ParticleBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SpaceDarkBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TarunViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSettingsScreen(
    viewModel: TarunViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val voiceSettings by viewModel.voiceSettings.collectAsState()
    val availableVoices by viewModel.availableVoices.collectAsState()

    var pitch by remember(voiceSettings) { mutableFloatStateOf(voiceSettings.pitch) }
    var speed by remember(voiceSettings) { mutableFloatStateOf(voiceSettings.speed) }
    var selectedLanguage by remember(voiceSettings) { mutableStateOf(voiceSettings.languageMode) }
    var selectedVoice by remember(voiceSettings) { mutableStateOf(voiceSettings.selectedVoiceName) }
    var wakePhrase by remember(voiceSettings) { mutableStateOf(voiceSettings.wakePhrase) }

    var langDropdownExpanded by remember { mutableStateOf(false) }
    var voiceDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "VOICE & SPEECH SETTINGS",
                        color = NeonCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SpaceDarkBackground)
            )
        },
        containerColor = SpaceDarkBackground,
        modifier = Modifier.testTag("voice_settings_screen")
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A1424),
                            SpaceDarkBackground
                        )
                    )
                )
        ) {
            ParticleBackground(particleCount = 20)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Language Mode Selector
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "PRIMARY LANGUAGE MODE",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = langDropdownExpanded,
                            onExpandedChange = { langDropdownExpanded = !langDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedLanguage.displayName,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = langDropdownExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = GlassSurfaceDark,
                                    unfocusedContainerColor = GlassSurfaceDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = langDropdownExpanded,
                                onDismissRequest = { langDropdownExpanded = false }
                            ) {
                                LanguageMode.entries.forEach { mode ->
                                    DropdownMenuItem(
                                        text = { Text(mode.displayName) },
                                        onClick = {
                                            selectedLanguage = mode
                                            langDropdownExpanded = false
                                            viewModel.updateVoiceSettings(voiceSettings.copy(languageMode = mode))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Voice Pitch Slider
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "VOICE PITCH",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = String.format("%.2fx", pitch),
                                color = HologramTeal,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Slider(
                            value = pitch,
                            onValueChange = {
                                pitch = it
                                viewModel.updateVoiceSettings(voiceSettings.copy(pitch = it))
                            },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = HologramTeal,
                                inactiveTrackColor = GlassBorder
                            )
                        )
                    }
                }

                // Voice Speed Slider
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "SPEECH RATE / SPEED",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = String.format("%.2fx", speed),
                                color = HologramTeal,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Slider(
                            value = speed,
                            onValueChange = {
                                speed = it
                                viewModel.updateVoiceSettings(voiceSettings.copy(speed = it))
                            },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = HologramTeal,
                                inactiveTrackColor = GlassBorder
                            )
                        )
                    }
                }

                // System TTS Voice Selection
                if (availableVoices.isNotEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "DEVICE TTS VOICE ENGINE",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            ExposedDropdownMenuBox(
                                expanded = voiceDropdownExpanded,
                                onExpandedChange = { voiceDropdownExpanded = !voiceDropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedVoice.ifBlank { "Default Natural Voice" },
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = voiceDropdownExpanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = GlassSurfaceDark,
                                        unfocusedContainerColor = GlassSurfaceDark,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = voiceDropdownExpanded,
                                    onDismissRequest = { voiceDropdownExpanded = false }
                                ) {
                                    availableVoices.take(15).forEach { voiceName ->
                                        DropdownMenuItem(
                                            text = { Text(voiceName) },
                                            onClick = {
                                                selectedVoice = voiceName
                                                voiceDropdownExpanded = false
                                                viewModel.updateVoiceSettings(voiceSettings.copy(selectedVoiceName = voiceName))
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Wake Phrase Setting
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "ASSISTANT WAKE PHRASE",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = wakePhrase,
                            onValueChange = {
                                wakePhrase = it
                                viewModel.updateVoiceSettings(voiceSettings.copy(wakePhrase = it))
                            },
                            placeholder = { Text("e.g. Tarun", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = GlassSurfaceDark,
                                unfocusedContainerColor = GlassSurfaceDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                }

                // Test Voice Button
                Button(
                    onClick = {
                        HapticFeedbackHelper.performClickHaptic(context)
                        val sample = when (selectedLanguage) {
                            LanguageMode.GUJARATI -> "નમસ્તે બોસ, હું તરુણ છું. હું આપના આદેશ માટે તૈયાર છું."
                            LanguageMode.HINDI -> "नमस्ते बॉस, मैं तरुण हूँ। मैं आपके हर हुक्म के लिए तैयार हूँ।"
                            LanguageMode.ENGLISH -> "Hello Boss, I am Tarun. All systems are operational."
                            else -> "Namaste Boss, main Tarun hoon. Aapka personal AI voice assistant."
                        }
                        val langCode = when (selectedLanguage) {
                            LanguageMode.GUJARATI -> "gu"
                            LanguageMode.HINDI -> "hi"
                            LanguageMode.ENGLISH -> "en"
                            else -> "hi"
                        }
                        viewModel.speakText(sample, langCode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = SpaceDarkBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TEST VOICE SYNTHESIS",
                        color = SpaceDarkBackground,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
