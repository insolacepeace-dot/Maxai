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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MemoryFactEntity
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
fun MemoryScreen(
    viewModel: TarunViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val appSettings by viewModel.appSettings.collectAsState()
    val memoryFacts by viewModel.memoryFacts.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var newFactKey by remember { mutableStateOf("") }
    var newFactValue by remember { mutableStateOf("") }
    var newFactCategory by remember { mutableStateOf("Preference") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF0F172A),
            title = {
                Text(
                    text = "Add Memory Fact",
                    color = Slate100,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newFactKey,
                        onValueChange = { newFactKey = it },
                        label = { Text("Topic / Key (e.g. Favorite Car)", color = Slate400, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan500,
                            unfocusedBorderColor = GlassWhite10,
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate100
                        )
                    )
                    OutlinedTextField(
                        value = newFactValue,
                        onValueChange = { newFactValue = it },
                        label = { Text("Detail / Value (e.g. Porsche 911)", color = Slate400, fontSize = 12.sp) },
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
                        if (newFactKey.isNotBlank() && newFactValue.isNotBlank()) {
                            viewModel.addMemoryFact(newFactKey.trim(), newFactValue.trim(), newFactCategory)
                            newFactKey = ""
                            newFactValue = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500)
                ) {
                    Text("Save", color = VoidBlack, fontWeight = FontWeight.Bold)
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
                        text = "TARUN MEMORY & CONTEXT",
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
                    if (memoryFacts.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                viewModel.clearAllMemoryFacts()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear All Memory",
                                tint = AlertRed
                            )
                        }
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
                Text("Add Fact", color = VoidBlack, fontWeight = FontWeight.Bold)
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
            // Memory Toggle Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Cyan400,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Conversation Memory",
                                    color = Slate100,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Allow Tarun to remember recent context across turns",
                                    color = Slate400,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = appSettings.conversationMemoryEnabled,
                            onCheckedChange = { checked ->
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                viewModel.updateAppSettings(appSettings.copy(conversationMemoryEnabled = checked))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VoidBlack,
                                checkedTrackColor = Cyan500,
                                uncheckedThumbColor = Slate400,
                                uncheckedTrackColor = GlassWhite10
                            )
                        )
                    }
                }
            }

            // Fixed Boss Memory Core Facts
            item {
                Text(
                    text = "CORE MEMORY FACTS",
                    color = Cyan500,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("User Title", color = Slate400, fontSize = 12.sp)
                            Text(appSettings.bossTitle, color = Cyan400, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("AI Engine", color = Slate400, fontSize = 12.sp)
                            Text(if (appSettings.geminiEnabled) "Gemini 2.5 Flash + Local" else "Local Rule Engine", color = HologramTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Language Adaptivity", color = Slate400, fontSize = 12.sp)
                            Text("Hindi, Gujarati, English, Hinglish", color = Slate100, fontSize = 12.sp)
                        }
                    }
                }
            }

            // User Memory Facts List
            item {
                Text(
                    text = "CUSTOM FACTS (${memoryFacts.size})",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (memoryFacts.isEmpty()) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = Slate500, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No custom memory facts saved yet", color = Slate400, fontSize = 12.sp)
                            Text("Tap '+ Add Fact' or tell Tarun 'Remember that my car is Porsche'", color = Slate500, fontSize = 11.sp)
                        }
                    }
                }
            } else {
                items(memoryFacts, key = { it.id }) { fact ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = fact.key,
                                    color = Cyan400,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = fact.value,
                                    color = Slate100,
                                    fontSize = 12.sp
                                )
                            }
                            IconButton(
                                onClick = {
                                    HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                    viewModel.deleteMemoryFact(fact.id)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = AlertRed.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
