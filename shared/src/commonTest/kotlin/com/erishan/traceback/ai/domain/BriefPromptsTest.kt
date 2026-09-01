package com.erishan.traceback.ai.domain

import com.erishan.traceback.me.domain.UserContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BriefPromptsTest {

    @Test
    fun systemPrompt_includesProfileAndJsonKeys() {
        val prompt = briefSystemPrompt(
            UserContext(
                about = "Android, no PHP",
                rateBand = "mid",
                pace = "one client",
            )
        )

        assertTrue(prompt.contains("Android, no PHP"))
        assertTrue(prompt.contains("mid"))
        assertTrue(prompt.contains("one client"))
        assertTrue(prompt.contains("fit.verdict"))
        assertTrue(prompt.contains("approach.technologies"))
        assertTrue(prompt.contains("JSON"))
    }

    @Test
    fun userMessage_omitsBlankOptionalFields() {
        val message = briefUserMessage(
            JobInput(
                title = "Dashboard",
                description = "Migrate XML",
                source = "UPWORK",
                sourceLabel = null,
                appliedMessage = null,
            )
        )

        assertTrue(message.contains("Title: Dashboard"))
        assertTrue(message.contains("Description: Migrate XML"))
        assertTrue(message.contains("Source: UPWORK"))
        assertFalse(message.contains("Source label:"))
        assertFalse(message.contains("Applied message:"))
    }

    @Test
    fun userMessage_includesSourceLabelAndAppliedMessageWhenPresent() {
        val message = briefUserMessage(
            JobInput(
                title = "Dashboard",
                description = null,
                source = "OTHER",
                sourceLabel = "Twitter DM",
                appliedMessage = "Hi, I can help.",
            )
        )

        assertTrue(message.contains("Source label: Twitter DM"))
        assertTrue(message.contains("Applied message: Hi, I can help."))
    }
}
