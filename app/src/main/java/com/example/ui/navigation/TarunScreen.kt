package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TarunScreen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Splash : TarunScreen("splash", "Splash")
    object Onboarding : TarunScreen("onboarding", "Onboarding")
    object MainAi : TarunScreen("main_ai", "TARUN AI", Icons.Default.Mic)
    object History : TarunScreen("history", "Conversation History", Icons.Default.Chat)
    object NotificationCenter : TarunScreen("notifications", "Notification Center", Icons.Default.Notifications)
    object DeviceControl : TarunScreen("device_control", "Device Control", Icons.Default.Dashboard)
    object Skills : TarunScreen("skills", "Skills & Capabilities", Icons.Default.Stars)
    object VoiceSettings : TarunScreen("voice_settings", "Voice Settings", Icons.Default.GraphicEq)
    object GeminiSettings : TarunScreen("gemini_settings", "Gemini AI", Icons.Default.Psychology)
    object PermissionCenter : TarunScreen("permissions", "Permission Center", Icons.Default.VerifiedUser)
    object Memory : TarunScreen("memory", "Memory & Context", Icons.Default.AutoAwesome)
    object Automation : TarunScreen("automation", "Automations", Icons.Default.ElectricBolt)
    object Privacy : TarunScreen("privacy", "Privacy & Data", Icons.Default.Security)
    object AppSettings : TarunScreen("app_settings", "Assistant Settings", Icons.Default.Settings)
    object DeveloperMode : TarunScreen("developer_mode", "Developer Mode", Icons.Default.BugReport)
    object BuildExport : TarunScreen("build_export", "Build & APK Center", Icons.Default.VerifiedUser)
    object About : TarunScreen("about", "About Tarun", Icons.Default.Info)
}

val DrawerScreens = listOf(
    TarunScreen.MainAi,
    TarunScreen.NotificationCenter,
    TarunScreen.DeviceControl,
    TarunScreen.History,
    TarunScreen.Skills,
    TarunScreen.Memory,
    TarunScreen.Automation,
    TarunScreen.VoiceSettings,
    TarunScreen.GeminiSettings,
    TarunScreen.AppSettings,
    TarunScreen.PermissionCenter,
    TarunScreen.BuildExport,
    TarunScreen.Privacy,
    TarunScreen.DeveloperMode,
    TarunScreen.About
)
