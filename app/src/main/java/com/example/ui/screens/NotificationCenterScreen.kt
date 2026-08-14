package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NotificationEventEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen(
    viewModel: TarunViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val appSettings by viewModel.appSettings.collectAsState()
    val allNotifications by viewModel.allNotifications.collectAsState()
    val isNotificationListenerEnabled = viewModel.permissionManager.isNotificationAccessGranted()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = listOf("ALL", "WHATSAPP", "TELEGRAM", "GMAIL", "SMS", "CALLS", "OTHER")

    val filteredNotifications = allNotifications.filter { notif ->
        val matchesCategory = when (selectedCategory) {
            "ALL" -> true
            "WHATSAPP" -> notif.isWhatsApp || notif.packageName.contains("whatsapp", ignoreCase = true)
            "TELEGRAM" -> notif.packageName.contains("telegram", ignoreCase = true)
            "GMAIL" -> notif.packageName.contains("gm", ignoreCase = true) || notif.packageName.contains("google.android.gm", ignoreCase = true)
            "SMS" -> notif.packageName.contains("messaging", ignoreCase = true) || notif.packageName.contains("mms", ignoreCase = true)
            "CALLS" -> notif.packageName.contains("dialer", ignoreCase = true) || notif.packageName.contains("telecom", ignoreCase = true)
            else -> !notif.isWhatsApp
        }
        val matchesSearch = searchQuery.isBlank() ||
                notif.sender.contains(searchQuery, ignoreCase = true) ||
                notif.text.contains(searchQuery, ignoreCase = true) ||
                notif.packageName.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NOTIFICATION INTELLIGENCE",
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
                    if (allNotifications.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                viewModel.clearAllNotifications()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear All Notifications",
                                tint = AlertRed
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VoidBlack)
            )
        },
        containerColor = VoidBlack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Notification Access Warning Banner if not enabled
            if (!isNotificationListenerEnabled) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AlertRed.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = AlertRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notification Access Disabled",
                                color = Slate100,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Enable Notification Listener in Android Settings so Tarun can read WhatsApp & app alerts.",
                                color = Slate400,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                viewModel.permissionManager.openNotificationListenerSettings()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Enable", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // AI Action Quick Triggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                        viewModel.handleUserInput("Last WhatsApp message bata")
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = VoidBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Read Last WhatsApp", color = VoidBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                        viewModel.handleUserInput("Notifications summarize karo")
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GlassWhite10)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = Cyan400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Summarize (AI)", color = Slate100, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notification_search_input"),
                placeholder = { Text("Search sender, app, message...", color = Slate500, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Cyan400)
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cyan500,
                    unfocusedBorderColor = GlassWhite10,
                    focusedTextColor = Slate100,
                    unfocusedTextColor = Slate100,
                    cursorColor = Cyan400
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                HapticFeedbackHelper.performClickHaptic(context, appSettings.hapticFeedbackEnabled)
                                selectedCategory = category
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Cyan500 else GlassWhite5,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Cyan500 else GlassWhite10
                        )
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) VoidBlack else Slate400,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notifications List
            if (filteredNotifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Slate500,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No notifications detected",
                            color = Slate400,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Incoming notifications will appear here automatically.",
                            color = Slate500,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredNotifications, key = { it.id }) { notif ->
                        NotificationItemCard(
                            notification = notif,
                            onSpeak = {
                                val textToSpeak = "${appSettings.bossTitle}, ${notif.sender} ne kaha: ${notif.text}"
                                viewModel.speakText(textToSpeak)
                            },
                            onDelete = {
                                viewModel.deleteNotification(notif.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItemCard(
    notification: NotificationEventEntity,
    onSpeak: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(notification.timestamp) {
        val sdf = SimpleDateFormat("hh:mm a • dd MMM", Locale.getDefault())
        sdf.format(Date(notification.timestamp))
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 12.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (notification.isWhatsApp) HologramTeal.copy(alpha = 0.2f) else Cyan500.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (notification.isWhatsApp) HologramTeal.copy(alpha = 0.5f) else Cyan500.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = if (notification.isWhatsApp) "WHATSAPP" else notification.packageName.substringAfterLast(".").uppercase(),
                            color = if (notification.isWhatsApp) HologramTeal else Cyan400,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = notification.sender.ifBlank { "Notification" },
                        color = Slate100,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = dateStr,
                    color = Slate500,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = notification.text,
                color = Slate400,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Read Aloud",
                        tint = Cyan400,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
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
