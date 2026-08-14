package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.data.local.NotificationEventEntity
import com.example.data.local.TarunDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TarunNotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var database: TarunDatabase

    companion object {
        private val _isServiceConnected = MutableStateFlow(false)
        val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

        private val _latestWhatsAppNotification = MutableStateFlow<NotificationEventEntity?>(null)
        val latestWhatsAppNotification: StateFlow<NotificationEventEntity?> = _latestWhatsAppNotification.asStateFlow()

        private val pendingReplyActions = mutableMapOf<String, Notification.Action>()

        fun replyToNotification(key: String, replyMessage: String): Boolean {
            val action = pendingReplyActions[key] ?: return false
            val remoteInputs = action.remoteInputs ?: return false
            val remoteInput = remoteInputs.firstOrNull() ?: return false

            val intent = Intent()
            val bundle = Bundle()
            bundle.putCharSequence(remoteInput.resultKey, replyMessage)
            RemoteInput.addResultsToIntent(remoteInputs, intent, bundle)

            return try {
                action.actionIntent.send(null, 0, intent)
                true
            } catch (e: PendingIntent.CanceledException) {
                false
            }
        }

        fun replyToLastWhatsApp(context: android.content.Context? = null, replyMessage: String): Boolean {
            val key = _latestWhatsAppNotification.value?.notificationKey ?: return false
            return replyToNotification(key, replyMessage)
        }
    }

    override fun onCreate() {
        super.onCreate()
        database = TarunDatabase.getInstance(applicationContext)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        _isServiceConnected.value = true
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        _isServiceConnected.value = false
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val pkg = sbn.packageName ?: return
        val extras = sbn.notification.extras ?: return

        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?: ""

        if (title.isBlank() && text.isBlank()) return

        val isWhatsApp = pkg == "com.whatsapp" || pkg == "com.whatsapp.w4b"
        val appName = try {
            val pm = applicationContext.packageManager
            val ai = pm.getApplicationInfo(pkg, 0)
            pm.getApplicationLabel(ai).toString()
        } catch (e: Exception) {
            pkg
        }

        // Store reply action if present
        sbn.notification.actions?.forEach { action ->
            if (action.remoteInputs != null && action.remoteInputs.isNotEmpty()) {
                pendingReplyActions[sbn.key] = action
            }
        }

        val event = NotificationEventEntity(
            packageName = pkg,
            appName = appName,
            sender = title,
            title = title,
            text = text,
            timestamp = sbn.postTime,
            isWhatsApp = isWhatsApp,
            notificationKey = sbn.key
        )

        if (isWhatsApp) {
            _latestWhatsAppNotification.value = event
        }

        serviceScope.launch {
            try {
                database.tarunDao().insertNotification(event)
            } catch (e: Exception) {
                // Ignore db errors
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        sbn?.key?.let { pendingReplyActions.remove(it) }
    }
}
