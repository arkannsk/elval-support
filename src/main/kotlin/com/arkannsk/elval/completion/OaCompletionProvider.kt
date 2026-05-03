package com.arkannsk.elval.completion

import com.arkannsk.elval.ElvalConstants
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons

object OaCompletionProvider {

    fun getSuggestions(rawContext: String): List<String> {
        val hasSpace = rawContext.contains(" ")
        val trimmed = rawContext.trim()
        val list = mutableListOf<String>()

        if (trimmed.isEmpty()) {
            return ElvalConstants.OA_KEYS.keys.toList()
        }

        if (!hasSpace) {
            for ((key, _) in ElvalConstants.OA_KEYS) {
                if (key.startsWith(trimmed)) {
                    list.add(key)
                }
            }
        } else {
            val afterKey = rawContext.substringAfter(" ").trim()
            val commonValues = listOf("\"\"", "string", "integer", "boolean")
            for (value in commonValues) {
                if (value.startsWith(afterKey)) {
                    list.add(value)
                }
            }
        }
        return list
    }

    fun fillCompletions(
        result: CompletionResultSet,
        absoluteContextStartOffset: Int,
        cursorOffset: Int,
        rawContext: String
    ) {
        val suggestions = getSuggestions(rawContext)
        val hasSpace = rawContext.contains(" ")

        for (s in suggestions) {
            val insertText = if (!hasSpace) "$s " else s

            var startOffsetDelta = 0
            if (hasSpace) {
                val lastSpaceIndex = rawContext.lastIndexOf(' ')
                if (lastSpaceIndex != -1) {
                    startOffsetDelta = lastSpaceIndex + 1
                }
            }

            val actualStartOffset = absoluteContextStartOffset + startOffsetDelta
            val hint = ElvalConstants.OA_KEYS[s]

            val builder = LookupElementBuilder.create(insertText)
                .withLookupString(s)
                .bold()
                .withInsertHandler(SimpleReplaceHandler(actualStartOffset, insertText))
                .withIcon(AllIcons.Nodes.Property)

            if (!hint.isNullOrEmpty()) {
                builder.withTailText(" ($hint)", true)
            }

            result.addElement(builder)
        }
    }
}
