package com.arkannsk.elval.completion

import com.arkannsk.elval.ElvalConstants
import com.arkannsk.elval.util.SimpleReplaceHandler
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.arkannsk.elval.util.CompletionUtils

object OaCompletionProvider {

    fun getSuggestions(rawContext: String): List<String> {
        val trimmedContext = rawContext.trim()

        if (trimmedContext.isEmpty()) {
            return ElvalConstants.OA_KEYS.keys.toList()
        }

        // --- ОБРАБОТКА @oa:in ---
        if (trimmedContext == "in" || trimmedContext.startsWith("in ")) {
            val afterIn = if (trimmedContext.startsWith("in ")) {
                trimmedContext.substring(3).trimStart() // "in ".length == 3
            } else {
                ""
            }

            return ElvalConstants.OA_IN_VALUES.filter { it.startsWith(afterIn) }
        }

        // --- ОБРАБОТКА @oa:rewrite. ---
        if (trimmedContext.startsWith("rewrite.")) {
            val afterRewrite = trimmedContext.substring(8).trimStart()
            if ("type".startsWith(afterRewrite)) {
                return listOf("type")
            }
            return emptyList()
        }

        // --- ОБЫЧНЫЕ КЛЮЧИ OA ---
        return ElvalConstants.OA_KEYS.keys.filter {
            it.startsWith(trimmedContext) && it != "in"
        }
    }

    fun fillCompletions(
        result: CompletionResultSet,
        absoluteContextStartOffset: Int,
        cursorOffset: Int,
        rawContext: String
    ) {
        val suggestions = getSuggestions(rawContext)

        val isInValueContext = rawContext.trim().let {
            it == "in" || it.startsWith("in ")
        }

        for (s in suggestions) {
            var insertText = s

            // Добавляем пробел, если это значение для 'in' или обычный ключ
            if (isInValueContext || (!s.endsWith(":") && !s.endsWith("."))) {
                insertText += " "
            }

            // Рассчитываем смещение начала замены (исправляем ошибку с переменной)
            val lastSpaceIndex = rawContext.lastIndexOf(' ')
            val startOffsetDelta = if (lastSpaceIndex != -1) lastSpaceIndex + 1 else 0

            val actualStartOffset = absoluteContextStartOffset + startOffsetDelta
            val hint = if (isInValueContext) "Location for parameter" else ElvalConstants.OA_KEYS[s]

            // Используем утилиту, тип выводится автоматически
            val element = CompletionUtils.createPrioritizedElement(
                lookupString = s,
                insertText = insertText,
                startOffset = actualStartOffset,
                hint = hint,
                icon = AllIcons.Nodes.Property
            )

            result.addElement(element)
        }
    }
}
