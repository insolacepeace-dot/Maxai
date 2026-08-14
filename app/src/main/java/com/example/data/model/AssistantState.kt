package com.example.data.model

enum class AssistantState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING
}

enum class LanguageMode(val displayName: String, val code: String) {
    AUTO("Auto-Detect (Hindi / Gujarati / English)", "auto"),
    HINDI("Hindi (हिन्दी)", "hi"),
    GUJARATI("Gujarati (ગુજરાતી)", "gu"),
    ENGLISH("English (Indian)", "en"),
    HINGLISH("Hinglish", "hi-IN")
}

data class TarunMessage(
    val id: Long = 0,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "auto",
    val actionExecuted: String? = null
)
