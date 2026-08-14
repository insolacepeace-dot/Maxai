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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class OpenAIProvider(
    private val customApiKey: String? = null,
    private var modelName: String = "gpt-4o-mini",
    private val temperature: Float = 0.7f,
    private val maxTokens: Int = 512,
    private val timeoutSeconds: Int = 30
) : AIProvider {

    override val name: String get() = "OpenAI ($modelName)"

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
                // Check if build config or env contains OPENAI_API_KEY
                ""
            } catch (e: Exception) {
                ""
            }
        }
    }

    fun getActiveModel(): String = modelName

    fun setModel(newModel: String) {
        modelName = newModel
    }

    override suspend fun processQuery(query: String, context: AIContext): AIResult = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()
        if (apiKey.isBlank()) {
            val boss = context.bossTitle
            return@withContext AIResult(
                spokenText = "$boss, OpenAI API key configure nahi hai. Settings me ja kar apni OpenAI API key daaliye.",
                detectedLanguage = "hi",
                isFromOpenAi = false,
                providerUsed = "Local Fallback"
            )
        }

        val promptJson = buildChatCompletionPayload(query, context, stream = false)
        val requestBody = promptJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            val code = response.code
            val bodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = parseErrorMessage(code, bodyString)
                return@withContext AIResult(
                    spokenText = "${context.bossTitle}, OpenAI Error: $errorMsg",
                    detectedLanguage = "hi",
                    isFromOpenAi = false,
                    providerUsed = "OpenAI Error"
                )
            }

            val jsonResponse = JSONObject(bodyString)
            val choices = jsonResponse.optJSONArray("choices")
            if (choices == null || choices.length() == 0) {
                return@withContext AIResult(
                    spokenText = "${context.bossTitle}, OpenAI se koi response nahi aaya.",
                    detectedLanguage = "hi",
                    isFromOpenAi = true,
                    providerUsed = "OpenAI ($modelName)"
                )
            }

            val messageObj = choices.getJSONObject(0).optJSONObject("message")
            val rawContent = messageObj?.optString("content", "")?.trim() ?: ""

            parseStructuredResponse(rawContent, query, context.bossTitle)
        } catch (e: Exception) {
            val boss = context.bossTitle
            AIResult(
                spokenText = "$boss, OpenAI se connect karne mein problem aayi: ${e.localizedMessage ?: "Network error"}",
                detectedLanguage = "hi",
                isFromOpenAi = false,
                providerUsed = "OpenAI Error"
            )
        }
    }

    override suspend fun streamQuery(
        query: String,
        context: AIContext,
        onChunk: (String) -> Unit
    ): AIResult = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()
        if (apiKey.isBlank()) {
            val res = processQuery(query, context)
            onChunk(res.spokenText)
            return@withContext res
        }

        val promptJson = buildChatCompletionPayload(query, context, stream = true)
        val requestBody = promptJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val body = response.body?.string() ?: ""
                val err = parseErrorMessage(response.code, body)
                val fullText = "${context.bossTitle}, OpenAI Error: $err"
                onChunk(fullText)
                return@withContext AIResult(spokenText = fullText, detectedLanguage = "hi", isFromOpenAi = false)
            }

            val source = response.body?.byteStream()
            if (source == null) {
                val fullText = "${context.bossTitle}, response stream uplabdh nahi hai."
                onChunk(fullText)
                return@withContext AIResult(spokenText = fullText, detectedLanguage = "hi")
            }

            val reader = BufferedReader(InputStreamReader(source))
            val fullContent = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val currentLine = line?.trim() ?: continue
                if (currentLine.startsWith("data: ")) {
                    val data = currentLine.removePrefix("data: ").trim()
                    if (data == "[DONE]") break
                    try {
                        val json = JSONObject(data)
                        val delta = json.optJSONArray("choices")?.optJSONObject(0)?.optJSONObject("delta")
                        val content = delta?.optString("content", "") ?: ""
                        if (content.isNotEmpty()) {
                            fullContent.append(content)
                            onChunk(fullContent.toString())
                        }
                    } catch (ignore: Exception) {}
                }
            }

            val rawResult = fullContent.toString().trim()
            parseStructuredResponse(rawResult, query, context.bossTitle)
        } catch (e: Exception) {
            val res = processQuery(query, context)
            onChunk(res.spokenText)
            res
        }
    }

    override suspend fun testConnection(): Boolean {
        return testConnectionDetailed().success
    }

    override suspend fun testConnectionDetailed(): ConnectionTestResult = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()
        if (apiKey.isBlank()) {
            return@withContext ConnectionTestResult(
                success = false,
                statusText = "OpenAI API Key is empty. Please enter your API key in Settings.",
                isInvalidKey = true
            )
        }

        val startTime = System.currentTimeMillis()
        try {
            val payload = JSONObject().apply {
                put("model", modelName)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", "Respond with exact words: 'TARUN OPENAI ONLINE'")
                    })
                })
                put("max_tokens", 16)
            }

            val body = payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val latency = System.currentTimeMillis() - startTime
            val code = response.code
            val respBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val json = JSONObject(respBody)
                val reply = json.optJSONArray("choices")
                    ?.optJSONObject(0)
                    ?.optJSONObject("message")
                    ?.optString("content", "")?.trim() ?: "OK"

                ConnectionTestResult(
                    success = true,
                    statusText = "Connected to OpenAI ($modelName) successfully! [Latency: ${latency}ms]",
                    statusCode = code,
                    responseText = reply,
                    latencyMs = latency
                )
            } else {
                val errorMsg = parseErrorMessage(code, respBody)
                ConnectionTestResult(
                    success = false,
                    statusText = "OpenAI Error ($code): $errorMsg",
                    statusCode = code,
                    isInvalidKey = code == 401 || code == 403,
                    isRateLimited = code == 429,
                    isModelUnavailable = code == 404,
                    latencyMs = latency
                )
            }
        } catch (e: Exception) {
            val latency = System.currentTimeMillis() - startTime
            ConnectionTestResult(
                success = false,
                statusText = "Connection error: ${e.localizedMessage ?: "Timeout or network failure"}",
                isNetworkUnavailable = true,
                latencyMs = latency
            )
        }
    }

    private fun buildChatCompletionPayload(query: String, context: AIContext, stream: Boolean): JSONObject {
        val messagesArray = JSONArray()

        val systemPrompt = """
You are TARUN AI, an elite, hyper-intelligent JARVIS-style executive personal voice assistant.
Always address the user with supreme loyalty and respect as "${context.bossTitle}".
Support Hindi, Hinglish, Gujarati, and English seamlessly.

If the user request contains a command to control their Android device, reply in JSON format with two keys:
1. "reply": Your conversational response to speak aloud to ${context.bossTitle}.
2. "action": The device action object (optional, null if conversational only):
   {
     "type": "OPEN_APP" | "PLAY_MUSIC" | "PAUSE_MUSIC" | "NEXT_TRACK" | "PREV_TRACK" | "SET_ALARM" | "SET_TIMER" | "MAKE_CALL" | "DRAFT_WHATSAPP" | "REPLY_NOTIFICATION" | "TOGGLE_TORCH" | "VOLUME_UP" | "VOLUME_DOWN" | "NAVIGATE_BACK" | "NAVIGATE_HOME" | "OPEN_CAMERA" | "GET_WEATHER" | "CONVERSATION_ONLY",
     "targetApp": string (e.g. "WhatsApp", "YouTube", "Spotify", "Camera"),
     "contactName": string,
     "messageBody": string,
     "hour": int (0-23),
     "minute": int (0-59)
   }

If the user is having a normal conversation, provide an insightful, concise, and helpful response.
""".trimIndent()

        messagesArray.put(JSONObject().apply {
            put("role", "system")
            put("content", systemPrompt)
        })

        // Conversation history
        for ((role, text) in context.conversationHistory.takeLast(6)) {
            messagesArray.put(JSONObject().apply {
                put("role", if (role == "user") "user" else "assistant")
                put("content", text)
            })
        }

        // Current query
        messagesArray.put(JSONObject().apply {
            put("role", "user")
            put("content", query)
        })

        return JSONObject().apply {
            put("model", modelName)
            put("messages", messagesArray)
            put("temperature", temperature.toDouble())
            put("max_tokens", maxTokens)
            put("stream", stream)
        }
    }

    private fun parseStructuredResponse(rawContent: String, originalQuery: String, bossTitle: String): AIResult {
        try {
            val trimmed = rawContent.trim()
            val jsonText = when {
                trimmed.startsWith("```json") -> trimmed.substringAfter("```json").substringBeforeLast("```").trim()
                trimmed.startsWith("```") -> trimmed.substringAfter("```").substringBeforeLast("```").trim()
                trimmed.startsWith("{") && trimmed.endsWith("}") -> trimmed
                else -> null
            }

            if (jsonText != null) {
                val json = JSONObject(jsonText)
                val reply = json.optString("reply", "")
                val actionObj = json.optJSONObject("action")
                var deviceCommand: DeviceCommand? = null

                if (actionObj != null) {
                    val typeStr = actionObj.optString("type", "CONVERSATION_ONLY")
                    val cmdType = try {
                        CommandType.valueOf(typeStr)
                    } catch (e: Exception) {
                        CommandType.CONVERSATION_ONLY
                    }

                    deviceCommand = DeviceCommand(
                        type = cmdType,
                        rawQuery = originalQuery,
                        targetApp = actionObj.optString("targetApp").ifBlank { null },
                        contactName = actionObj.optString("contactName").ifBlank { null },
                        messageBody = actionObj.optString("messageBody").ifBlank { null },
                        hour = if (actionObj.has("hour")) actionObj.getInt("hour") else null,
                        minute = if (actionObj.has("minute")) actionObj.getInt("minute") else null,
                        conversationalReply = reply
                    )
                }

                return AIResult(
                    spokenText = if (reply.isNotBlank()) reply else rawContent,
                    detectedLanguage = detectLanguage(if (reply.isNotBlank()) reply else rawContent),
                    command = deviceCommand,
                    isFromOpenAi = true,
                    providerUsed = "OpenAI ($modelName)"
                )
            }
        } catch (ignore: Exception) {}

        return AIResult(
            spokenText = rawContent,
            detectedLanguage = detectLanguage(rawContent),
            command = null,
            isFromOpenAi = true,
            providerUsed = "OpenAI ($modelName)"
        )
    }

    private fun parseErrorMessage(code: Int, body: String): String {
        try {
            val json = JSONObject(body)
            val err = json.optJSONObject("error")
            val msg = err?.optString("message")
            if (!msg.isNullOrBlank()) return msg
        } catch (ignore: Exception) {}

        return when (code) {
            401 -> "Invalid API Key. Please check your OpenAI key."
            429 -> "Rate limit or quota exceeded on OpenAI account."
            404 -> "Model '$modelName' not found or unsupported."
            500, 503 -> "OpenAI servers are currently experiencing issues."
            else -> "HTTP $code"
        }
    }

    private fun detectLanguage(text: String): String {
        for (char in text) {
            val code = char.code
            if (code in 0x0900..0x097F) return "hi"
            if (code in 0x0A80..0x0AFF) return "gu"
        }
        return "en"
    }
}
