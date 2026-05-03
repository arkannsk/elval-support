package com.arkannsk.elval.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiComment

class ElvalCompletionContributor : CompletionContributor() {

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val position = parameters.position

        if (position !is PsiComment) return

        val commentText = position.text
        val cursorOffsetInFile = parameters.offset
        val commentStartOffset = position.textRange.startOffset
        val relativeCursorPos = cursorOffsetInFile - commentStartOffset

        if (relativeCursorPos > commentText.length) return

        val textBeforeCursor = commentText.substring(0, relativeCursorPos)

        // 1. Проверка на базовый триггер @
        val lastAtIndex = textBeforeCursor.lastIndexOf('@')
        if (lastAtIndex == -1) {
            return
        }

        val textAfterAt = textBeforeCursor.substring(lastAtIndex + 1)

        val isEvPrefix = textAfterAt.startsWith("evl") || "evl".startsWith(textAfterAt)
        val isOaPrefix = textAfterAt.startsWith("oa") || "oa".startsWith(textAfterAt)

        val isValidContext = textAfterAt.isEmpty() || isEvPrefix || isOaPrefix

        if (!isValidContext) {
            return
        }

        var prefixEndIndex = -1
        var currentContext = ""

        if (isEvPrefix) {
            val evlIndex = textBeforeCursor.indexOf("@evl:", lastAtIndex)
            if (evlIndex != -1) {
                prefixEndIndex = evlIndex + "@evl:".length
                currentContext = textBeforeCursor.substring(prefixEndIndex)
            } else {
                prefixEndIndex = lastAtIndex + 1
                currentContext = textAfterAt
            }
        } else if (isOaPrefix) {
            val oaIndex = textBeforeCursor.indexOf("@oa:", lastAtIndex)
            if (oaIndex != -1) {
                prefixEndIndex = oaIndex + "@oa:".length
                currentContext = textBeforeCursor.substring(prefixEndIndex)
            } else {
                prefixEndIndex = lastAtIndex + 1
                currentContext = textAfterAt
            }
        }

        // Случай 1: Просто @ или частичный ввод (@e, @ev, @o)
        if (prefixEndIndex == -1 || !textBeforeCursor.contains(":")) {

            val absoluteStartOffset = commentStartOffset + lastAtIndex

            if (isEvPrefix || textAfterAt.isEmpty()) {
                result.addElement(LookupElementBuilder.create("@evl:")
                    .withLookupString("evl:")
                    .withTailText(" (ElVal)")
                    .bold()
                    .withInsertHandler(SimpleReplaceHandler(absoluteStartOffset, "@evl:")))
            }

            if (isOaPrefix || textAfterAt.isEmpty()) {
                result.addElement(LookupElementBuilder.create("@oa:")
                    .withLookupString("oa:")
                    .withTailText(" (OpenAPI)")
                    .bold()
                    .withInsertHandler(SimpleReplaceHandler(absoluteStartOffset, "@oa:")))
            }

            result.stopHere()
            return
        }

        // Вычисляем абсолютный индекс начала контекста (после @evl: или @oa:) в файле
        val absoluteContextStartOffset = commentStartOffset + prefixEndIndex

        // Случай 2: Мы внутри @evl: или @oa: (двоеточие уже есть)
        if (isEvPrefix) {
            EvlCompletionProvider.fillCompletions(result, absoluteContextStartOffset, cursorOffsetInFile, currentContext)
        } else {
            OaCompletionProvider.fillCompletions(result, absoluteContextStartOffset, cursorOffsetInFile, currentContext)
        }
        result.stopHere()
    }
}
