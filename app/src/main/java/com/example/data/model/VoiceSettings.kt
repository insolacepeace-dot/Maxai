package com.example.data.model

enum class AiMode(val displayName: String, val description: String) {
    AUTO("Auto", "Use Gemini Cloud when available; automatically fallback to Free Local Mode"),
    GEMINI("Gemini", "Use Google Gemini AI Cloud model"),
    LOCAL_FREE("Local Free Mode", "Offline rule engine (No API Key required)")
}

data class VoiceSettings(
    val pitch: Float = 1.0f,
    val speed: Float = 1.0f,
    val selectedVoiceName: String = "",
    val languageMode: LanguageMode = LanguageMode.AUTO,
    val wakePhrase: String = "Tarun",
    val wakeWordEnabled: Boolean = false
)

data class AppSettings(
    val bossTitle: String = "Boss",
    val aiMode: AiMode = AiMode.AUTO,
    val geminiEnabled: Boolean = true,
    val geminiApiKey: String = "",
    val geminiModel: String = "gemini-2.5-flash",
    val geminiTemperature: Float = 0.7f,
    val geminiMaxTokens: Int = 512,
    val geminiTimeoutSeconds: Int = 30,
    val autoConfirmReplies: Boolean = false,
    val askBeforeSending: Boolean = true,
    val conversationMemoryEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val proactiveSuggestions: Boolean = false,
    val autoReadNotifications: Boolean = false,
    val onboarded: Boolean = false
)

