package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppSettings
import com.example.data.model.LanguageMode
import com.example.data.model.VoiceSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TarunPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tarun_preferences", Context.MODE_PRIVATE)

    private val _appSettings = MutableStateFlow(loadAppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private val _voiceSettings = MutableStateFlow(loadVoiceSettings())
    val voiceSettings: StateFlow<VoiceSettings> = _voiceSettings.asStateFlow()

    private fun loadAppSettings(): AppSettings {
        val aiModeOrdinal = prefs.getInt("ai_mode", com.example.data.model.AiMode.AUTO.ordinal)
        val mode = com.example.data.model.AiMode.entries.getOrElse(aiModeOrdinal) { com.example.data.model.AiMode.AUTO }
        return AppSettings(
            bossTitle = prefs.getString("boss_title", "Boss") ?: "Boss",
            aiMode = mode,
            geminiEnabled = prefs.getBoolean("gemini_enabled", true),
            geminiApiKey = prefs.getString("gemini_api_key", "") ?: "",
            geminiModel = prefs.getString("gemini_model", "gemini-2.5-flash") ?: "gemini-2.5-flash",
            geminiTemperature = prefs.getFloat("gemini_temp", 0.7f),
            geminiMaxTokens = prefs.getInt("gemini_max_tokens", 512),
            geminiTimeoutSeconds = prefs.getInt("gemini_timeout", 30),
            openAiEnabled = prefs.getBoolean("openai_enabled", true),
            openAiApiKey = prefs.getString("openai_api_key", "") ?: "",
            openAiModel = prefs.getString("openai_model", "gpt-4o-mini") ?: "gpt-4o-mini",
            openAiTemperature = prefs.getFloat("openai_temp", 0.7f),
            openAiMaxTokens = prefs.getInt("openai_max_tokens", 512),
            openAiTimeoutSeconds = prefs.getInt("openai_timeout", 30),
            jarvisModeEnabled = prefs.getBoolean("jarvis_mode_enabled", true),
            backgroundAssistantEnabled = prefs.getBoolean("bg_assistant_enabled", true),
            voiceAssistantEnabled = prefs.getBoolean("voice_assistant_enabled", true),
            spokenNotificationsEnabled = prefs.getBoolean("spoken_notifs_enabled", true),
            proactiveAssistantEnabled = prefs.getBoolean("proactive_assistant_enabled", true),
            callAssistantEnabled = prefs.getBoolean("call_assistant_enabled", true),
            accessibilityAutomationEnabled = prefs.getBoolean("accessibility_automation_enabled", true),
            whatsAppAgentEnabled = prefs.getBoolean("whatsapp_agent_enabled", true),
            whatsAppAutoReply = prefs.getBoolean("whatsapp_auto_reply", false),
            whatsAppVoiceReply = prefs.getBoolean("whatsapp_voice_reply", true),
            whatsAppAiReplies = prefs.getBoolean("whatsapp_ai_replies", true),
            autoConfirmReplies = prefs.getBoolean("auto_confirm_replies", false),
            askBeforeSending = prefs.getBoolean("ask_before_sending", true),
            preferredMediaApp = prefs.getString("preferred_media_app", "Spotify") ?: "Spotify",
            quietHoursEnabled = prefs.getBoolean("quiet_hours_enabled", false),
            quietHoursStart = prefs.getInt("quiet_hours_start", 22),
            quietHoursEnd = prefs.getInt("quiet_hours_end", 7),
            conversationMemoryEnabled = prefs.getBoolean("memory_enabled", true),
            hapticFeedbackEnabled = prefs.getBoolean("haptic_enabled", true),
            proactiveSuggestions = prefs.getBoolean("proactive_suggestions", true),
            autoReadNotifications = prefs.getBoolean("auto_read_notifications", false),
            onboarded = prefs.getBoolean("is_onboarded", false)
        )
    }

    private fun loadVoiceSettings(): VoiceSettings {
        val langModeOrdinal = prefs.getInt("lang_mode", LanguageMode.AUTO.ordinal)
        val mode = LanguageMode.entries.getOrElse(langModeOrdinal) { LanguageMode.AUTO }
        return VoiceSettings(
            pitch = prefs.getFloat("voice_pitch", 1.0f),
            speed = prefs.getFloat("voice_speed", 1.0f),
            selectedVoiceName = prefs.getString("voice_name", "") ?: "",
            languageMode = mode,
            wakePhrase = prefs.getString("wake_phrase", "Tarun") ?: "Tarun",
            wakeWordEnabled = prefs.getBoolean("wake_enabled", false)
        )
    }

    fun updateAppSettings(settings: AppSettings) {
        prefs.edit()
            .putString("boss_title", settings.bossTitle)
            .putInt("ai_mode", settings.aiMode.ordinal)
            .putBoolean("gemini_enabled", settings.geminiEnabled)
            .putString("gemini_api_key", settings.geminiApiKey)
            .putString("gemini_model", settings.geminiModel)
            .putFloat("gemini_temp", settings.geminiTemperature)
            .putInt("gemini_max_tokens", settings.geminiMaxTokens)
            .putInt("gemini_timeout", settings.geminiTimeoutSeconds)
            .putBoolean("openai_enabled", settings.openAiEnabled)
            .putString("openai_api_key", settings.openAiApiKey)
            .putString("openai_model", settings.openAiModel)
            .putFloat("openai_temp", settings.openAiTemperature)
            .putInt("openai_max_tokens", settings.openAiMaxTokens)
            .putInt("openai_timeout", settings.openAiTimeoutSeconds)
            .putBoolean("jarvis_mode_enabled", settings.jarvisModeEnabled)
            .putBoolean("bg_assistant_enabled", settings.backgroundAssistantEnabled)
            .putBoolean("voice_assistant_enabled", settings.voiceAssistantEnabled)
            .putBoolean("spoken_notifs_enabled", settings.spokenNotificationsEnabled)
            .putBoolean("proactive_assistant_enabled", settings.proactiveAssistantEnabled)
            .putBoolean("call_assistant_enabled", settings.callAssistantEnabled)
            .putBoolean("accessibility_automation_enabled", settings.accessibilityAutomationEnabled)
            .putBoolean("whatsapp_agent_enabled", settings.whatsAppAgentEnabled)
            .putBoolean("whatsapp_auto_reply", settings.whatsAppAutoReply)
            .putBoolean("whatsapp_voice_reply", settings.whatsAppVoiceReply)
            .putBoolean("whatsapp_ai_replies", settings.whatsAppAiReplies)
            .putBoolean("auto_confirm_replies", settings.autoConfirmReplies)
            .putBoolean("ask_before_sending", settings.askBeforeSending)
            .putString("preferred_media_app", settings.preferredMediaApp)
            .putBoolean("quiet_hours_enabled", settings.quietHoursEnabled)
            .putInt("quiet_hours_start", settings.quietHoursStart)
            .putInt("quiet_hours_end", settings.quietHoursEnd)
            .putBoolean("memory_enabled", settings.conversationMemoryEnabled)
            .putBoolean("haptic_enabled", settings.hapticFeedbackEnabled)
            .putBoolean("proactive_suggestions", settings.proactiveSuggestions)
            .putBoolean("auto_read_notifications", settings.autoReadNotifications)
            .putBoolean("is_onboarded", settings.onboarded)
            .apply()
        _appSettings.value = settings
    }

    fun updateVoiceSettings(settings: VoiceSettings) {
        prefs.edit()
            .putFloat("voice_pitch", settings.pitch)
            .putFloat("voice_speed", settings.speed)
            .putString("voice_name", settings.selectedVoiceName)
            .putInt("lang_mode", settings.languageMode.ordinal)
            .putString("wake_phrase", settings.wakePhrase)
            .putBoolean("wake_enabled", settings.wakeWordEnabled)
            .apply()
        _voiceSettings.value = settings
    }

    fun setOnboarded(onboarded: Boolean) {
        updateAppSettings(_appSettings.value.copy(onboarded = onboarded))
    }
}
