package com.example.data.model

enum class AiMode(val displayName: String, val description: String) {
    AUTO("Auto Router", "Use Gemini Cloud when available -> fallback to OpenAI -> fallback to Local Free Mode"),
    GEMINI("Google Gemini", "Use Google Gemini AI Cloud model"),
    OPENAI("OpenAI GPT", "Use OpenAI GPT-4o / GPT-4o-mini Cloud model"),
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
    // Gemini Settings
    val geminiEnabled: Boolean = true,
    val geminiApiKey: String = "",
    val geminiModel: String = "gemini-2.5-flash",
    val geminiTemperature: Float = 0.7f,
    val geminiMaxTokens: Int = 512,
    val geminiTimeoutSeconds: Int = 30,
    // OpenAI Settings
    val openAiEnabled: Boolean = true,
    val openAiApiKey: String = "",
    val openAiModel: String = "gpt-4o-mini",
    val openAiTemperature: Float = 0.7f,
    val openAiMaxTokens: Int = 512,
    val openAiTimeoutSeconds: Int = 30,
    // JARVIS & Background Mode
    val jarvisModeEnabled: Boolean = true,
    val backgroundAssistantEnabled: Boolean = true,
    val voiceAssistantEnabled: Boolean = true,
    val spokenNotificationsEnabled: Boolean = true,
    val proactiveAssistantEnabled: Boolean = true,
    val callAssistantEnabled: Boolean = true,
    val accessibilityAutomationEnabled: Boolean = true,
    // WhatsApp Agent
    val whatsAppAgentEnabled: Boolean = true,
    val whatsAppAutoReply: Boolean = false,
    val whatsAppVoiceReply: Boolean = true,
    val whatsAppAiReplies: Boolean = true,
    val autoConfirmReplies: Boolean = false,
    val askBeforeSending: Boolean = true,
    // Media & System
    val preferredMediaApp: String = "Spotify",
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: Int = 22,
    val quietHoursEnd: Int = 7,
    // General
    val conversationMemoryEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val proactiveSuggestions: Boolean = true,
    val autoReadNotifications: Boolean = false,
    val onboarded: Boolean = false
)


