package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "whatsapp_contacts")
data class WhatsAppContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val notes: String = "",
    val autoReplyEnabled: Boolean = false,
    val lastMessageTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "whatsapp_messages")
data class WhatsAppMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactName: String,
    val phoneNumber: String,
    val incomingText: String,
    val proposedAiReply: String,
    val finalSentReply: String = "",
    val status: String = "PENDING_APPROVAL", // "PENDING_APPROVAL", "APPROVED", "REJECTED", "SENT"
    val timestamp: Long = System.currentTimeMillis(),
    val isAutoSent: Boolean = false
)
