package com.example.ai

import com.example.data.model.CommandType
import com.example.data.model.DeviceCommand
import java.util.Calendar
import java.util.regex.Pattern

class LocalProvider : AIProvider {
    override val name: String = "Local Rule Engine"

    override suspend fun testConnection(): Boolean = true

    override suspend fun processQuery(query: String, context: AIContext): AIResult {
        val boss = context.bossTitle
        val normalized = query.trim().lowercase()
        val detectedLang = detectLanguage(query)

        // Wake word / Greeting checks
        if (normalized in listOf("tarun", "hey tarun", "hi tarun", "tarun suno", "hello tarun", "namaste", "kem cho", "kem chho")) {
            val reply = when (detectedLang) {
                "gu" -> "હા $boss, બોલો હું શું મદદ કરી શકું?"
                "hi" -> "हाँ $boss, बोलिए। मैं आपकी क्या मदद करूँ?"
                else -> "Yes $boss, how can I assist you today?"
            }
            return AIResult(reply, detectedLang, DeviceCommand(CommandType.CONVERSATION_ONLY, query, conversationalReply = reply))
        }

        // WhatsApp / Notifications
        if (normalized.contains("message") || normalized.contains("whatsapp") || normalized.contains("notif") || normalized.contains("sandesh")) {
            if (normalized.contains("kisi ka") || normalized.contains("aaya hai") || normalized.contains("aavyo") || normalized.contains("check") || normalized.contains("bata") || normalized.contains("read") || normalized.contains("padho") || normalized.contains("kaho")) {
                val reply = when (detectedLang) {
                    "gu" -> "હા $boss, હું નોટિફિકેશન ચેક કરી રહ્યો છું."
                    "hi" -> "हाँ $boss, मैं हाल ही के WhatsApp मैसेजेस चेक कर रहा हूँ।"
                    else -> "Checking your latest WhatsApp notifications, $boss."
                }
                return AIResult(
                    spokenText = reply,
                    detectedLanguage = detectedLang,
                    command = DeviceCommand(CommandType.READ_NOTIFICATION, query, conversationalReply = reply)
                )
            }

            if (normalized.contains("reply") || normalized.contains("jawab") || normalized.contains("uttar")) {
                val replyText = extractReplyText(query)
                val reply = when (detectedLang) {
                    "gu" -> "હા $boss, રિપ્લાય તૈયાર છે: '$replyText'. મોકલી દઉં?"
                    "hi" -> "हाँ $boss, रिप्लाई तैयार है: '$replyText'। भेज दूँ?"
                    else -> "Prepared reply: '$replyText'. Should I send it, $boss?"
                }
                return AIResult(
                    spokenText = reply,
                    detectedLanguage = detectedLang,
                    command = DeviceCommand(
                        CommandType.REPLY_NOTIFICATION,
                        query,
                        replyText = replyText,
                        confirmationRequired = true,
                        conversationalReply = reply
                    )
                )
            }
        }

        // Torch / Flashlight
        if (normalized.contains("torch") || normalized.contains("flashlight") || normalized.contains("light")) {
            val reply = when (detectedLang) {
                "gu" -> "$boss, ટોર્ચ ટોગલ કરી દીધી છે."
                "hi" -> "$boss, टॉर्च टॉगल कर दी है।"
                else -> "Toggling the flashlight for you, $boss."
            }
            return AIResult(reply, detectedLang, DeviceCommand(CommandType.TOGGLE_TORCH, query, conversationalReply = reply))
        }

        // Volume
        if (normalized.contains("volume") || normalized.contains("awaaz") || normalized.contains("awaz") || normalized.contains("sound")) {
            if (normalized.contains("kam") || normalized.contains("ghata") || normalized.contains("low") || normalized.contains("down") || normalized.contains("ochhu") || normalized.contains("dheemi")) {
                val reply = if (detectedLang == "gu") "અવાજ ઓછો કરી દીધો છે." else if (detectedLang == "hi") "वॉल्यूम कम कर दिया है, $boss." else "Decreased volume, $boss."
                return AIResult(reply, detectedLang, DeviceCommand(CommandType.VOLUME_DOWN, query, conversationalReply = reply))
            } else {
                val reply = if (detectedLang == "gu") "અવાજ વધારી દીધો છે." else if (detectedLang == "hi") "वॉल्यूम बढ़ा दिया है, $boss." else "Increased volume, $boss."
                return AIResult(reply, detectedLang, DeviceCommand(CommandType.VOLUME_UP, query, conversationalReply = reply))
            }
        }

        // Camera
        if (normalized.contains("camera") || normalized.contains("photo") || normalized.contains("selfie")) {
            val reply = if (detectedLang == "gu") "$boss, કેમેરો ખોલી રહ્યો છું." else if (detectedLang == "hi") "$boss, कैमरा खोल रहा हूँ।" else "Opening camera, $boss."
            return AIResult(reply, detectedLang, DeviceCommand(CommandType.OPEN_CAMERA, query, conversationalReply = reply))
        }

        // Wi-Fi
        if (normalized.contains("wifi") || normalized.contains("wi-fi")) {
            val reply = if (detectedLang == "gu") "$boss, Wi-Fi સેટિંગ્સ ખોલી રહ્યો છું." else if (detectedLang == "hi") "$boss, Wi-Fi सेटिंग्स खोल रहा हूँ।" else "Opening Wi-Fi settings, $boss."
            return AIResult(reply, detectedLang, DeviceCommand(CommandType.OPEN_WIFI_SETTINGS, query, conversationalReply = reply))
        }

        // Bluetooth
        if (normalized.contains("bluetooth")) {
            val reply = if (detectedLang == "gu") "$boss, બ્લૂટૂથ સેટિંગ્સ ખોલી રહ્યો છું." else if (detectedLang == "hi") "$boss, ब्लूटूथ सेटिंग्स खोल रहा हूँ।" else "Opening Bluetooth settings, $boss."
            return AIResult(reply, detectedLang, DeviceCommand(CommandType.OPEN_BLUETOOTH_SETTINGS, query, conversationalReply = reply))
        }

        // Settings
        if (normalized.contains("setting") || normalized.contains("settings")) {
            val reply = if (detectedLang == "gu") "$boss, ફોન સેટિંગ્સ ખોલી રહ્યો છું." else if (detectedLang == "hi") "$boss, सेटिंग्स खोल रहा हूँ।" else "Opening system settings, $boss."
            return AIResult(reply, detectedLang, DeviceCommand(CommandType.OPEN_SETTINGS, query, conversationalReply = reply))
        }

        // Back / Home / Recents (Accessibility Navigation)
        if (normalized.contains("back") || normalized.contains("peeche") || normalized.contains("paachhal")) {
            val reply = if (detectedLang == "gu") "પાછળ જઈ રહ્યો છું." else if (detectedLang == "hi") "बैक कर दिया, $boss." else "Going back, $boss."
            return AIResult(reply, detectedLang, DeviceCommand(CommandType.NAVIGATE_BACK, query, conversationalReply = reply))
        }
        if (normalized.contains("home") || normalized.contains("ghar") || normalized.contains("mukhya")) {
            val reply = if (detectedLang == "gu") "હોમ સ્ક્રીન પર જઈ રહ્યો છું." else if (detectedLang == "hi") "होम स्क्रीन पर जा रहा हूँ, $boss." else "Navigating home, $boss."
            return AIResult(reply, detectedLang, DeviceCommand(CommandType.NAVIGATE_HOME, query, conversationalReply = reply))
        }
        if (normalized.contains("recent") || normalized.contains("task") || normalized.contains("all apps")) {
            val reply = if (detectedLang == "gu") "હાલની એપ્સ ખોલી રહ્યો છું." else if (detectedLang == "hi") "रीसेंट ऐप्स खोल रहा हूँ, $boss." else "Opening recent apps, $boss."
            return AIResult(reply, detectedLang, DeviceCommand(CommandType.OPEN_RECENTS, query, conversationalReply = reply))
        }

        // Alarm
        if (normalized.contains("alarm") || normalized.contains("wake me")) {
            val (h, m) = parseAlarmTime(normalized)
            val reply = if (detectedLang == "gu") "$boss, $h વાગ્યાનું અલાર્મ સેટ કરી રહ્યો છું." else if (detectedLang == "hi") "$boss, $h बजे का अलार्म सेट कर रहा हूँ।" else "Setting alarm for $h:$m, $boss."
            return AIResult(reply, detectedLang, DeviceCommand(CommandType.SET_ALARM, query, hour = h, minute = m, conversationalReply = reply))
        }

        // Phone call
        if (normalized.contains("call") || normalized.contains("phone lagao") || normalized.contains("phone karo")) {
            val contact = extractContactName(query)
            val reply = if (detectedLang == "gu") "$boss, $contact ને કૉલ કરી રહ્યો છું." else if (detectedLang == "hi") "$boss, $contact को कॉल लगा रहा हूँ।" else "Calling $contact, $boss."
            return AIResult(reply, detectedLang, DeviceCommand(CommandType.MAKE_CALL, query, contactName = contact, conversationalReply = reply))
        }

        // Open Apps (WhatsApp, YouTube, Instagram, Maps, Spotify, Chrome, etc.)
        val appName = extractAppName(normalized)
        if (appName != null) {
            val reply = if (detectedLang == "gu") "$boss, $appName ખોલી રહ્યો છું." else if (detectedLang == "hi") "$boss, $appName खोल रहा हूँ।" else "Opening $appName, $boss."
            return AIResult(reply, detectedLang, DeviceCommand(CommandType.OPEN_APP, query, targetApp = appName, conversationalReply = reply))
        }

        // General conversational responses
        val fallbackResponse = when (detectedLang) {
            "gu" -> "જી $boss, મેં સાંભળ્યું: '$query'. હું તમારા આદેશ માટે તૈયાર છું."
            "hi" -> "जी $boss, मैंने सुना: '$query'। मैं आपकी सहायता के लिए तैयार हूँ।"
            else -> "Understood, $boss: '$query'. How else can I assist you?"
        }
        return AIResult(fallbackResponse, detectedLang, DeviceCommand(CommandType.CONVERSATION_ONLY, query, conversationalReply = fallbackResponse))
    }

