package com.arkannsk.elval.completion

import com.arkannsk.elval.ElvalConstants
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder

object OaCompletionProvider {

    fun fillCompletions(
        result: CompletionResultSet,
        cursorOffset: Int,
        rawContext: String
    ) {
        val trimmedContext = rawContext.trim()

        if (!rawContext.contains(" ")) {
            // Предлагаем ключи
            for ((key, hint) in ElvalConstants.OA_KEYS) {
                if (key.startsWith(trimmedContext)) {
                    val insertText = "$key "
                    val startOffset = cursorOffset - trimmedContext.length
                    result.addElement(LookupElementBuilder.create(insertText)
                        .withLookupString(key)
                        .withTailText(" ($hint)")
                        .bold()
                        .withInsertHandler(EvlCompletionProvider.SimpleReplaceHandler(startOffset, insertText)))
                }
            }
        } else {
            // Ключ введен, предлагаем значения (заглушка)
            val key = rawContext.substringBefore(" ").trim()
            val valuePart = rawContext.substringAfter(" ").trim()

            if (key == "title" || key == "description") {
                result.addElement(LookupElementBuilder.create("\"\"")
                    .withTailText(" (String)")
                    .withInsertHandler(EvlCompletionProvider.SimpleReplaceHandler(cursorOffset - valuePart.length, "\"\"")))
            }
        }
    }
}
