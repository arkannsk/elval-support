package com.arkannsk.elval.completion

import com.arkannsk.elval.ElvalConstants
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EvlLogicTest {

    @Test
    fun `EVL directives should contain validate, decor and rewrite`() {
        val directives = ElvalConstants.EVL_DIRECTIVES
        assertTrue(directives.contains("validate"), "Should contain 'validate'")
        assertTrue(directives.contains("decor"), "Should contain 'decor'")
        assertTrue(directives.contains("rewrite"), "Should contain 'rewrite'")
    }

    @Test
    fun `Validate params should contain required and min`() {
        val params = ElvalConstants.VALIDATE_PARAMS.keys
        assertTrue(params.contains("required"), "Should contain 'required'")
        assertTrue(params.contains("min:"), "Should contain 'min:'")
        assertTrue(params.contains("max:"), "Should contain 'max:'")
        assertTrue(params.contains("pattern:"), "Should contain 'pattern:'")
    }

    @Test
    fun `Validate param hints should not be empty`() {
        // Проверяем, что у каждого параметра есть описание (hint)
        ElvalConstants.VALIDATE_PARAMS.values.forEach { hint ->
            assertTrue(hint.isNotEmpty(), "Hint for param should not be empty")
        }
    }
}
