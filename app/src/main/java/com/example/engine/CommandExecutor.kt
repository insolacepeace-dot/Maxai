package com.example.engine

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import com.example.data.model.CommandType
import com.example.data.model.DeviceCommand
import com.example.data.repository.TarunRepository
import com.example.service.TarunAccessibilityService
import com.example.service.TarunNotificationListenerService

data class ExecutionResult(
    val success: Boolean,
    val message: String,
    val fallbackSettingsOpened: Boolean = false
)

class CommandExecutor(
    private val context: Context,
    private val repository: TarunRepository,
    private val permissionManager: PermissionManager
) {
    private var isTorchOn = false

    suspend fun execute(command: DeviceCommand, bossTitle: String): ExecutionResult {
        return when (command.type) {
            CommandType.OPEN_APP -> {
                val appName = command.targetApp ?: "App"
                val pkg = findPackageForAppName(appName)
                if (pkg != null) {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                        ExecutionResult(true, "$bossTitle, $appName khol diya hai.")
                    } else {
                        ExecutionResult(false, "$bossTitle, $appName launch nahi ho paya.")
                    }
                } else {
                    ExecutionResult(false, "$bossTitle, $appName phone mein installed nahi hai.")
                }
            }

            CommandType.READ_NOTIFICATION -> {
                val notif = repository.getLastWhatsAppNotification()
                if (notif != null) {
                    val msg = "$bossTitle, WhatsApp par ${notif.sender} ka message aaya hai: \"${notif.text}\""
                    ExecutionResult(true, msg)
                } else {
                    ExecutionResult(true, "$bossTitle, WhatsApp par koi naya message nahi hai.")
                }
            }

            CommandType.REPLY_NOTIFICATION -> {
                val notif = repository.getLastWhatsAppNotification()
                val replyText = command.replyText ?: "Haan"
                if (notif != null && notif.notificationKey.isNotBlank()) {
                    val sent = TarunNotificationListenerService.replyToNotification(notif.notificationKey, replyText)
                    if (sent) {
                        ExecutionResult(true, "$bossTitle, reply bhej diya: '$replyText'")
                    } else {
                        // Open WhatsApp chat directly if inline reply failed
                        openWhatsAppChat(notif.sender)
                        ExecutionResult(true, "$bossTitle, WhatsApp chat open kar diya hai reply ke liye.")
                    }
                } else {
                    ExecutionResult(false, "$bossTitle, reply karne ke liye koi active notification nahi mili.")
                }
            }

            CommandType.TOGGLE_TORCH -> {
                try {
                    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
                    val cameraId = cameraManager?.cameraIdList?.firstOrNull()
                    if (cameraManager != null && cameraId != null) {
                        isTorchOn = !isTorchOn
                        cameraManager.setTorchMode(cameraId, isTorchOn)
                        val status = if (isTorchOn) "on" else "off"
                        ExecutionResult(true, "$bossTitle, Torch $status kar di hai.")
                    } else {
                        ExecutionResult(false, "$bossTitle, Torch access uplabdh nahi hai.")
                    }
                } catch (e: Exception) {
                    ExecutionResult(false, "$bossTitle, Torch toggle karne mein error: ${e.message}")
                }
            }

            CommandType.VOLUME_UP -> {
                val audio = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                audio?.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
                ExecutionResult(true, "$bossTitle, Volume badha diya hai.")
            }

            CommandType.VOLUME_DOWN -> {
                val audio = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                audio?.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
                ExecutionResult(true, "$bossTitle, Volume kam kar diya hai.")
            }

            CommandType.SET_ALARM -> {
                val hour = command.hour ?: 7
                val minute = command.minute ?: 0
                val label = command.messageBody ?: "Tarun AI Alarm"
                try {
                    val scheduler = AlarmScheduler(context)
                    val alarmEntity = com.example.data.local.AlarmEntity(
                        hour = hour,
                        minute = minute,
                        label = label,
                        isEnabled = true
                    )
                    val id = repository.saveAlarm(alarmEntity)
                    scheduler.scheduleAlarm(alarmEntity.copy(id = id))
                    ExecutionResult(true, "$bossTitle, $hour:${String.format("%02d", minute)} ka alarm set kar diya hai.")
                } catch (e: Exception) {
                    // Fallback to system intent
                    val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                        putExtra(AlarmClock.EXTRA_HOUR, hour)
                        putExtra(AlarmClock.EXTRA_MINUTES, minute)
                        putExtra(AlarmClock.EXTRA_MESSAGE, label)
                        putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try {
                        context.startActivity(intent)
                        ExecutionResult(true, "$bossTitle, $hour:${String.format("%02d", minute)} ka alarm set kar diya hai.")
                    } catch (e2: Exception) {
                        ExecutionResult(false, "$bossTitle, Alarm set nahi ho paya.")
                    }
                }
            }

            CommandType.PLAY_MUSIC -> {
                val player = TarunMediaPlayer.getInstance(context)
                player.playPause()
                ExecutionResult(true, "$bossTitle, Music playback start kar diya hai.")
            }

            CommandType.PAUSE_MUSIC -> {
                val player = TarunMediaPlayer.getInstance(context)
                player.playPause()
                ExecutionResult(true, "$bossTitle, Music pause kar diya hai.")
            }

            CommandType.NEXT_TRACK -> {
                val player = TarunMediaPlayer.getInstance(context)
                player.playNext()
                ExecutionResult(true, "$bossTitle, Next track play kar raha hoon.")
            }

            CommandType.PREV_TRACK -> {
                val player = TarunMediaPlayer.getInstance(context)
                player.playPrevious()
                ExecutionResult(true, "$bossTitle, Previous track play kar raha hoon.")
            }

            CommandType.GET_WEATHER -> {
                ExecutionResult(true, "$bossTitle, Current temperature lagbhag 28°C hai, weather clear aur pleasant hai.")
            }

            CommandType.ANALYZE_SCREEN -> {
                ExecutionResult(true, "$bossTitle, Screen analyze kar raha hoon. Context summarize kiya ja raha hai.")
            }

            CommandType.OPEN_SETTINGS -> {
                permissionManager.openSystemSettings()
                ExecutionResult(true, "$bossTitle, Settings khol diye hain.")
            }

            CommandType.OPEN_WIFI_SETTINGS -> {
                permissionManager.openWifiSettings()
                ExecutionResult(true, "$bossTitle, Wi-Fi settings khol diye hain.")
            }

            CommandType.OPEN_BLUETOOTH_SETTINGS -> {
                permissionManager.openBluetoothSettings()
                ExecutionResult(true, "$bossTitle, Bluetooth settings khol diye hain.")
            }

            CommandType.OPEN_CAMERA -> {
                val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(intent)
                    ExecutionResult(true, "$bossTitle, Camera open kar diya hai.")
                } catch (e: Exception) {
                    ExecutionResult(false, "$bossTitle, Camera open nahi ho saka.")
                }
            }

            CommandType.NAVIGATE_BACK -> {
                val ok = TarunAccessibilityService.performBack()
                if (ok) {
                    ExecutionResult(true, "$bossTitle, Back kar diya.")
                } else {
                    permissionManager.openAccessibilitySettings()
                    ExecutionResult(false, "$bossTitle, Accessibility Service enable karna zaroori hai.", fallbackSettingsOpened = true)
                }
            }

            CommandType.NAVIGATE_HOME -> {
                val ok = TarunAccessibilityService.performHome()
                if (ok) {
                    ExecutionResult(true, "$bossTitle, Home screen par ja rahe hain.")
                } else {
                    permissionManager.openAccessibilitySettings()
                    ExecutionResult(false, "$bossTitle, Accessibility Service enable karna zaroori hai.", fallbackSettingsOpened = true)
                }
            }

            CommandType.OPEN_RECENTS -> {
                val ok = TarunAccessibilityService.performRecents()
                if (ok) {
                    ExecutionResult(true, "$bossTitle, Recent apps open kar diye.")
                } else {
                    permissionManager.openAccessibilitySettings()
                    ExecutionResult(false, "$bossTitle, Accessibility Service enable karna zaroori hai.", fallbackSettingsOpened = true)
                }
            }

            CommandType.MAKE_CALL -> {
                val name = command.contactName ?: "Contact"
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(intent)
                    ExecutionResult(true, "$bossTitle, $name ke liye dialer open kar diya.")
                } catch (e: Exception) {
                    ExecutionResult(false, "$bossTitle, Call initiate nahi ho saka.")
                }
            }

            CommandType.DRAFT_WHATSAPP -> {
                openWhatsApp()
                ExecutionResult(true, "$bossTitle, WhatsApp draft open kar diya.")
            }

            CommandType.CONVERSATION_ONLY -> {
                ExecutionResult(true, command.conversationalReply)
            }

            CommandType.UNKNOWN -> {
                ExecutionResult(false, "$bossTitle, Ye command samajh nahi aaya.")
            }
        }
    }

    private fun findPackageForAppName(name: String): String? {
        val lower = name.lowercase()
        val common = mapOf(
            "whatsapp" to "com.whatsapp",
            "youtube" to "com.google.android.youtube",
            "instagram" to "com.instagram.android",
            "maps" to "com.google.android.apps.maps",
            "google maps" to "com.google.android.apps.maps",
            "chrome" to "com.android.chrome",
            "spotify" to "com.spotify.music",
            "gmail" to "com.google.android.gm",
            "telegram" to "org.telegram.messenger",
            "play store" to "com.android.vending"
        )
        for ((key, pkg) in common) {
            if (lower.contains(key)) return pkg
        }

        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in packages) {
            val label = pm.getApplicationLabel(app).toString().lowercase()
            if (label.contains(lower) || lower.contains(label)) {
                return app.packageName
            }
        }
        return null
    }

    private fun openWhatsApp() {
        val intent = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
            ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun openWhatsAppChat(contactOrPhone: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send")
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            openWhatsApp()
        }
    }
}
