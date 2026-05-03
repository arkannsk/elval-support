package com.arkannsk.elval.completion

import com.arkannsk.elval.ElvalConstants
import com.intellij.codeInsight.completion.*
import com.arkannsk.elval.util.CompletionUtils

data class CompletionItem(
    val insertText: String,
    val lookupString: String,
    val startOffsetDelta: Int,
    val hint: String? = null
)

object EvlCompletionProvider {

    fun getSuggestions(rawContext: String): List<String> {
        val fullContext = rawContext
        val trimmedContext = fullContext.trim()

        if (trimmedContext.isEmpty()) {
            return ElvalConstants.EVL_DIRECTIVES.toList()
        }

        val matchedDirective = ElvalConstants.EVL_DIRECTIVES.firstOrNull { trimmedContext.startsWith(it) }

        return when (matchedDirective) {
            "validate" -> {
                handleDirectiveParams(fullContext, "validate", ElvalConstants.VALIDATE_PARAMS)
            }

            "decor" -> {
                handleDirectiveParams(fullContext, "decor", ElvalConstants.DECOR_PARAMS)
            }

            "rewrite" -> {
                handleDirectiveParams(fullContext, "rewrite", ElvalConstants.REWRITE_PARAMS)
            }

            else -> {
                ElvalConstants.EVL_DIRECTIVES.filter { it.startsWith(trimmedContext) }
            }
        }
    }

    private fun handleDirectiveParams(
        fullContext: String,
        directiveName: String,
        paramsMap: Map<String, String>
    ): List<String> {
        val directiveEndIndex = fullContext.indexOf(directiveName) + directiveName.length
        val afterDirectiveRaw = if (directiveEndIndex < fullContext.length) {
            fullContext.substring(directiveEndIndex)
        } else {
            ""
        }

        val endsWithSeparator = afterDirectiveRaw.endsWith(" ") || afterDirectiveRaw.endsWith(",")

        // Разбиваем на слова
        val words = afterDirectiveRaw.trim().split(Regex("[\\s,]+")).filter { it.isNotEmpty() }

        // Текущее вводимое слово
        val currentWord = if (endsWithSeparator) "" else words.lastOrNull() ?: ""

        // Логика "одна директива на строку": если есть другие параметры, не предлагаем новые,
        // если только пользователь не начал явно вводить новое слово.
        val hasOtherParams = words.size > 1 || (words.size == 1 && words[0] != currentWord)

        if (hasOtherParams && (currentWord.isEmpty() || paramsMap.keys.contains(currentWord))) {
            if (currentWord.isNotEmpty()) {
                return paramsMap.keys.filter { it.startsWith(currentWord) }
            } else {
                return emptyList()
            }
        }


        val result = paramsMap.keys.filter { param ->
            param.startsWith(currentWord) &&
                    (currentWord.isNotEmpty() || !words.contains(param))
        }
        return result
    }

    fun getCompletionItems(rawContext: String): List<CompletionItem> {
        val suggestions = getSuggestions(rawContext)
        val trimmed = rawContext.trim()

        val isPartialDirectiveInput = ElvalConstants.EVL_DIRECTIVES.any {
            it.startsWith(trimmed) && it != trimmed
        }

        // Определяем, какая директива активна, чтобы выбрать правильную мапу хинтов
        val activeDirective = ElvalConstants.EVL_DIRECTIVES.firstOrNull { trimmed.startsWith(it) }
        val paramsMap = when (activeDirective) {
            "validate" -> ElvalConstants.VALIDATE_PARAMS
            "decor" -> ElvalConstants.DECOR_PARAMS
            "rewrite" -> ElvalConstants.REWRITE_PARAMS
            else -> emptyMap()
        }

        val items = mutableListOf<CompletionItem>()

        for (s in suggestions) {
            var insertText = s

            // Добавляем пробел в конце, если параметр не заканчивается на ':'
            if (!s.endsWith(":")) {
                insertText += " "
            }

            var startOffsetDelta = 0

            if (!isPartialDirectiveInput) {
                val lastSpaceIndex = rawContext.lastIndexOf(' ')
                val lastCommaIndex = rawContext.lastIndexOf(',')
                val lastSeparatorIndex = maxOf(lastSpaceIndex, lastCommaIndex)

                if (lastSeparatorIndex != -1) {
                    startOffsetDelta = lastSeparatorIndex + 1
                }
            }

            val hint = paramsMap[s]
            items.add(CompletionItem(insertText, s, startOffsetDelta, hint))
        }

        return items
    }

    fun fillCompletions(
        result: CompletionResultSet,
        absoluteContextStartOffset: Int,
        cursorOffset: Int,
        rawContext: String
    ) {
        val items = getCompletionItems(rawContext)

        for (item in items) {
            val actualStartOffset = absoluteContextStartOffset + item.startOffsetDelta

            // Просто используй val, тип выведется автоматически как LookupElement
            val element = CompletionUtils.createPrioritizedElement(
                lookupString = item.lookupString,
                insertText = item.insertText,
                startOffset = actualStartOffset,
                hint = item.hint
            )

            result.addElement(element)
        }
    }
}
