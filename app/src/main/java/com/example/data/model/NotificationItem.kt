package com.example.data.model

data class NotificationItem(
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val sender: String,
    val title: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isWhatsApp: Boolean = false,
    val key: String = ""
)
