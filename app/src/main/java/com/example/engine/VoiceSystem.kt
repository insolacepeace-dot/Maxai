package com.example.engine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.model.AssistantState
import com.example.data.model.LanguageMode
import com.example.data.model.VoiceSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceSystem(
    private val context: Context,
    private val onSpeechResult: (String) -> Unit,
    private val onStateChanged: (AssistantState) -> Unit
) : RecognitionListener, TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _audioAmplitude = MutableStateFlow(0f)
    val audioAmplitude: StateFlow<Float> = _audioAmplitude.asStateFlow()

    private val _availableVoices = MutableStateFlow<List<String>>(emptyList())
    val availableVoices: StateFlow<List<String>> = _availableVoices.asStateFlow()

    private var voiceSettings = VoiceSettings()

    init {
        tts = TextToSpeech(context.applicationContext, this)
        initRecognizer()
    }

    private fun initRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(this@VoiceSystem)
            }
        }
    }

    fun updateVoiceSettings(settings: VoiceSettings) {
        this.voiceSettings = settings
        tts?.setPitch(settings.pitch)
        tts?.setSpeechRate(settings.speed)
        applyVoice(settings.selectedVoiceName)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            tts?.setLanguage(Locale("hi", "IN"))
            tts?.setPitch(voiceSettings.pitch)
            tts?.setSpeechRate(voiceSettings.speed)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    onStateChanged(AssistantState.SPEAKING)
                }

                override fun onDone(utteranceId: String?) {
                    onStateChanged(AssistantState.IDLE)
                    _audioAmplitude.value = 0f
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    onStateChanged(AssistantState.IDLE)
                    _audioAmplitude.value = 0f
                }
            })

            // Populate system TTS voices
            try {
                val voices = tts?.voices?.map { it.name } ?: emptyList()
                _availableVoices.value = voices
                applyVoice(voiceSettings.selectedVoiceName)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    private fun applyVoice(voiceName: String) {
        if (!isTtsInitialized || voiceName.isBlank()) return
        try {
            val matchedVoice = tts?.voices?.find { it.name == voiceName }
            if (matchedVoice != null) {
                tts?.voice = matchedVoice
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun startListening() {
        stopSpeaking()
        initRecognizer()

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

            val langCode = when (voiceSettings.languageMode) {
                LanguageMode.HINDI -> "hi-IN"
                LanguageMode.GUJARATI -> "gu-IN"
                LanguageMode.ENGLISH -> "en-IN"
                LanguageMode.HINGLISH -> "hi-IN"
                LanguageMode.AUTO -> "hi-IN"
            }
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, langCode)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langCode)
            putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("en-IN", "gu-IN", "hi-IN"))
        }

        try {
            speechRecognizer?.startListening(intent)
            onStateChanged(AssistantState.LISTENING)
        } catch (e: Exception) {
            onStateChanged(AssistantState.IDLE)
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            // Ignore
        }
        _audioAmplitude.value = 0f
    }

    fun speak(text: String, languageCode: String = "hi") {
        if (!isTtsInitialized || text.isBlank()) return

        stopSpeaking()

        val targetLocale = when (languageCode.lowercase()) {
            "gu" -> Locale("gu", "IN")
            "hi" -> Locale("hi", "IN")
            "en" -> Locale("en", "IN")
            else -> Locale("hi", "IN")
        }

        val result = tts?.setLanguage(targetLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale("en", "IN"))
        }

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "TARUN_TALK_${System.currentTimeMillis()}")
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "TARUN_TALK_${System.currentTimeMillis()}")
        onStateChanged(AssistantState.SPEAKING)
    }

    fun stopSpeaking() {
        if (tts?.isSpeaking == true) {
            tts?.stop()
        }
        _audioAmplitude.value = 0f
    }

    fun destroy() {
        stopSpeaking()
        stopListening()
        speechRecognizer?.destroy()
        tts?.shutdown()
    }

    // --- Speech Recognition Callbacks ---

    override fun onReadyForSpeech(params: Bundle?) {
        onStateChanged(AssistantState.LISTENING)
    }

    override fun onBeginningOfSpeech() {
        onStateChanged(AssistantState.LISTENING)
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Normalize RMS (-2 to 10 dB) to 0.0 - 1.0
        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.05f, 1f)
        _audioAmplitude.value = normalized
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        onStateChanged(AssistantState.THINKING)
        _audioAmplitude.value = 0f
    }

    override fun onError(error: Int) {
        _audioAmplitude.value = 0f
        onStateChanged(AssistantState.IDLE)
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val spoken = matches?.firstOrNull()
        if (!spoken.isNullOrBlank()) {
            onSpeechResult(spoken)
        } else {
            onStateChanged(AssistantState.IDLE)
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val partial = matches?.firstOrNull()
        if (!partial.isNullOrBlank()) {
            // Can be used for live transcription
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}
}
