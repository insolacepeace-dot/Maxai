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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AutomationEntity
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
fun AutomationScreen(
    viewModel: TarunViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val appSettings by viewModel.appSettings.collectAsState()
    val automations by viewModel.automations.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var routineName by remember { mutableStateOf("") }
    var selectedTrigger by remember { mutableStateOf("CHARGING_STARTED") }
    var selectedAction by remember { mutableStateOf("SPEAK_TEXT") }
    var actionPayload by remember { mutableStateOf("Charging connected Boss") }

    val defaultPresetRoutines = listOf(
        AutomationEntity(
            id = -1,
            name = "Charging Plugged Announcement",
            triggerType = "CHARGING_STARTED",
            triggerCondition = "AC/USB",
            actionType = "SPEAK_TEXT",
            actionPayload = "Fast charging active Boss, power protocols online.",
            isEnabled = true
        ),
        AutomationEntity(
            id = -2,
            name = "Morning Briefing Routine",
            triggerType = "TIME_OF_DAY",
            triggerCondition = "07:00 AM",
            actionType = "SPEAK_TEXT",
            actionPayload = "Good morning Boss! Your schedules and notifications are ready.",
            isEnabled = true
        ),
        AutomationEntity(
            id = -3,
            name = "Headphone Connect Media",
            triggerType = "HEADPHONES_CONNECTED",
            triggerCondition = "Bluetooth/Jack",
            actionType = "OPEN_APP",
            actionPayload = "com.google.android.youtube",
            isEnabled = false
        )
    )

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF0F172A),
            title = {
                Text("Create Automation Routine", color = Slate100, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = routineName,
                        onValueChange = { routineName = it },
                        label = { Text("Routine Name", color = Slate400, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan500,
                            unfocusedBorderColor = GlassWhite10,
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate100
                        )
                    )
                    OutlinedTextField(
                        value = actionPayload,
                        onValueChange = { actionPayload = it },
                        label = { Text("Spoken Message or App Package", color = Slate400, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan500,
                            unfocusedBorderColor = GlassWhite10,
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate100
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (routineName.isNotBlank()) {
                            viewModel.addAutomation(
                                name = routineName.trim(),
                                triggerType = selectedTrigger,
                                triggerCondition = "User trigger",
                                actionType = selectedAction,
                                actionPayload = actionPayload.trim()
                            )
                            routineName = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500)
                ) {
                    Text("Create", color = VoidBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "JARVIS AUTOMATIONS",
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
        floatingActionButton = {
            Button(
                onClick = {
                    HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                    showAddDialog = true
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Cyan500)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = VoidBlack)
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Routine", color = VoidBlack, fontWeight = FontWeight.Bold)
            }
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
                    text = "TRIGGER-ACTION AUTOMATION ENGINE",
                    color = Cyan500,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Configure contextual triggers that automatically execute voice alerts or device tasks.",
                    color = Slate400,
                    fontSize = 12.sp
                )
            }

            // Presets
            item {
                Text(
                    text = "SYSTEM PRESETS",
                    color = Slate500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            items(defaultPresetRoutines) { routine ->
                AutomationCard(
                    automation = routine,
                    onToggle = { /* preset toggle */ },
                    onTest = {
                        HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                        viewModel.testAutomation(routine)
                    },
                    onDelete = null
                )
            }

            // User Created Automations
            if (automations.isNotEmpty()) {
                item {
                    Text(
                        text = "CUSTOM ROUTINES (${automations.size})",
                        color = Slate500,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                items(automations, key = { it.id }) { routine ->
                    AutomationCard(
                        automation = routine,
                        onToggle = { enabled ->
                            HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                            viewModel.toggleAutomation(routine.id, enabled)
                        },
                        onTest = {
                            HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                            viewModel.testAutomation(routine)
                        },
                        onDelete = {
                            HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                            viewModel.deleteAutomation(routine.id)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun AutomationCard(
    automation: AutomationEntity,
    onToggle: (Boolean) -> Unit,
    onTest: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val triggerIcon: ImageVector = when (automation.triggerType) {
        "CHARGING_STARTED" -> Icons.Default.BatteryChargingFull
        "TIME_OF_DAY" -> Icons.Default.Schedule
        "HEADPHONES_CONNECTED" -> Icons.Default.Headphones
        else -> Icons.Default.AutoAwesome
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Cyan500.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = triggerIcon, contentDescription = null, tint = Cyan400, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = automation.name,
                            color = Slate100,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${automation.triggerType} ➔ ${automation.actionType}",
                            color = HologramTeal,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Switch(
                    checked = automation.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = VoidBlack,
                        checkedTrackColor = Cyan500,
                        uncheckedThumbColor = Slate400,
                        uncheckedTrackColor = GlassWhite10
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GlassWhite5,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassWhite10),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Action: “${automation.actionPayload}”",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onTest,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = VoidBlack, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test Trigger", color = VoidBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (onDelete != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
