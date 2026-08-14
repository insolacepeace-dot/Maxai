package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val sender: String,
    val title: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isWhatsApp: Boolean = false,
    val notificationKey: String = ""
)
