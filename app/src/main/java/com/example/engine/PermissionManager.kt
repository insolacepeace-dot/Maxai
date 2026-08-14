package com.example.engine

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.service.TarunAccessibilityService

data class PermissionStatus(
    val title: String,
    val description: String,
    val isGranted: Boolean,
    val isSystemSpecial: Boolean = false,
    val permissionKey: String
)

class PermissionManager(private val context: Context) {

    fun isAudioPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isNotificationPostGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isNotificationAccessGranted(): Boolean {
        val packageName = context.packageName
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        ) ?: ""
        return flat.contains(packageName)
    }

    fun isAccessibilityServiceEnabled(): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            ?: return false
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        val expectedServiceName = "${context.packageName}/${TarunAccessibilityService::class.java.name}"
        return enabledServices.any { service ->
            val serviceInfo = service.resolveInfo.serviceInfo
            val fullPath = "${serviceInfo.packageName}/${serviceInfo.name}"
            fullPath.equals(expectedServiceName, ignoreCase = true) || serviceInfo.packageName == context.packageName
        }
    }

    fun isContactsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isBluetoothPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getAllPermissionStatuses(): List<PermissionStatus> {
        return listOf(
            PermissionStatus(
                title = "Microphone Access",
                description = "Required for Tarun's voice-first interaction and speech recognition.",
                isGranted = isAudioPermissionGranted(),
                permissionKey = Manifest.permission.RECORD_AUDIO
            ),
            PermissionStatus(
                title = "Notification Listener",
                description = "Enables Tarun to announce incoming WhatsApp & system messages hands-free.",
                isGranted = isNotificationAccessGranted(),
                isSystemSpecial = true,
                permissionKey = "notification_listener"
            ),
            PermissionStatus(
                title = "Accessibility Service",
                description = "Allows Tarun to perform Back, Home, Recents, and safe voice automation.",
                isGranted = isAccessibilityServiceEnabled(),
                isSystemSpecial = true,
                permissionKey = "accessibility"
            ),
            PermissionStatus(
                title = "Post Notifications",
                description = "Required to display status and foreground assistant controls.",
                isGranted = isNotificationPostGranted(),
                permissionKey = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else "post_notifications"
            ),
            PermissionStatus(
                title = "Contacts (Optional)",
                description = "Allows calling and messaging contacts by name (e.g., 'Papa ko call karo').",
                isGranted = isContactsPermissionGranted(),
                permissionKey = Manifest.permission.READ_CONTACTS
            ),
            PermissionStatus(
                title = "Bluetooth (Optional)",
                description = "Allows quick device connection and hands-free bluetooth management.",
                isGranted = isBluetoothPermissionGranted(),
                permissionKey = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_CONNECT else "bluetooth"
            ),
            PermissionStatus(
                title = "Camera (Optional)",
                description = "Allows controlling the flashlight / torch and launching the camera.",
                isGranted = isCameraPermissionGranted(),
                permissionKey = Manifest.permission.CAMERA
            )
        )
    }

    fun openNotificationListenerSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppSettings()
        }
    }

    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppSettings()
        }
    }

    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun openWifiSettings() {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppSettings()
        }
    }

    fun openBluetoothSettings() {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppSettings()
        }
    }

    fun openSystemSettings() {
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppSettings()
        }
    }
}
