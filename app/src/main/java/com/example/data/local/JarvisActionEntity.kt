package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jarvis_actions")
data class JarvisActionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val trigger: String = "VOICE", // VOICE, WHATSAPP, CALL, SYSTEM, AUTOMATION, MANUAL
    val userCommand: String = "",
    val aiSummary: String = "",
    val actionExecuted: String = "",
    val status: String = "SUCCESS", // REQUESTED, RUNNING, SUCCESS, FAILED
    val executionDetails: String = ""
)
