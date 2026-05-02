package com.arkannsk.elval.completion

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class ElvalAnnotatorLogicTest {

    // Копируем паттерны из Annotator для тестирования
    private val keywordPattern = Pattern.compile("@evl:(validate|decor|rewrite)\\b")
    private val paramKvPattern = Pattern.compile("\\b(min|max|len|pattern|contains|starts_with|ends_with|enum|url|http_url|dsn|gt|gte|lt|lte|eq|neq):([^\\s]+)")

    @Test
    fun `Keyword pattern should match @evl-validate`() {
        val text = "// @evl:validate required"
        val matcher = keywordPattern.matcher(text)
        assertTrue(matcher.find(), "Should find @evl:validate")
        assertEquals("@evl:validate", matcher.group())
    }

    @Test
    fun `Keyword pattern should match @evl-decor`() {
        val text = "// @evl:decor uuid-gen"
        val matcher = keywordPattern.matcher(text)
        assertTrue(matcher.find(), "Should find @evl:decor")
        assertEquals("@evl:decor", matcher.group())
    }

    @Test
    fun `Param KV pattern should match min-10`() {
        val text = "// @evl:validate min:10 max:50"
        val matcher = paramKvPattern.matcher(text)

        // Ищем первое совпадение (min:10)
        assertTrue(matcher.find(), "Should find min:10")
        assertEquals("min", matcher.group(1))
        assertEquals("10", matcher.group(2))

        // Ищем второе совпадение (max:50)
        assertTrue(matcher.find(), "Should find max:50")
        assertEquals("max", matcher.group(1))
        assertEquals("50", matcher.group(2))
    }

    @Test
    fun `Param KV pattern should match pattern-email`() {
        val text = "// @evl:validate pattern:email"
        val matcher = paramKvPattern.matcher(text)
        assertTrue(matcher.find(), "Should find pattern:email")
        assertEquals("pattern", matcher.group(1))
        assertEquals("email", matcher.group(2))
    }
}
