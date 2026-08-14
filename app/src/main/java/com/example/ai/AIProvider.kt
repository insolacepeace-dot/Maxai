package com.example.ai

import com.example.data.model.DeviceCommand

data class AIContext(
    val bossTitle: String = "Boss",
    val recentNotifications: List<String> = emptyList(),
    val conversationHistory: List<Pair<String, String>> = emptyList() // role to message
)

data class AIResult(
    val spokenText: String,
    val detectedLanguage: String,
    val command: DeviceCommand? = null,
    val isFromGemini: Boolean = false
)

data class ConnectionTestResult(
    val success: Boolean,
    val statusText: String,
    val statusCode: Int = 0,
    val isInvalidKey: Boolean = false,
    val isNetworkUnavailable: Boolean = false,
    val isModelUnavailable: Boolean = false,
    val responseText: String? = null
)

interface AIProvider {
    val name: String
    suspend fun processQuery(query: String, context: AIContext): AIResult
    suspend fun testConnection(): Boolean
    suspend fun testConnectionDetailed(): ConnectionTestResult = ConnectionTestResult(
        success = testConnection(),
        statusText = if (testConnection()) "Connected successfully" else "Connection failed"
    )
}

