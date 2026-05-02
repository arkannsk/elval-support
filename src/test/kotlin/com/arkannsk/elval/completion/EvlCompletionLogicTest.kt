package com.arkannsk.elval.completion

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EvlCompletionLogicTest {

    @Test
    fun `should suggest all directives when context is empty inside evl prefix`() {
        // Симуляция: пользователь написал "@evl:" и нажал Ctrl+Space.
        val suggestions = EvlCompletionProvider.getSuggestions("")

        assertTrue(suggestions.contains("validate"), "Should suggest 'validate'")
        assertTrue(suggestions.contains("decor"), "Should suggest 'decor'")
        assertTrue(suggestions.contains("rewrite"), "Should suggest 'rewrite'")
        assertEquals(3, suggestions.size, "Should suggest exactly 3 directives")
    }

    @Test
    fun `should filter directives by prefix val`() {
        // Симуляция: пользователь написал "@evl:val"
        val suggestions = EvlCompletionProvider.getSuggestions("val")

        assertTrue(suggestions.contains("validate"), "Should suggest 'validate'")
        assertEquals(1, suggestions.size, "Should only suggest 'validate'")
    }

    @Test
    fun `should suggest validate params after directive and space`() {
        // Симуляция: пользователь написал "@evl:validate "
        val suggestions = EvlCompletionProvider.getSuggestions("validate ")

        assertTrue(suggestions.contains("required"), "Should suggest 'required'")
        assertTrue(suggestions.contains("min:"), "Should suggest 'min:'")
        assertTrue(suggestions.contains("max:"), "Should suggest 'max:'")
    }

    @Test
    fun `should filter validate params by prefix m`() {
        // Симуляция: пользователь написал "@evl:validate m"
        val suggestions = EvlCompletionProvider.getSuggestions("validate m")

        assertTrue(suggestions.contains("min:"), "Should suggest 'min:'")
        assertTrue(suggestions.contains("max:"), "Should suggest 'max:'")
        assertTrue(!suggestions.contains("required"), "Should NOT suggest 'required'")
    }

    @Test
    fun `should handle partial directive input like dec`() {
        // Симуляция: пользователь написал "@evl:dec"
        val suggestions = EvlCompletionProvider.getSuggestions("dec")

        assertTrue(suggestions.contains("decor"), "Should suggest 'decor'")
        assertEquals(1, suggestions.size, "Should only suggest 'decor'")
    }
}
