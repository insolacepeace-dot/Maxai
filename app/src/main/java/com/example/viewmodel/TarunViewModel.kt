package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AIContext
import com.example.ai.AIProviderManager
import com.example.ai.GeminiProvider
import com.example.data.local.AlarmEntity
import com.example.data.local.AutomationEntity
import com.example.data.local.MemoryFactEntity
import com.example.data.local.NotificationEventEntity
import com.example.data.local.TarunDatabase
import com.example.data.local.TarunPreferences
import com.example.data.local.WhatsAppContactEntity
import com.example.data.local.WhatsAppMessageEntity
import com.example.data.model.AppSettings
import com.example.data.model.AssistantState
import com.example.data.model.CommandType
import com.example.data.model.DeviceCommand
import com.example.data.model.TarunMessage
import com.example.data.model.VoiceSettings
import com.example.data.repository.TarunRepository
import com.example.engine.ActionValidator
import com.example.engine.AlarmScheduler
import com.example.engine.AudioTrack
import com.example.engine.CommandExecutor
import com.example.engine.PermissionManager
import com.example.engine.PermissionStatus
import com.example.engine.PlaybackState
import com.example.engine.ScreenAnalysisHelper
import com.example.engine.TarunMediaPlayer
import com.example.engine.VoiceSystem
import com.example.service.TarunNotificationListenerService
import com.example.service.TarunVoiceService
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
    private val alarmScheduler = AlarmScheduler(context)
    val mediaPlayer = TarunMediaPlayer.getInstance(context)

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

    private val _availableGeminiModels = MutableStateFlow<List<String>>(
        listOf("gemini-2.5-flash", "gemini-2.0-flash", "gemini-1.5-flash", "gemini-1.5-pro", "gemini-2.0-flash-lite")
    )
    val availableGeminiModels: StateFlow<List<String>> = _availableGeminiModels.asStateFlow()

    private val _permissionStatuses = MutableStateFlow(permissionManager.getAllPermissionStatuses())
    val permissionStatuses: StateFlow<List<PermissionStatus>> = _permissionStatuses.asStateFlow()

    val conversationHistory: StateFlow<List<TarunMessage>> = repository.conversationHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentWhatsAppNotifications: StateFlow<List<NotificationEventEntity>> = repository.recentWhatsAppNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationEventEntity>> = repository.recentNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memoryFacts: StateFlow<List<MemoryFactEntity>> = repository.memoryFacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automations: StateFlow<List<AutomationEntity>> = repository.automations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alarms: StateFlow<List<AlarmEntity>> = repository.alarms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val whatsAppContacts: StateFlow<List<WhatsAppContactEntity>> = repository.whatsAppContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val whatsAppMessages: StateFlow<List<WhatsAppMessageEntity>> = repository.whatsAppMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingWhatsAppApprovals: StateFlow<List<WhatsAppMessageEntity>> = repository.pendingWhatsAppApprovals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Media player state
    val mediaPlaybackState: StateFlow<PlaybackState> = mediaPlayer.playbackState
    val currentTrack: StateFlow<AudioTrack?> = mediaPlayer.currentTrack
    val playlist: StateFlow<List<AudioTrack>> = mediaPlayer.playlist

    // Screen analysis
    private val _screenAnalysisResult = MutableStateFlow<String?>(null)
    val screenAnalysisResult: StateFlow<String?> = _screenAnalysisResult.asStateFlow()
    private val _isAnalyzingScreen = MutableStateFlow(false)
    val isAnalyzingScreen: StateFlow<Boolean> = _isAnalyzingScreen.asStateFlow()

    // Background Service state
    private val _isBackgroundServiceActive = MutableStateFlow(false)
    val isBackgroundServiceActive: StateFlow<Boolean> = _isBackgroundServiceActive.asStateFlow()

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

        // Observe WhatsApp notification updates for auto-announcements
        viewModelScope.launch {
            TarunNotificationListenerService.latestWhatsAppNotification.collect { notif ->
                if (notif != null) {
                    // Record in WhatsApp Agent
                    handleIncomingWhatsAppFromNotification(notif)
                    if (_assistantState.value == AssistantState.IDLE) {
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

        // Fetch dynamic Gemini models in background
        refreshAvailableGeminiModels()
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

            // Check if there is an active pending confirmation
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

    // Alarms Management
    fun addAlarm(hour: Int, minute: Int, label: String, daysOfWeek: String = "DAILY", isVibrate: Boolean = true) {
        viewModelScope.launch {
            val alarm = AlarmEntity(
                hour = hour,
                minute = minute,
                label = label.ifBlank { "Tarun Alarm" },
                daysOfWeek = daysOfWeek,
                isVibrate = isVibrate,
                isEnabled = true
            )
            val id = repository.saveAlarm(alarm)
            val savedAlarm = alarm.copy(id = id)
            alarmScheduler.scheduleAlarm(savedAlarm)
            val msg = "${appSettings.value.bossTitle}, $hour:${String.format("%02d", minute)} ka alarm set kar diya hai."
            _lastAssistantReply.value = msg
            voiceSystem.speak(msg, "hi")
        }
    }

    fun toggleAlarm(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAlarm(id, enabled)
            val alarm = repository.getAlarmById(id)
            if (alarm != null) {
                if (enabled) {
                    alarmScheduler.scheduleAlarm(alarm.copy(isEnabled = true))
                } else {
                    alarmScheduler.cancelAlarm(id)
                }
            }
        }
    }

    fun deleteAlarm(id: Long) {
        viewModelScope.launch {
            alarmScheduler.cancelAlarm(id)
            repository.deleteAlarm(id)
        }
    }

    fun clearAllAlarms() {
        viewModelScope.launch {
            val list = alarms.value
            list.forEach { alarmScheduler.cancelAlarm(it.id) }
            repository.clearAllAlarms()
        }
    }

    // Media Player Controls
    fun playTrack(track: AudioTrack) {
        mediaPlayer.playTrack(track)
    }

    fun playPauseMusic() {
        mediaPlayer.playPause()
    }

    fun playNextMusic() {
        mediaPlayer.playNext()
    }

    fun playPrevMusic() {
        mediaPlayer.playPrevious()
    }

    fun stopMusic() {
        mediaPlayer.stop()
    }

    // WhatsApp Agent Controls
    private suspend fun handleIncomingWhatsAppFromNotification(notif: NotificationEventEntity) {
        val proposedReplyPrompt = "A WhatsApp message arrived from ${notif.sender}: '${notif.text}'. As ${appSettings.value.bossTitle}'s personal AI assistant, compose a helpful, polite, and brief suggested WhatsApp reply (1 sentence)."
        val aiResult = aiProviderManager.processQuery(proposedReplyPrompt, AIContext(bossTitle = appSettings.value.bossTitle))
        val proposed = aiResult.spokenText.removeSurrounding("\"")

        repository.saveWhatsAppMessage(
            WhatsAppMessageEntity(
                contactName = notif.sender,
                phoneNumber = "",
                incomingText = notif.text,
                proposedAiReply = proposed,
                status = "PENDING_APPROVAL"
            )
        )
    }

    fun approveAndSendWhatsApp(message: WhatsAppMessageEntity) {
        viewModelScope.launch {
            val replyToSend = if (message.finalSentReply.isNotBlank()) message.finalSentReply else message.proposedAiReply
            // Use notification listener inline reply if available
            val sent = TarunNotificationListenerService.replyToLastWhatsApp(context, replyToSend)
            repository.updateWhatsAppMessageStatus(message.id, if (sent) "SENT" else "APPROVED", replyToSend)
            val msg = "${appSettings.value.bossTitle}, WhatsApp reply send kar diya hai: $replyToSend"
            _lastAssistantReply.value = msg
            voiceSystem.speak(msg, "hi")
        }
    }

    fun rejectWhatsAppMessage(id: Long) {
        viewModelScope.launch {
            repository.updateWhatsAppMessageStatus(id, "REJECTED", "")
        }
    }

    fun editAndSendWhatsApp(id: Long, newReply: String) {
        viewModelScope.launch {
            val sent = TarunNotificationListenerService.replyToLastWhatsApp(context, newReply)
            repository.updateWhatsAppMessageStatus(id, if (sent) "SENT" else "APPROVED", newReply)
        }
    }

    fun addWhatsAppContact(name: String, phone: String, notes: String, autoReply: Boolean) {
        viewModelScope.launch {
            repository.saveWhatsAppContact(
                WhatsAppContactEntity(
                    name = name,
                    phoneNumber = phone,
                    notes = notes,
                    autoReplyEnabled = autoReply
                )
            )
        }
    }

    fun deleteWhatsAppContact(id: Long) {
        viewModelScope.launch {
            repository.deleteWhatsAppContact(id)
        }
    }

    fun testSimulateWhatsAppMessage(sender: String, messageText: String) {
        viewModelScope.launch {
            val prompt = "Incoming WhatsApp from $sender: '$messageText'. Write a professional 1-sentence draft reply for ${appSettings.value.bossTitle}."
            val aiResult = aiProviderManager.processQuery(prompt, AIContext(bossTitle = appSettings.value.bossTitle))
            repository.saveWhatsAppMessage(
                WhatsAppMessageEntity(
                    contactName = sender,
                    phoneNumber = "+91 98765 43210",
                    incomingText = messageText,
                    proposedAiReply = aiResult.spokenText.removeSurrounding("\""),
                    status = "PENDING_APPROVAL"
                )
            )
        }
    }

    // Screen & Chat Analysis
    fun analyzeCurrentScreen() {
        _isAnalyzingScreen.value = true
        viewModelScope.launch {
            val result = ScreenAnalysisHelper.analyzeCurrentScreenText(aiProviderManager, appSettings.value.bossTitle)
            _screenAnalysisResult.value = result
            _isAnalyzingScreen.value = false
            _lastAssistantReply.value = result
            voiceSystem.speak(result, "hi")
        }
    }

    fun clearScreenAnalysis() {
        _screenAnalysisResult.value = null
    }

    fun analyzePastedChat(chatText: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = ScreenAnalysisHelper.summarizeChatText(chatText, aiProviderManager, appSettings.value.bossTitle)
            onResult(result)
        }
    }

    // Background Voice Service
    fun startBackgroundAssistant() {
        if (!permissionManager.isAudioPermissionGranted()) return
        val serviceIntent = Intent(context, TarunVoiceService::class.java).apply {
            action = TarunVoiceService.ACTION_START_LISTENING
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
        _isBackgroundServiceActive.value = true
    }

    fun stopBackgroundAssistant() {
        val serviceIntent = Intent(context, TarunVoiceService::class.java).apply {
            action = TarunVoiceService.ACTION_STOP
        }
        context.startService(serviceIntent)
        _isBackgroundServiceActive.value = false
    }

    // Dynamic Gemini Model Discovery
    fun refreshAvailableGeminiModels() {
        viewModelScope.launch {
            val provider = GeminiProvider(
                customApiKey = appSettings.value.geminiApiKey.ifBlank { null },
                modelName = appSettings.value.geminiModel
            )
            val models = provider.listAvailableModels()
            _availableGeminiModels.value = models
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
                AutomationEntity(
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

    fun testAutomation(automation: AutomationEntity) {
        viewModelScope.launch {
            when (automation.actionType) {
                "SPEAK_TEXT" -> {
                    _lastAssistantReply.value = automation.actionPayload
                    voiceSystem.speak(automation.actionPayload, "hi")
                }
                "OPEN_APP" -> {
                    commandExecutor.execute(
                        DeviceCommand(
                            type = CommandType.OPEN_APP,
                            rawQuery = "Open ${automation.actionPayload}",
                            targetApp = automation.actionPayload,
                            confirmationRequired = false
                        ),
                        bossTitle = appSettings.value.bossTitle
                    )
                }
                else -> {
                    _lastAssistantReply.value = automation.actionPayload
                    voiceSystem.speak(automation.actionPayload, "hi")
                }
            }
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

    fun saveGeminiApiKey(apiKey: String) {
        val updated = appSettings.value.copy(geminiApiKey = apiKey.trim())
        updateAppSettings(updated)
        refreshAvailableGeminiModels()
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
            val provider = GeminiProvider(
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
            // Auto update model if fallback was selected
            if (result.success && provider.getActiveModel() != modelToUse) {
                val updated = appSettings.value.copy(geminiModel = provider.getActiveModel())
                updateAppSettings(updated)
            }
        }
    }

    fun executeLocalTest(commandText: String) {
        viewModelScope.launch {
            val localProvider = com.example.ai.LocalProvider()
            val result = localProvider.processQuery(
                commandText,
                AIContext(bossTitle = appSettings.value.bossTitle)
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
        mediaPlayer.stop()
    }
}
