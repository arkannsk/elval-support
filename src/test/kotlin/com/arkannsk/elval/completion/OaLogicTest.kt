package com.arkannsk.elval.completion

import com.arkannsk.elval.ElvalConstants
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class OaLogicTest {

    @Test
    fun `OA keys should contain title, description and type`() {
        val keys = ElvalConstants.OA_KEYS.keys
        assertTrue(keys.contains("title"), "Should contain 'title'")
        assertTrue(keys.contains("description"), "Should contain 'description'")
        assertTrue(keys.contains("type"), "Should contain 'type'")
        assertTrue(keys.contains("format"), "Should contain 'format'")
    }

    @Test
    fun `OA key hints should not be empty`() {
        ElvalConstants.OA_KEYS.values.forEach { hint ->
            assertTrue(hint.isNotEmpty(), "Hint for OA key should not be empty")
        }
    }
}
