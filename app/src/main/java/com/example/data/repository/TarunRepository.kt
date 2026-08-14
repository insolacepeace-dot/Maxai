package com.example.data.repository

import com.example.data.local.ConversationEntity
import com.example.data.local.NotificationEventEntity
import com.example.data.local.TarunDao
import com.example.data.local.TarunPreferences
import com.example.data.model.AppSettings
import com.example.data.model.TarunMessage
import com.example.data.model.VoiceSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class TarunRepository(
    private val dao: TarunDao,
    private val preferences: TarunPreferences
) {
    val appSettings: StateFlow<AppSettings> = preferences.appSettings
    val voiceSettings: StateFlow<VoiceSettings> = preferences.voiceSettings

    val conversationHistory: Flow<List<TarunMessage>> = dao.getAllConversations().map { list ->
        list.map {
            TarunMessage(
                id = it.id,
                text = it.text,
                isUser = it.isUser,
                timestamp = it.timestamp,
                language = it.language,
                actionExecuted = it.actionExecuted
            )
        }
    }

    val recentNotifications: Flow<List<NotificationEventEntity>> = dao.getRecentNotifications()
    val recentWhatsAppNotifications: Flow<List<NotificationEventEntity>> = dao.getRecentWhatsAppNotifications()

    suspend fun saveMessage(message: TarunMessage): Long {
        if (!preferences.appSettings.value.conversationMemoryEnabled) return 0L
        return dao.insertConversation(
            ConversationEntity(
                text = message.text,
                isUser = message.isUser,
                timestamp = message.timestamp,
                language = message.language,
                actionExecuted = message.actionExecuted
            )
        )
    }

    suspend fun deleteMessage(id: Long) {
        dao.deleteConversation(id)
    }

    suspend fun clearHistory() {
        dao.clearAllConversations()
    }

    suspend fun saveNotification(notification: NotificationEventEntity): Long {
        return dao.insertNotification(notification)
    }

    suspend fun getLastWhatsAppNotification(): NotificationEventEntity? {
        return dao.getLastWhatsAppNotification()
    }

    suspend fun clearNotifications() {
        dao.clearAllNotifications()
    }

    suspend fun deleteNotification(id: Long) {
        dao.deleteNotification(id)
    }

    // Memory Facts
    val memoryFacts: Flow<List<com.example.data.local.MemoryFactEntity>> = dao.getAllMemoryFacts()

    suspend fun saveMemoryFact(key: String, value: String, category: String = "general") {
        dao.insertMemoryFact(
            com.example.data.local.MemoryFactEntity(
                key = key,
                value = value,
                category = category
            )
        )
    }

    suspend fun deleteMemoryFact(id: Long) {
        dao.deleteMemoryFact(id)
    }

    suspend fun clearAllMemoryFacts() {
        dao.clearAllMemoryFacts()
    }

    // Automations
    val automations: Flow<List<com.example.data.local.AutomationEntity>> = dao.getAllAutomations()

    suspend fun saveAutomation(automation: com.example.data.local.AutomationEntity) {
        dao.insertAutomation(automation)
    }

    suspend fun toggleAutomation(id: Long, enabled: Boolean) {
        dao.toggleAutomation(id, enabled)
    }

    suspend fun deleteAutomation(id: Long) {
        dao.deleteAutomation(id)
    }

    fun updateAppSettings(settings: AppSettings) {
        preferences.updateAppSettings(settings)
    }

    fun updateVoiceSettings(settings: VoiceSettings) {
        preferences.updateVoiceSettings(settings)
    }

    fun setOnboarded(onboarded: Boolean) {
        preferences.setOnboarded(onboarded)
    }
}
