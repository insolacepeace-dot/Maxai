package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AIContext
import com.example.ai.AIProviderManager
import com.example.data.local.NotificationEventEntity
import com.example.data.local.TarunDatabase
import com.example.data.local.TarunPreferences
import com.example.data.model.AppSettings
import com.example.data.model.AssistantState
import com.example.data.model.CommandType
import com.example.data.model.DeviceCommand
import com.example.data.model.TarunMessage
import com.example.data.model.VoiceSettings
import com.example.data.repository.TarunRepository
import com.example.engine.ActionValidator
import com.example.engine.CommandExecutor
import com.example.engine.PermissionManager
import com.example.engine.PermissionStatus
import com.example.engine.VoiceSystem
import com.example.service.TarunNotificationListenerService
import com.example.ui.components.HapticFeedbackHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TarunViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val database = TarunDatabase.getInstance(context)
    private val preferences = TarunPreferences(context)
    val repository = TarunRepository(database.tarunDao(), preferences)
    val permissionManager = PermissionManager(context)
    val actionValidator = ActionValidator(permissionManager)
    private val commandExecutor = CommandExecutor(context, repository, permissionManager)

    val appSettings: StateFlow<AppSettings> = repository.appSettings
    val voiceSettings: StateFlow<VoiceSettings> = repository.voiceSettings

    private val aiProviderManager = AIProviderManager(appSettings.value)

    private val _assistantState = MutableStateFlow(AssistantState.IDLE)
    val assistantState: StateFlow<AssistantState> = _assistantState.asStateFlow()

    private val _liveTranscription = MutableStateFlow("")
    val liveTranscription: StateFlow<String> = _liveTranscription.asStateFlow()

    private val _lastAssistantReply = MutableStateFlow("Namaste Boss, how can I assist you today?")
    val lastAssistantReply: StateFlow<String> = _lastAssistantReply.asStateFlow()

    private val _pendingConfirmationCommand = MutableStateFlow<DeviceCommand?>(null)
    val pendingConfirmationCommand: StateFlow<DeviceCommand?> = _pendingConfirmationCommand.asStateFlow()

    private val _geminiTestStatus = MutableStateFlow<String?>(null)
    val geminiTestStatus: StateFlow<String?> = _geminiTestStatus.asStateFlow()

    private val _geminiDetailedStatus = MutableStateFlow<com.example.ai.ConnectionTestResult?>(null)
    val geminiDetailedStatus: StateFlow<com.example.ai.ConnectionTestResult?> = _geminiDetailedStatus.asStateFlow()

    private val _isTestingGemini = MutableStateFlow(false)
    val isTestingGemini: StateFlow<Boolean> = _isTestingGemini.asStateFlow()

    private val _permissionStatuses = MutableStateFlow(permissionManager.getAllPermissionStatuses())
    val permissionStatuses: StateFlow<List<PermissionStatus>> = _permissionStatuses.asStateFlow()

    val conversationHistory: StateFlow<List<TarunMessage>> = repository.conversationHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentWhatsAppNotifications: StateFlow<List<NotificationEventEntity>> = repository.recentWhatsAppNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationEventEntity>> = repository.recentNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memoryFacts: StateFlow<List<com.example.data.local.MemoryFactEntity>> = repository.memoryFacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automations: StateFlow<List<com.example.data.local.AutomationEntity>> = repository.automations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Developer Mode diagnostics
    private val _lastStructuredIntent = MutableStateFlow<String>("{\"intent\": \"STANDBY\", \"status\": \"READY\"}")
    val lastStructuredIntent: StateFlow<String> = _lastStructuredIntent.asStateFlow()

    private val _lastExecutionResult = MutableStateFlow<String>("IDLE")
    val lastExecutionResult: StateFlow<String> = _lastExecutionResult.asStateFlow()

    private val _lastCommandName = MutableStateFlow<String>("None")
    val lastCommandName: StateFlow<String> = _lastCommandName.asStateFlow()

    private val voiceSystem = VoiceSystem(
        context = context,
        onSpeechResult = { text -> handleUserInput(text) },
        onStateChanged = { state -> _assistantState.value = state }
    )

    val audioAmplitude: StateFlow<Float> = voiceSystem.audioAmplitude
    val availableVoices: StateFlow<List<String>> = voiceSystem.availableVoices

    init {
        // Apply initial voice settings
        voiceSystem.updateVoiceSettings(voiceSettings.value)

        // Observe WhatsApp notification updates for auto-announcements if in foreground
        viewModelScope.launch {
            TarunNotificationListenerService.latestWhatsAppNotification.collect { notif ->
                if (notif != null && _assistantState.value == AssistantState.IDLE) {
                    val announcement = "${appSettings.value.bossTitle}, WhatsApp par ${notif.sender} ka message aaya hai."
                    _lastAssistantReply.value = announcement
                    repository.saveMessage(
                        TarunMessage(
                            text = announcement,
                            isUser = false,
                            language = "hi",
                            actionExecuted = "WhatsApp Notification"
                        )
                    )
                }
            }
        }
    }

    fun refreshPermissions() {
        _permissionStatuses.value = permissionManager.getAllPermissionStatuses()
    }

    fun startListening() {
        if (!permissionManager.isAudioPermissionGranted()) {
            _lastAssistantReply.value = "${appSettings.value.bossTitle}, Microphone permission zaroori hai voice sunne ke liye."
            return
        }
        HapticFeedbackHelper.performActivePulseHaptic(context, appSettings.value.hapticFeedbackEnabled)
        _liveTranscription.value = ""
        voiceSystem.startListening()
    }

    fun stopListening() {
        voiceSystem.stopListening()
    }

    fun stopSpeaking() {
        voiceSystem.stopSpeaking()
        _assistantState.value = AssistantState.IDLE
    }

    fun handleUserInput(rawQuery: String) {
        val query = rawQuery.trim()
        if (query.isBlank()) {
            _assistantState.value = AssistantState.IDLE
            return
        }

        _liveTranscription.value = query
        _assistantState.value = AssistantState.THINKING
        HapticFeedbackHelper.performClickHaptic(context, appSettings.value.hapticFeedbackEnabled)

        viewModelScope.launch {
            // Save user query to conversation history
            val userMsg = TarunMessage(text = query, isUser = true)
            repository.saveMessage(userMsg)

            // Check if there is an active pending confirmation (e.g. "Haan / Bhej do / Cancel")
            val pending = _pendingConfirmationCommand.value
            if (pending != null) {
                handleConfirmationResponse(query, pending)
                return@launch
            }

            // Process query through AIProviderManager
            val recentTurns = conversationHistory.value.takeLast(6).map {
                (if (it.isUser) "user" else "model") to it.text
            }
            val aiContext = AIContext(
                bossTitle = appSettings.value.bossTitle,
                conversationHistory = recentTurns
            )

            val aiResult = aiProviderManager.processQuery(query, aiContext)
            val command = aiResult.command

            if (command != null && command.type != CommandType.CONVERSATION_ONLY) {
                // Command requires validation & execution
                val validation = actionValidator.validate(command)
                when (validation) {
                    is ActionValidator.ValidationResult.Allowed -> {
                        if (command.confirmationRequired && appSettings.value.askBeforeSending) {
                            _pendingConfirmationCommand.value = command
                            _lastAssistantReply.value = aiResult.spokenText
                            voiceSystem.speak(aiResult.spokenText, aiResult.detectedLanguage)
                            repository.saveMessage(
                                TarunMessage(
                                    text = aiResult.spokenText,
                                    isUser = false,
                                    language = aiResult.detectedLanguage,
                                    actionExecuted = command.type.name
                                )
                            )
                        } else {
                            executeAndSpeak(command, aiResult.detectedLanguage, aiResult.spokenText)
                        }
                    }

                    is ActionValidator.ValidationResult.PermissionRequired -> {
                        val msg = "${appSettings.value.bossTitle}, ${validation.permissionName} enabled nahi hai. Main settings khol raha hoon."
                        _lastAssistantReply.value = msg
                        voiceSystem.speak(msg, aiResult.detectedLanguage)
                        validation.openAction.invoke()
                        repository.saveMessage(
                            TarunMessage(
                                text = msg,
                                isUser = false,
                                language = aiResult.detectedLanguage,
                                actionExecuted = "PERMISSION_REQUIRED_${validation.permissionName}"
                            )
                        )
                    }

                    is ActionValidator.ValidationResult.Denied -> {
                        val msg = "${appSettings.value.bossTitle}, Android is action ko directly allow nahi karta."
                        _lastAssistantReply.value = msg
                        voiceSystem.speak(msg, aiResult.detectedLanguage)
                        repository.saveMessage(
                            TarunMessage(
                                text = msg,
                                isUser = false,
                                language = aiResult.detectedLanguage,
                                actionExecuted = "DENIED"
                            )
                        )
                    }
                }
            } else {
                // Pure conversational response
                _lastAssistantReply.value = aiResult.spokenText
                voiceSystem.speak(aiResult.spokenText, aiResult.detectedLanguage)
                repository.saveMessage(
                    TarunMessage(
                        text = aiResult.spokenText,
                        isUser = false,
                        language = aiResult.detectedLanguage,
                        actionExecuted = "CONVERSATION"
                    )
                )
            }
        }
    }

    private suspend fun handleConfirmationResponse(query: String, pending: DeviceCommand) {
        val lower = query.lowercase()
        val isConfirmed = lower.contains("haan") || lower.contains("ha") || lower.contains("yes") ||
                lower.contains("bhej") || lower.contains("send") || lower.contains("karo") || lower.contains("mokli")

        _pendingConfirmationCommand.value = null

        if (isConfirmed) {
            executeAndSpeak(pending, "hi", "")
        } else {
            val cancelMsg = "${appSettings.value.bossTitle}, action cancel kar diya hai."
            _lastAssistantReply.value = cancelMsg
            voiceSystem.speak(cancelMsg, "hi")
            repository.saveMessage(
                TarunMessage(
                    text = cancelMsg,
                    isUser = false,
                    language = "hi",
                    actionExecuted = "CANCELLED"
                )
            )
        }
    }

    private suspend fun executeAndSpeak(command: DeviceCommand, lang: String, preSpoken: String) {
        _lastCommandName.value = command.type.name
        val target = command.targetApp ?: command.contactName ?: ""
        val msg = command.messageBody ?: command.replyText ?: ""
        _lastStructuredIntent.value = "{\n  \"intent\": \"${command.type.name}\",\n  \"target\": \"$target\",\n  \"message\": \"$msg\",\n  \"confirmation\": ${command.confirmationRequired}\n}"
        val result = commandExecutor.execute(command, appSettings.value.bossTitle)
        _lastExecutionResult.value = if (result.success) "SUCCESS: ${result.message}" else "FAILED/RESTRICTED: ${result.message}"
        val finalSpeech = if (result.message.isNotBlank()) result.message else preSpoken
        _lastAssistantReply.value = finalSpeech
        voiceSystem.speak(finalSpeech, lang)
        repository.saveMessage(
            TarunMessage(
                text = finalSpeech,
                isUser = false,
                language = lang,
                actionExecuted = command.type.name
            )
        )
    }

    fun triggerQuickCommand(commandType: CommandType, label: String) {
        val cmd = DeviceCommand(type = commandType, rawQuery = label)
        viewModelScope.launch {
            executeAndSpeak(cmd, "hi", label)
        }
    }

    fun speakText(text: String, lang: String = "hi") {
        _lastAssistantReply.value = text
        voiceSystem.speak(text, lang)
    }

    fun updateAppSettings(settings: AppSettings) {
        repository.updateAppSettings(settings)
        aiProviderManager.updateSettings(settings)
    }

    fun updateVoiceSettings(settings: VoiceSettings) {
        repository.updateVoiceSettings(settings)
        voiceSystem.updateVoiceSettings(settings)
    }

    fun completeOnboarding() {
        repository.setOnboarded(true)
    }

    fun clearConversationHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun deleteMessage(id: Long) {
        viewModelScope.launch {
            repository.deleteMessage(id)
        }
    }

    // Notification Center Actions
    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearNotifications()
        }
    }

    // Memory Actions
    fun addMemoryFact(key: String, value: String, category: String = "general") {
        viewModelScope.launch {
            repository.saveMemoryFact(key, value, category)
        }
    }

    fun deleteMemoryFact(id: Long) {
        viewModelScope.launch {
            repository.deleteMemoryFact(id)
        }
    }

    fun clearAllMemoryFacts() {
        viewModelScope.launch {
            repository.clearAllMemoryFacts()
        }
    }

    // Automation Actions
    fun addAutomation(
        name: String,
        triggerType: String,
        triggerCondition: String,
        actionType: String,
        actionPayload: String
    ) {
        viewModelScope.launch {
            repository.saveAutomation(
                com.example.data.local.AutomationEntity(
                    name = name,
                    triggerType = triggerType,
                    triggerCondition = triggerCondition,
                    actionType = actionType,
                    actionPayload = actionPayload,
                    isEnabled = true
                )
            )
        }
    }

    fun toggleAutomation(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAutomation(id, enabled)
        }
    }

    fun deleteAutomation(id: Long) {
        viewModelScope.launch {
            repository.deleteAutomation(id)
        }
    }

    fun testAutomation(automation: com.example.data.local.AutomationEntity) {
        viewModelScope.launch {
            when (automation.actionType) {
                "SPEAK_TEXT" -> {
                    speakText(automation.actionPayload)
                }
                "TOGGLE_TORCH" -> {
                    triggerQuickCommand(CommandType.TOGGLE_TORCH, "Torch toggle")
                }
                "OPEN_APP" -> {
                    val cmd = DeviceCommand(
                        type = CommandType.OPEN_APP,
                        rawQuery = "open ${automation.actionPayload}",
                        targetApp = automation.actionPayload
                    )
                    executeAndSpeak(cmd, "hi", "Opening app")
                }
                else -> {
                    speakText("Testing automation: ${automation.name}")
                }
            }
        }
    }

    fun saveGeminiApiKey(apiKey: String) {
        val updated = appSettings.value.copy(geminiApiKey = apiKey.trim())
        updateAppSettings(updated)
    }

    fun clearGeminiApiKey() {
        val updated = appSettings.value.copy(geminiApiKey = "")
        updateAppSettings(updated)
        _geminiDetailedStatus.value = null
        _geminiTestStatus.value = null
    }

    fun testGeminiApi() {
        testGeminiApiDetailed()
    }

    fun testGeminiApiDetailed(overrideKey: String? = null, overrideModel: String? = null) {
        _isTestingGemini.value = true
        _geminiTestStatus.value = "Connecting to Gemini Cloud..."
        viewModelScope.launch {
            val keyToUse = overrideKey ?: appSettings.value.geminiApiKey
            val modelToUse = overrideModel ?: appSettings.value.geminiModel
            val provider = com.example.ai.GeminiProvider(
                customApiKey = keyToUse.ifBlank { null },
                modelName = modelToUse,
                temperature = appSettings.value.geminiTemperature,
                maxTokens = appSettings.value.geminiMaxTokens,
                timeoutSeconds = appSettings.value.geminiTimeoutSeconds
            )
            val result = provider.testConnectionDetailed()
            _geminiDetailedStatus.value = result
            _geminiTestStatus.value = result.statusText
            _isTestingGemini.value = false
        }
    }

    fun executeLocalTest(commandText: String) {
        viewModelScope.launch {
            val localProvider = com.example.ai.LocalProvider()
            val result = localProvider.processQuery(
                commandText,
                com.example.ai.AIContext(bossTitle = appSettings.value.bossTitle)
            )
            val cmd = result.command
            if (cmd != null && cmd.type != CommandType.CONVERSATION_ONLY) {
                executeAndSpeak(cmd, result.detectedLanguage, result.spokenText)
            } else {
                _lastAssistantReply.value = result.spokenText
                voiceSystem.speak(result.spokenText, result.detectedLanguage)
            }
        }
    }

    fun isSetupReady(): Boolean {
        return permissionManager.isAudioPermissionGranted()
    }

    fun getMissingSetupRequirements(): List<String> {
        val missing = mutableListOf<String>()
        if (!permissionManager.isAudioPermissionGranted()) {
            missing.add("Microphone (Voice Recognition)")
        }
        if (!permissionManager.isNotificationAccessGranted()) {
            missing.add("Notification Listener (WhatsApp Assistant)")
        }
        if (!permissionManager.isAccessibilityServiceEnabled()) {
            missing.add("Accessibility Service (Device Navigation)")
        }
        return missing
    }

    override fun onCleared() {
        super.onCleared()
        voiceSystem.destroy()
    }
}
