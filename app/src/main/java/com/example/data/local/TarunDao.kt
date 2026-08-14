package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TarunDao {
    @Query("SELECT * FROM conversations ORDER BY timestamp ASC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentConversations(limit: Int = 50): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE text LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchConversations(query: String): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: Long)

    @Query("DELETE FROM conversations")
    suspend fun clearAllConversations()

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentNotifications(limit: Int = 30): Flow<List<NotificationEventEntity>>

    @Query("SELECT * FROM notifications WHERE isWhatsApp = 1 ORDER BY timestamp DESC LIMIT 10")
    fun getRecentWhatsAppNotifications(): Flow<List<NotificationEventEntity>>

    @Query("SELECT * FROM notifications WHERE isWhatsApp = 1 ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastWhatsAppNotification(): NotificationEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEventEntity): Long

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)

    // Memory Facts
    @Query("SELECT * FROM memory_facts ORDER BY timestamp DESC")
    fun getAllMemoryFacts(): Flow<List<MemoryFactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemoryFact(fact: MemoryFactEntity): Long

    @Query("DELETE FROM memory_facts WHERE id = :id")
    suspend fun deleteMemoryFact(id: Long)

    @Query("DELETE FROM memory_facts")
    suspend fun clearAllMemoryFacts()

    // Automations
    @Query("SELECT * FROM automations")
    fun getAllAutomations(): Flow<List<AutomationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutomation(automation: AutomationEntity): Long

    @Query("UPDATE automations SET isEnabled = :enabled WHERE id = :id")
    suspend fun toggleAutomation(id: Long, enabled: Boolean)

    @Query("DELETE FROM automations WHERE id = :id")
    suspend fun deleteAutomation(id: Long)

    // Alarms
    @Query("SELECT * FROM alarms ORDER BY hour ASC, minute ASC")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE isEnabled = 1 ORDER BY hour ASC, minute ASC")
    suspend fun getEnabledAlarms(): List<AlarmEntity>

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getAlarmById(id: Long): AlarmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Query("UPDATE alarms SET isEnabled = :enabled WHERE id = :id")
    suspend fun toggleAlarm(id: Long, enabled: Boolean)

    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteAlarm(id: Long)

    @Query("DELETE FROM alarms")
    suspend fun clearAllAlarms()

    // WhatsApp Business / Agent
    @Query("SELECT * FROM whatsapp_contacts ORDER BY lastMessageTime DESC")
    fun getAllWhatsAppContacts(): Flow<List<WhatsAppContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWhatsAppContact(contact: WhatsAppContactEntity): Long

    @Query("DELETE FROM whatsapp_contacts WHERE id = :id")
    suspend fun deleteWhatsAppContact(id: Long)

    @Query("SELECT * FROM whatsapp_messages ORDER BY timestamp DESC")
    fun getAllWhatsAppMessages(): Flow<List<WhatsAppMessageEntity>>

    @Query("SELECT * FROM whatsapp_messages WHERE status = 'PENDING_APPROVAL' ORDER BY timestamp DESC")
    fun getPendingApprovalMessages(): Flow<List<WhatsAppMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWhatsAppMessage(message: WhatsAppMessageEntity): Long

    @Query("UPDATE whatsapp_messages SET status = :status, finalSentReply = :finalReply WHERE id = :id")
    suspend fun updateWhatsAppMessageStatus(id: Long, status: String, finalReply: String)

    @Query("DELETE FROM whatsapp_messages WHERE id = :id")
    suspend fun deleteWhatsAppMessage(id: Long)

    @Query("DELETE FROM whatsapp_messages")
    suspend fun clearAllWhatsAppMessages()

    // JARVIS Action History
    @Query("SELECT * FROM jarvis_actions ORDER BY timestamp DESC")
    fun getAllJarvisActions(): Flow<List<JarvisActionEntity>>

    @Query("SELECT * FROM jarvis_actions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentJarvisActions(limit: Int = 50): Flow<List<JarvisActionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJarvisAction(action: JarvisActionEntity): Long

    @Query("UPDATE jarvis_actions SET status = :status, executionDetails = :details WHERE id = :id")
    suspend fun updateJarvisActionStatus(id: Long, status: String, details: String)

    @Query("DELETE FROM jarvis_actions WHERE id = :id")
    suspend fun deleteJarvisAction(id: Long)

    @Query("DELETE FROM jarvis_actions")
    suspend fun clearAllJarvisActions()
}

