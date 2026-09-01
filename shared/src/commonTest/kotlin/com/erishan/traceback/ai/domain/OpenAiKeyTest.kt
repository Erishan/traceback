package com.erishan.traceback.ai.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class OpenAiKeyTest {

    @Test
    fun lastFour_returnsLastFourCharacters() {
        assertEquals("WXYZ", lastFourOf("prefix-WXYZ"))
    }

    @Test
    fun lastFour_shortValue_returnsTheWholeValue() {
        assertEquals("ab", lastFourOf("ab"))
    }

    @Test
    fun lastFour_isNotTheFullSecretWhenLongerThanFour() {
        val secret = "abcdefghij"
        val four = lastFourOf(secret)
        assertEquals("ghij", four)
        assertEquals(4, four.length)
        assertNotEquals(secret, four)
    }

    @Test
    fun trimmedOpenAiKey_rejectsBlank() {
        assertNull(trimmedOpenAiKey(""))
        assertNull(trimmedOpenAiKey("   "))
        assertNull(trimmedOpenAiKey("\n\t"))
    }

    @Test
    fun trimmedOpenAiKey_trimsSurroundingWhitespace() {
        assertEquals("token", trimmedOpenAiKey("  token  "))
    }
}
