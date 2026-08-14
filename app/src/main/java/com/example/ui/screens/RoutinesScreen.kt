package com.example.ui.screens

import android.app.TimePickerDialog
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AlarmEntity
import com.example.data.local.AutomationEntity
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CorePulseViolet
import com.example.ui.theme.Cyan400
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassWhite10
import com.example.ui.theme.GlassWhite5
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.SpaceDarkBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TarunViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    viewModel: TarunViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Alarms (Exact)", "Automations", "Proactive Agent")

    val alarms by viewModel.alarms.collectAsState()
    val automations by viewModel.automations.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()

    var showAddAlarmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ROUTINES & ALARMS",
                            color = Slate100,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Real Android AlarmManager & Automations",
                            color = NeonCyan,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("routines_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate100)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SpaceDarkBackground
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddAlarmDialog = true },
                    containerColor = NeonCyan,
                    contentColor = SpaceDarkBackground,
                    modifier = Modifier.testTag("add_alarm_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Alarm")
                }
            }
        },
        containerColor = SpaceDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = GlassSurfaceDark,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonCyan
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) NeonCyan else Slate400
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> AlarmsTab(
                    alarms = alarms,
                    onToggle = { id, enabled -> viewModel.toggleAlarm(id, enabled) },
                    onDelete = { id -> viewModel.deleteAlarm(id) }
                )
                1 -> AutomationsTab(
                    automations = automations,
                    onToggle = { id, enabled -> viewModel.toggleAutomation(id, enabled) },
                    onDelete = { id -> viewModel.deleteAutomation(id) }
                )
                2 -> ProactiveAgentTab(
                    appSettings = appSettings,
                    onUpdateSettings = { viewModel.updateAppSettings(it) }
                )
            }
        }
    }

    if (showAddAlarmDialog) {
        AddAlarmDialog(
            onDismiss = { showAddAlarmDialog = false },
            onConfirm = { hour, minute, label, days, vibrate ->
                viewModel.addAlarm(hour, minute, label, days, vibrate)
                showAddAlarmDialog = false
            }
        )
    }
}

@Composable
fun AlarmsTab(
    alarms: List<AlarmEntity>,
    onToggle: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "ACTIVE ANDROID ALARMS",
                color = Slate100,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        if (alarms.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GlassSurfaceDark),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Alarm, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No Alarms Set", color = Slate100, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tell TARUN 'Set alarm for 7:00 AM' or tap '+' to create a precise AlarmManager alarm.",
                            color = Slate400,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(alarms, key = { it.id }) { alarm ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (alarm.isEnabled) GlassSurfaceDark else Color(0xFF101726)
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = String.format("%02d:%02d", alarm.hour, alarm.minute),
                                color = if (alarm.isEnabled) NeonCyan else Slate500,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = alarm.label,
                                color = if (alarm.isEnabled) Slate100 else Slate500,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Repeats: ${alarm.daysOfWeek} • Vibrate: ${if (alarm.isVibrate) "Yes" else "No"}",
                                color = Slate500,
                                fontSize = 11.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = alarm.isEnabled,
                                onCheckedChange = { onToggle(alarm.id, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeonCyan,
                                    checkedTrackColor = NeonCyan.copy(alpha = 0.5f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { onDelete(alarm.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Slate500)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AutomationsTab(
    automations: List<AutomationEntity>,
    onToggle: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit
) {
    val defaultPresets = listOf(
        Triple("Morning Briefing", "At 07:30 AM speak weather, calendar, and unread WhatsApp", Icons.Default.WbSunny),
        Triple("Night Focus Routine", "At 11:00 PM enable DND, turn off torch, and play focus music", Icons.Default.Brightness4),
        Triple("Battery Saver Trigger", "When battery falls below 20% announce and adjust brightness", Icons.Default.ElectricBolt)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "AUTOMATED ROUTINES & TRIGGERS",
                color = Slate100,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        items(defaultPresets) { preset ->
            Card(
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
                            .background(CorePulseViolet.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(preset.third, contentDescription = null, tint = CorePulseViolet, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(preset.first, color = Slate100, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(preset.second, color = Slate400, fontSize = 12.sp)
                    }
                    Switch(
                        checked = true,
                        onCheckedChange = {},
                        colors = SwitchDefaults.colors(checkedThumbColor = CorePulseViolet, checkedTrackColor = CorePulseViolet.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}

@Composable
fun ProactiveAgentTab(
    appSettings: com.example.data.model.AppSettings,
    onUpdateSettings: (com.example.data.model.AppSettings) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GlassSurfaceDark),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PROACTIVE TARUN SETTINGS",
                        color = NeonCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Enable autonomous contextual assistance, morning alerts, and proactive notifications.",
                        color = Slate400,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Proactive Greeting on Unlock", color = Slate100, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Briefly greets you with unread alerts", color = Slate400, fontSize = 11.sp)
                        }
                        Switch(
                            checked = appSettings.proactiveSuggestions,
                            onCheckedChange = {
                                onUpdateSettings(appSettings.copy(proactiveSuggestions = it))
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonCyan.copy(alpha = 0.5f))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Automatic WhatsApp Announcements", color = Slate100, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Announces incoming messages aloud", color = Slate400, fontSize = 11.sp)
                        }
                        Switch(
                            checked = appSettings.autoReadNotifications,
                            onCheckedChange = {
                                onUpdateSettings(appSettings.copy(autoReadNotifications = it))
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonCyan.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddAlarmDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, String, String, Boolean) -> Unit
) {
    val cal = Calendar.getInstance()
    var hour by remember { mutableIntStateOf(cal.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(cal.get(Calendar.MINUTE)) }
    var label by remember { mutableStateOf("Wake up with Tarun") }
    var vibrate by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Exact Alarm", color = Slate100, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format("%02d : %02d", hour, minute),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { hour = (hour + 1) % 24 },
                        colors = ButtonDefaults.buttonColors(containerColor = GlassWhite10)
                    ) {
                        Text("+ Hour")
                    }
                    Button(
                        onClick = { minute = (minute + 5) % 60 },
                        colors = ButtonDefaults.buttonColors(containerColor = GlassWhite10)
                    ) {
                        Text("+ 5 Min")
                    }
                }

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Alarm Label") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Vibration", color = Slate100, fontSize = 13.sp)
                    Switch(
                        checked = vibrate,
                        onCheckedChange = { vibrate = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonCyan.copy(alpha = 0.5f))
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(hour, minute, label, "DAILY", vibrate) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = SpaceDarkBackground)
            ) {
                Text("Set Alarm")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = SpaceDarkBackground
    )
}