    private fun detectLanguage(text: String): String {
        var gujaratiCount = 0
        var devanagariCount = 0
        for (char in text) {
            val code = char.code
            if (code in 0x0A80..0x0AFF) gujaratiCount++
            else if (code in 0x0900..0x097F) devanagariCount++
        }
        if (gujaratiCount > 0) return "gu"
        if (devanagariCount > 0) return "hi"

        val lower = text.lowercase()
        val gujKeywords = listOf("kem", "chho", "cho", "aavyo", "kaho", "bhai", "nathi", "karvanu", "su", "shu", "che", "chhe")
        val hiKeywords = listOf("kya", "hai", "kholo", "batao", "bata", "karo", "suno", "bhejo", "kaise", "mera", "meri", "namaste", "chalo")

        for (w in gujKeywords) {
            if (lower.contains(w)) return "gu"
        }
        for (w in hiKeywords) {
            if (lower.contains(w)) return "hi"
        }
        return "en"
    }

    private fun extractAppName(text: String): String? {
        val apps = mapOf(
            "whatsapp" to "WhatsApp",
            "youtube" to "YouTube",
            "instagram" to "Instagram",
            "insta" to "Instagram",
            "maps" to "Google Maps",
            "google maps" to "Google Maps",
            "chrome" to "Chrome",
            "browser" to "Chrome",
            "spotify" to "Spotify",
            "music" to "YouTube Music",
            "gallery" to "Gallery",
            "photos" to "Photos",
            "calculator" to "Calculator",
            "clock" to "Clock",
            "gmail" to "Gmail",
            "email" to "Gmail",
            "telegram" to "Telegram",
            "play store" to "Google Play Store",
            "twitter" to "X (Twitter)",
            "x" to "X (Twitter)",
            "facebook" to "Facebook"
        )
        for ((key, app) in apps) {
            if (text.contains(key)) return app
        }
        return null
    }

    private fun extractReplyText(text: String): String {
        val parts = text.split("reply", "jawab", "uttar", "de de", "kar do", "bolo")
        return if (parts.size > 1) {
            parts.last().replace("—", "").replace(":", "").trim()
        } else {
            "Haan, theek hai"
        }
    }

    private fun extractContactName(text: String): String {
        val pattern = Pattern.compile("(papa|mummy|rahul|mom|dad|bhai|friend|[A-Za-z]+)\\s*(ko|ne|to)?\\s*(call|phone)", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(text)
        if (matcher.find()) {
            return matcher.group(1)?.replaceFirstChar { it.uppercase() } ?: "Contact"
        }
        return "Contact"
    }

    private fun parseAlarmTime(text: String): Pair<Int, Int> {
        val pattern = Pattern.compile("(\\d{1,2})\\s*(baje|am|pm|vage|o'clock)?", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(text)
        if (matcher.find()) {
            val hour = matcher.group(1)?.toIntOrNull() ?: 7
            return Pair(hour, 0)
        }
        val cal = Calendar.getInstance()
        cal.add(Calendar.HOUR_OF_DAY, 1)
        return Pair(cal.get(Calendar.HOUR_OF_DAY), 0)
    }
}
