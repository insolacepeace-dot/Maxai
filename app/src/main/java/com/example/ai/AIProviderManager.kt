package com.example.ai

import com.example.data.model.AiMode
import com.example.data.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AIProviderManager(
    private var appSettings: AppSettings
) {
    private val localProvider = LocalProvider()
    private var geminiProvider: GeminiProvider? = null

    private val _activeProviderName = MutableStateFlow(localProvider.name)
    val activeProviderName: StateFlow<String> = _activeProviderName.asStateFlow()

    private val _isGeminiConfigured = MutableStateFlow(false)
    val isGeminiConfigured: StateFlow<Boolean> = _isGeminiConfigured.asStateFlow()

    init {
        updateSettings(appSettings)
    }

    fun updateSettings(settings: AppSettings) {
        this.appSettings = settings
        val hasKey = settings.geminiApiKey.isNotBlank()
        _isGeminiConfigured.value = hasKey

        if (settings.aiMode != AiMode.LOCAL_FREE && settings.geminiEnabled) {
            geminiProvider = GeminiProvider(
                customApiKey = settings.geminiApiKey.ifBlank { null },
                modelName = settings.geminiModel,
                temperature = settings.geminiTemperature,
                maxTokens = settings.geminiMaxTokens,
                timeoutSeconds = settings.geminiTimeoutSeconds
            )
            _activeProviderName.value = geminiProvider?.name ?: localProvider.name
        } else {
            geminiProvider = null
            _activeProviderName.value = localProvider.name
        }
    }

    suspend fun processQuery(query: String, context: AIContext): AIResult {
        when (appSettings.aiMode) {
            AiMode.LOCAL_FREE -> {
                return localProvider.processQuery(query, context)
            }
            AiMode.GEMINI -> {
                if (geminiProvider != null) {
                    try {
                        return geminiProvider!!.processQuery(query, context)
                    } catch (e: Exception) {
                        val boss = context.bossTitle
                        val errorText = "$boss, Gemini connect nahi ho paya. Please API key check kijiye."
                        return AIResult(
                            spokenText = errorText,
                            detectedLanguage = "hi",
                            isFromGemini = false
                        )
                    }
                } else {
                    val boss = context.bossTitle
                    val errorText = "$boss, Gemini API Key configure nahi hai. Settings me key enter kijiye."
                    return AIResult(
                        spokenText = errorText,
                        detectedLanguage = "hi",
                        isFromGemini = false
                    )
                }
            }
            AiMode.AUTO -> {
                if (geminiProvider != null) {
                    try {
                        return geminiProvider!!.processQuery(query, context)
                    } catch (e: Exception) {
                        // Seamless fallback to Local Free Mode
                        return localProvider.processQuery(query, context)
                    }
                } else {
                    // Gemini key not set, seamlessly use Local Free Mode
                    return localProvider.processQuery(query, context)
                }
            }
        }
    }

    suspend fun testGeminiConnection(): Boolean {
        return geminiProvider?.testConnection() ?: false
    }

    suspend fun testGeminiDetailed(): ConnectionTestResult {
        val provider = geminiProvider ?: GeminiProvider(
            customApiKey = appSettings.geminiApiKey.ifBlank { null },
            modelName = appSettings.geminiModel,
            temperature = appSettings.geminiTemperature,
            maxTokens = appSettings.geminiMaxTokens,
            timeoutSeconds = appSettings.geminiTimeoutSeconds
        )
        return provider.testConnectionDetailed()
    }
}

