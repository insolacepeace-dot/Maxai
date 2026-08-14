package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiMode
import com.example.ui.components.GlassCard
import com.example.ui.components.HapticFeedbackHelper
import com.example.ui.components.ParticleBackground
import com.example.ui.theme.AlertRed
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassWhite10
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.SpaceDarkBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBlack
import com.example.viewmodel.TarunViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiSettingsScreen(
    viewModel: TarunViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val appSettings by viewModel.appSettings.collectAsState()
    val geminiDetailedResult by viewModel.geminiDetailedStatus.collectAsState()
    val isTesting by viewModel.isTestingGemini.collectAsState()

    var apiKeyInput by remember(appSettings) { mutableStateOf(appSettings.geminiApiKey) }
    var selectedModel by remember(appSettings) { mutableStateOf(appSettings.geminiModel) }
    var temperature by remember(appSettings) { mutableFloatStateOf(appSettings.geminiTemperature) }
    var maxTokens by remember(appSettings) { mutableIntStateOf(appSettings.geminiMaxTokens) }
    var timeoutSeconds by remember(appSettings) { mutableIntStateOf(appSettings.geminiTimeoutSeconds) }
    var keyVisible by remember { mutableStateOf(false) }
    var modelDropdownExpanded by remember { mutableStateOf(false) }

    val models = listOf(
        "gemini-2.5-flash",
        "gemini-2.5-pro",
        "gemini-2.0-flash",
        "gemini-1.5-flash"
    )

    val isKeyConfigured = appSettings.geminiApiKey.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "GEMINI AI SETTINGS",
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
        modifier = Modifier.testTag("gemini_settings_screen")
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
                // Connection Status Header Card
                GlassCard(modifier = Modifier.fillMaxWidth(), borderGlow = isKeyConfigured) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        if (isKeyConfigured) HologramTeal.copy(alpha = 0.2f) else AlertRed.copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isKeyConfigured) Icons.Default.Cloud else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (isKeyConfigured) HologramTeal else AlertRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "GEMINI AI ENGINE",
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = if (isKeyConfigured) "Key Configured & Active" else "No API Key (Local Mode)",
                                    color = if (isKeyConfigured) Slate100 else Slate400,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isKeyConfigured) HologramTeal.copy(alpha = 0.15f) else Color(0x33FFB300),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isKeyConfigured) HologramTeal.copy(alpha = 0.5f) else Color(0x66FFB300)
                            )
                        ) {
                            Text(
                                text = if (isKeyConfigured) "CONNECTED" else "NOT CONNECTED",
                                color = if (isKeyConfigured) HologramTeal else Color(0xFFFFB300),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // AI Mode Selector Card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "AI ENGINE MODE",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        AiMode.entries.forEach { mode ->
                            val isSelected = appSettings.aiMode == mode
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                        viewModel.updateAppSettings(appSettings.copy(aiMode = mode))
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isSelected) NeonCyan else Slate500,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = mode.displayName,
                                        color = if (isSelected) Slate100 else Slate400,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = mode.description,
                                        color = Slate500,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // API Key Entry Card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GEMINI API KEY",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Enter your Google AI Studio API key. Stored securely on-device and never logged.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            placeholder = { Text("AIzaSy...", color = TextSecondary) },
                            visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { keyVisible = !keyVisible }) {
                                    Icon(
                                        imageVector = if (keyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Show/Hide Key",
                                        tint = NeonCyan
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("gemini_api_key_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = GlassSurfaceDark,
                                unfocusedContainerColor = GlassSurfaceDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: Save, Clear, Show/Hide
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                    viewModel.saveGeminiApiKey(apiKeyInput)
                                    Toast.makeText(context, "API Key saved securely!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_api_key_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Cyan500)
                            ) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = VoidBlack, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Key", color = VoidBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            if (apiKeyInput.isNotBlank() || isKeyConfigured) {
                                OutlinedButton(
                                    onClick = {
                                        HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                        apiKeyInput = ""
                                        viewModel.clearGeminiApiKey()
                                        Toast.makeText(context, "API Key cleared.", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = AlertRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clear", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Test Connection Button & Detailed Results
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CONNECTION VERIFICATION",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            if (isTesting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = NeonCyan,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sends a live 'TARUN ONLINE' test probe to verify API key validity, latency and quota.",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                viewModel.testGeminiApiDetailed(
                                    overrideKey = apiKeyInput.ifBlank { null },
                                    overrideModel = selectedModel
                                )
                            },
                            enabled = !isTesting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("test_connection_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = VoidBlack)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTesting) "TESTING CONNECTION..." else "TEST CONNECTION",
                                color = VoidBlack,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (geminiDetailedResult != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            val res = geminiDetailedResult!!
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (res.success) HologramTeal.copy(alpha = 0.15f) else AlertRed.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (res.success) HologramTeal.copy(alpha = 0.4f) else AlertRed.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (res.success) Icons.Default.CheckCircle else Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (res.success) HologramTeal else AlertRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = res.statusText,
                                            color = if (res.success) HologramTeal else AlertRed,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    if (res.responseText != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Response: \"${res.responseText}\"",
                                            color = Slate100,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Model Selection
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "SELECT GEMINI MODEL",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = modelDropdownExpanded,
                            onExpandedChange = { modelDropdownExpanded = !modelDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedModel,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelDropdownExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = GlassSurfaceDark,
                                    unfocusedContainerColor = GlassSurfaceDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = modelDropdownExpanded,
                                onDismissRequest = { modelDropdownExpanded = false }
                            ) {
                                models.forEach { m ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(m, fontWeight = FontWeight.Bold)
                                                Text(
                                                    text = when (m) {
                                                        "gemini-2.5-flash" -> "Recommended - Ultra fast & intelligent"
                                                        "gemini-2.5-pro" -> "High capability & deep reasoning"
                                                        "gemini-2.0-flash" -> "Fast multimodal agentic model"
                                                        else -> "Standard Gemini model"
                                                    },
                                                    fontSize = 10.sp,
                                                    color = Slate400
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedModel = m
                                            modelDropdownExpanded = false
                                            viewModel.updateAppSettings(appSettings.copy(geminiModel = m))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Advanced Generation Parameters
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GENERATION PARAMETERS",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Temperature
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Temperature (Creativity)", color = Slate400, fontSize = 12.sp)
                                Text(String.format("%.2f", temperature), color = HologramTeal, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = temperature,
                                onValueChange = {
                                    temperature = it
                                    viewModel.updateAppSettings(appSettings.copy(geminiTemperature = it))
                                },
                                valueRange = 0.0f..1.0f,
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonCyan,
                                    activeTrackColor = HologramTeal,
                                    inactiveTrackColor = GlassBorder
                                )
                            )
                        }

                        // Max Tokens
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Max Output Tokens", color = Slate400, fontSize = 12.sp)
                                Text("$maxTokens tokens", color = HologramTeal, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = maxTokens.toFloat(),
                                onValueChange = {
                                    maxTokens = it.toInt()
                                    viewModel.updateAppSettings(appSettings.copy(geminiMaxTokens = it.toInt()))
                                },
                                valueRange = 128f..2048f,
                                steps = 14,
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonCyan,
                                    activeTrackColor = HologramTeal,
                                    inactiveTrackColor = GlassBorder
                                )
                            )
                        }

                        // Timeout
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Network Timeout", color = Slate400, fontSize = 12.sp)
                                Text("$timeoutSeconds s", color = HologramTeal, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = timeoutSeconds.toFloat(),
                                onValueChange = {
                                    timeoutSeconds = it.toInt()
                                    viewModel.updateAppSettings(appSettings.copy(geminiTimeoutSeconds = it.toInt()))
                                },
                                valueRange = 10f..60f,
                                steps = 9,
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonCyan,
                                    activeTrackColor = HologramTeal,
                                    inactiveTrackColor = GlassBorder
                                )
                            )
                        }
                    }
                }

                // Security & Privacy Disclosure
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF070E1A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassWhite10),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = HologramTeal, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Security Guarantee: Your Gemini API Key is stored only in Android private SharedPreferences on this device. Keys are never transmitted to third parties and are excluded from system logs.",
                            color = Slate400,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
