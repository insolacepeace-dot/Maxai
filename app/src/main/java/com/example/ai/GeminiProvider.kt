package com.example.ai

import com.example.BuildConfig
import com.example.data.model.CommandType
import com.example.data.model.DeviceCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class GeminiProvider(
    private val customApiKey: String? = null,
    private val modelName: String = "gemini-2.5-flash",
    private val temperature: Float = 0.7f,
    private val maxTokens: Int = 512,
    private val timeoutSeconds: Int = 30
) : AIProvider {
    override val name: String = "Google Gemini AI ($modelName)"

    private val client = OkHttpClient.Builder()
        .connectTimeout(timeoutSeconds.coerceIn(5, 60).toLong(), TimeUnit.SECONDS)
        .readTimeout(timeoutSeconds.coerceIn(5, 60).toLong(), TimeUnit.SECONDS)
        .writeTimeout(timeoutSeconds.coerceIn(5, 60).toLong(), TimeUnit.SECONDS)
        .build()

    fun getEffectiveApiKey(): String {
        return if (!customApiKey.isNullOrBlank()) {
            customApiKey.trim()
        } else {
            try {
                BuildConfig.GEMINI_API_KEY.trim()
            } catch (e: Exception) {
                ""
            }
        }
    }

    override suspend fun testConnection(): Boolean {
        return testConnectionDetailed().success
    }

    override suspend fun testConnectionDetailed(): ConnectionTestResult = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "YOUR_GEMINI_API_KEY") {
            return@withContext ConnectionTestResult(
                success = false,
                statusText = "API key not configured. Please enter a valid Gemini API key.",
                isInvalidKey = true
            )
        }

        val testUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"
        val payload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", "Reply with exactly: TARUN ONLINE") })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("maxOutputTokens", 64)
                put("temperature", 0.1)
            })
        }

        try {
            val request = Request.Builder()
                .url(testUrl)
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val code = response.code
            val bodyString = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonRoot = JSONObject(bodyString)
                val candidates = jsonRoot.optJSONArray("candidates")
                val text = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text") ?: ""

                ConnectionTestResult(
                    success = true,
                    statusText = "✓ GEMINI ONLINE ($modelName)",
                    statusCode = code,
                    responseText = text.trim()
                )
            } else {
                val isKeyIssue = code == 400 || code == 403 || bodyString.contains("API_KEY_INVALID", ignoreCase = true) || bodyString.contains("API key not valid", ignoreCase = true)
                val isModelIssue = code == 404 || bodyString.contains("not found", ignoreCase = true)
                val statusText = when {
                    isKeyIssue -> "✕ Invalid API Key ($code)"
                    isModelIssue -> "✕ Model $modelName not found ($code)"
                    code == 429 -> "⚠ Rate limit exceeded ($code). Try again later."
                    else -> "✕ Connection failed ($code)"
                }
                ConnectionTestResult(
                    success = false,
                    statusText = statusText,
                    statusCode = code,
                    isInvalidKey = isKeyIssue,
                    isModelUnavailable = isModelIssue
                )
            }
        } catch (e: UnknownHostException) {
            ConnectionTestResult(
                success = false,
                statusText = "⚠ Network unavailable. Check internet connection.",
                isNetworkUnavailable = true
            )
        } catch (e: SocketTimeoutException) {
            ConnectionTestResult(
                success = false,
                statusText = "⚠ Connection timed out ($timeoutSeconds s).",
                isNetworkUnavailable = true
            )
        } catch (e: IOException) {
            ConnectionTestResult(
                success = false,
                statusText = "⚠ Network error: ${e.message ?: "Unable to reach Gemini"}",
                isNetworkUnavailable = true
            )
        } catch (e: Exception) {
            ConnectionTestResult(
                success = false,
                statusText = "✕ Test failed: ${e.message ?: "Unknown error"}"
            )
        }
    }

    override suspend fun processQuery(query: String, context: AIContext): AIResult = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "YOUR_GEMINI_API_KEY") {
            throw IllegalStateException("Gemini API key is not configured.")
        }

        val boss = context.bossTitle
        val systemPrompt = """
            You are "Tarun", a futuristic, intelligent, and respectful JARVIS-style personal AI voice assistant.
            You address the user as "$boss".
            You seamlessly support Hindi, Gujarati, English, Hinglish, and Gujlish.
            Detect the user's language automatically and respond naturally in the same language.
            Keep spoken responses crisp, conversational, and direct for voice synthesis (1-2 sentences).
            Never hardcode fake actions; return structured intent JSON for safe device automation.

            Available actions:
            - OPEN_APP (with target_app e.g. WhatsApp, YouTube, Instagram, Maps, Chrome, Camera, Settings, etc.)
            - READ_NOTIFICATION (when user asks about WhatsApp messages or notifications)
            - REPLY_NOTIFICATION (with reply_text)
            - TOGGLE_TORCH
            - VOLUME_UP
            - VOLUME_DOWN
            - SET_ALARM (with alarm_hour, alarm_minute)
            - OPEN_SETTINGS
            - OPEN_WIFI_SETTINGS
            - OPEN_BLUETOOTH_SETTINGS
            - OPEN_CAMERA
            - NAVIGATE_BACK
            - NAVIGATE_HOME
            - OPEN_RECENTS
            - MAKE_CALL (with contact_name)
            - CONVERSATION_ONLY (if purely conversational query)

            Respond ONLY with a JSON object in this exact schema:
            {
              "spoken_text": "The response to be spoken aloud to $boss",
              "detected_language": "hi" | "gu" | "en",
              "action": "ACTION_NAME",
              "target_app": "AppName or null",
              "contact_name": "Name or null",
              "reply_text": "text or null",
              "alarm_hour": 7,
              "alarm_minute": 0
            }
        """.trimIndent()

        val requestUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

        val contentsArray = JSONArray()
        // Include recent conversation turns if any
        context.conversationHistory.takeLast(4).forEach { (role, msg) ->
            contentsArray.put(JSONObject().apply {
                put("role", if (role == "user") "user" else "model")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", msg) })
                })
            })
        }

        // Add current query
        contentsArray.put(JSONObject().apply {
            put("role", "user")
            put("parts", JSONArray().apply {
                put(JSONObject().apply { put("text", query) })
            })
        })

        val requestBody = JSONObject().apply {
            put("contents", contentsArray)
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPrompt) })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", temperature)
                put("maxOutputTokens", maxTokens)
                put("responseMimeType", "application/json")
            })
        }

        val request = Request.Builder()
            .url(requestUrl)
            .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            val code = response.code
            throw Exception("Gemini API returned status $code")
        }

        val responseString = response.body?.string() ?: throw Exception("Empty Gemini response")
        val jsonRoot = JSONObject(responseString)
        val candidates = jsonRoot.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val rawText = parts?.optJSONObject(0)?.optString("text") ?: ""

        val structuredJson = JSONObject(cleanJson(rawText))
        val spokenText = structuredJson.optString("spoken_text", "Yes $boss.")
        val detectedLang = structuredJson.optString("detected_language", "en")
        val actionString = structuredJson.optString("action", "CONVERSATION_ONLY")
        val targetApp = if (structuredJson.has("target_app") && !structuredJson.isNull("target_app")) structuredJson.getString("target_app") else null
        val contactName = if (structuredJson.has("contact_name") && !structuredJson.isNull("contact_name")) structuredJson.getString("contact_name") else null
        val replyText = if (structuredJson.has("reply_text") && !structuredJson.isNull("reply_text")) structuredJson.getString("reply_text") else null
        val hour = if (structuredJson.has("alarm_hour")) structuredJson.optInt("alarm_hour") else null
        val min = if (structuredJson.has("alarm_minute")) structuredJson.optInt("alarm_minute") else null

        val cmdType = try {
            CommandType.valueOf(actionString)
        } catch (e: Exception) {
            CommandType.CONVERSATION_ONLY
        }

        val command = DeviceCommand(
            type = cmdType,
            rawQuery = query,
            targetApp = targetApp,
            contactName = contactName,
            replyText = replyText,
            hour = hour,
            minute = min,
            conversationalReply = spokenText
        )

        return@withContext AIResult(
            spokenText = spokenText,
            detectedLanguage = detectedLang,
            command = command,
            isFromGemini = true
        )
    }

    private fun cleanJson(raw: String): String {
        var trimmed = raw.trim()
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.removePrefix("```json")
        }
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.removePrefix("```")
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.removeSuffix("```")
        }
        return trimmed.trim()
    }
}

