package com.example.engine

import android.content.Context
import com.example.ai.AIContext
import com.example.ai.AIProviderManager
import com.example.service.TarunAccessibilityService

object ScreenAnalysisHelper {

    suspend fun analyzeCurrentScreenText(aiManager: AIProviderManager, bossTitle: String): String {
        // Collect visible text from accessibility nodes
        val screenContent = TarunAccessibilityService.getScreenTextDump()
        val prompt = if (screenContent.isNotBlank()) {
            "Please analyze and summarize this visible screen content for $bossTitle:\n\n$screenContent\n\nExplain key details, notifications, or actionable items clearly in 2-3 sentences."
        } else {
            "Explain that the current screen was analyzed and no active UI text was extracted from background nodes, and offer to assist with any direct queries for $bossTitle."
        }

        val result = aiManager.processQuery(
            prompt,
            AIContext(bossTitle = bossTitle)
        )
        return result.spokenText
    }

    suspend fun summarizeChatText(rawChatText: String, aiManager: AIProviderManager, bossTitle: String): String {
        if (rawChatText.isBlank()) return "Please paste or provide chat content to analyze."

        val prompt = """
            Analyze this conversation snippet for $bossTitle:
            $rawChatText

            Provide:
            1. Overall Tone & Emotion
            2. Key Summary Points (bullet points)
            3. Unanswered Questions or Action Items
        """.trimIndent()

        val result = aiManager.processQuery(
            prompt,
            AIContext(bossTitle = bossTitle)
        )
        return result.spokenText
    }
}
