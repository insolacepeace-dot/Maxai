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
}
