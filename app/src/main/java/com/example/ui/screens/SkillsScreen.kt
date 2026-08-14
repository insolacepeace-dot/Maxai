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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.HapticFeedbackHelper
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

data class SkillItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val category: String,
    val samplePhrases: List<String>,
    val requiresPermission: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsScreen(
    viewModel: TarunViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val appSettings by viewModel.appSettings.collectAsState()

    val skillsList = listOf(
        SkillItem(
            id = "whatsapp_intelligence",
            title = "WhatsApp Intelligence",
            description = "Read incoming WhatsApp notifications, draft context-aware replies, and auto-confirm with Boss.",
            icon = Icons.Default.Chat,
            category = "COMMUNICATION",
            samplePhrases = listOf("WhatsApp pe kya aaya?", "Rahul ko reply bhej kal milte hain", "WhatsApp kholo")
        ),
        SkillItem(
            id = "voice_phone_control",
            title = "Phone & Hardware Control",
            description = "Direct hardware automation for flashlight, volume adjustments, alarms, and settings panels.",
            icon = Icons.Default.FlashlightOn,
            category = "HARDWARE",
            samplePhrases = listOf("Torch on kar", "Volume 70 percent kar", "Alarm laga de subah 7 baje")
        ),
        SkillItem(
            id = "accessibility_gestures",
            title = "Accessibility Gestures",
            description = "Execute Back, Home, and Recent Apps system navigation gestures securely on command.",
            icon = Icons.Default.TouchApp,
            category = "ACCESSIBILITY",
            samplePhrases = listOf("Back kar", "Home par ja", "Recent apps kholo")
        ),
        SkillItem(
            id = "multilingual_ai",
            title = "Multilingual Intelligence",
            description = "Seamless spoken understanding in Hindi, Gujarati, English, Hinglish, and Gujlish.",
            icon = Icons.Default.Language,
            category = "AI INTELLIGENCE",
            samplePhrases = listOf("Su chale che?", "Aaje weather kevu che?", "Explain quantum computing in Hindi")
        ),
        SkillItem(
            id = "calling_contacts",
            title = "Contacts & Calling",
            description = "Resolve nicknames (Papa, Mummy, Rahul) and initiate direct calls after Boss confirmation.",
            icon = Icons.Default.Call,
            category = "COMMUNICATION",
            samplePhrases = listOf("Papa ko call karo", "Rahul ko call lagao")
        ),
        SkillItem(
            id = "navigation_maps",
            title = "Navigation & Maps",
            description = "Search places and initiate turn-by-turn navigation directly via Google Maps.",
            icon = Icons.Default.Map,
            category = "PRODUCTIVITY",
            samplePhrases = listOf("Google Maps kholo", "Directions to Airport")
        ),
        SkillItem(
            id = "media_entertainment",
            title = "Media & Music",
            description = "Launch YouTube, Spotify, or your favorite music players on command.",
            icon = Icons.Default.MusicNote,
            category = "MEDIA",
            samplePhrases = listOf("YouTube open karo", "Instagram kholo", "Spotify chalu karo")
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SKILLS & CAPABILITIES",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VoidBlack)
            )
        },
        containerColor = VoidBlack
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
                    text = "TARUN CORE SKILL MATRIX",
                    color = Cyan500,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap any test phrase to execute the skill directly through Tarun's agent engine.",
                    color = Slate400,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(skillsList, key = { it.id }) { skill ->
                SkillCard(
                    skill = skill,
                    onExecutePhrase = { phrase ->
                        HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                        viewModel.handleUserInput(phrase)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SkillCard(
    skill: SkillItem,
    onExecutePhrase: (String) -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 14.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Cyan500.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Cyan500.copy(alpha = 0.4f)),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = skill.icon,
                            contentDescription = null,
                            tint = Cyan400,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = skill.title,
                        color = Slate100,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = skill.category,
                        color = HologramTeal,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = skill.description,
                color = Slate400,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "TRY ASKING:",
                color = Slate500,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                skill.samplePhrases.forEach { phrase ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GlassWhite5,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassWhite10),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "“$phrase”",
                                color = Slate100,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { onExecutePhrase(phrase) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Cyan500),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = VoidBlack,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Test",
                                    color = VoidBlack,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
