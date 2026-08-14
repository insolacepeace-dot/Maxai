package com.example.engine

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TRIGGER_ALARM = "com.example.ACTION_TRIGGER_ALARM"
        const val ACTION_STOP_ALARM = "com.example.ACTION_STOP_ALARM"
        const val ACTION_SNOOZE_ALARM = "com.example.ACTION_SNOOZE_ALARM"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_ALARM_LABEL = "extra_alarm_label"
        const val EXTRA_ALARM_VIBRATE = "extra_alarm_vibrate"
        private const val CHANNEL_ID = "tarun_alarm_channel"
        private const val NOTIFICATION_ID = 2001

        private var activeRingtone: Ringtone? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_TRIGGER_ALARM -> {
                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, 0L)
                val label = intent.getStringExtra(EXTRA_ALARM_LABEL) ?: "TARUN AI Alarm"
                val vibrate = intent.getBooleanExtra(EXTRA_ALARM_VIBRATE, true)

                createNotificationChannel(context)
                playAlarmSound(context)
                if (vibrate) {
                    triggerVibration(context)
                }
                showAlarmNotification(context, alarmId, label)
            }
            ACTION_STOP_ALARM -> {
                stopAlarm(context)
            }
            ACTION_SNOOZE_ALARM -> {
                stopAlarm(context)
                // Schedule 5 minutes snooze
                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, 0L)
                val label = intent.getStringExtra(EXTRA_ALARM_LABEL) ?: "TARUN AI Alarm (Snoozed)"
                val scheduler = AlarmScheduler(context)
                val cal = java.util.Calendar.getInstance()
                cal.add(java.util.Calendar.MINUTE, 5)
                scheduler.scheduleAlarm(
                    com.example.data.local.AlarmEntity(
                        id = alarmId,
                        hour = cal.get(java.util.Calendar.HOUR_OF_DAY),
                        minute = cal.get(java.util.Calendar.MINUTE),
                        label = label,
                        isEnabled = true
                    )
                )
            }
        }
    }

    private fun playAlarmSound(context: Context) {
        try {
            var alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alertUri == null) {
                alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
            activeRingtone = RingtoneManager.getRingtone(context, alertUri)
            activeRingtone?.audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            activeRingtone?.play()
        } catch (e: Exception) {
            // Ignored
        }
    }

    private fun stopAlarm(context: Context) {
        try {
            activeRingtone?.stop()
            activeRingtone = null
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.cancel(NOTIFICATION_ID)
        } catch (e: Exception) {
            // Ignored
        }
    }

    private fun triggerVibration(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 800, 400, 800, 400, 800)
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(2000)
            }
        } catch (e: Exception) {
            // Ignored
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Tarun AI Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm alerts from Tarun AI"
                enableVibration(true)
                enableLights(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun showAlarmNotification(context: Context, alarmId: Long, label: String) {
        val appIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val appPendingIntent = PendingIntent.getActivity(
            context,
            0,
            appIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_STOP_ALARM
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_SNOOZE_ALARM
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_ALARM_LABEL, label)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            snoozeIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("⏰ TARUN AI Alarm")
            .setContentText(label)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(appPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .addAction(android.R.drawable.ic_popup_reminder, "Snooze (5m)", snoozePendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(NOTIFICATION_ID, notification)
    }
}
