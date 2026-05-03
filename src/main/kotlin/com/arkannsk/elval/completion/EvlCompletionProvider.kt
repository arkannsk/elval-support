package com.arkannsk.elval.completion

import com.arkannsk.elval.ElvalConstants
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons

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

        if (matchedDirective == "validate") {
            val directiveEndIndex = fullContext.indexOf("validate") + "validate".length
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

            // ВАЖНОЕ ИЗМЕНЕНИЕ:
            // Если есть уже введенные параметры (кроме текущего слова) И текущее слово пустое или полностью совпадает с существующим,
            // то не предлагаем новые параметры — пользователь должен создать новую строку.
            val hasOtherParams = words.size > 1 || (words.size == 1 && words[0] != currentWord)

            if (hasOtherParams && (currentWord.isEmpty() || ElvalConstants.VALIDATE_PARAMS.keys.contains(currentWord))) {
                // Не предлагаем ничего, кроме возможного завершения текущего слова
                if (currentWord.isNotEmpty()) {
                    return ElvalConstants.VALIDATE_PARAMS.keys.filter { it.startsWith(currentWord) }
                } else {
                    return emptyList()
                }
            }

            // Иначе предлагаем параметры, которые начинаются на currentWord и не являются дубликатами
            return ElvalConstants.VALIDATE_PARAMS.keys.filter { param ->
                param.startsWith(currentWord) &&
                        (currentWord.isNotEmpty() || !words.contains(param))
            }
        }

        return ElvalConstants.EVL_DIRECTIVES.filter { it.startsWith(trimmedContext) }
    }

    fun getCompletionItems(rawContext: String): List<CompletionItem> {
        val suggestions = getSuggestions(rawContext)
        val trimmed = rawContext.trim()

        // Проверяем, является ли ввод незавершенной директивой (например, "val")
        val isPartialDirectiveInput = ElvalConstants.EVL_DIRECTIVES.any {
            it.startsWith(trimmed) && it != trimmed
        }

        val items = mutableListOf<CompletionItem>()

        for (s in suggestions) {
            // Формируем текст вставки
            // Для параметров с двоеточием (min:) или ключевых слов (required) пробел в конце не обязателен,
            // но для удобства автодополнения часто добавляют пробел, если это не конец строки.
            // В твоем примере параметры стоят в конце строки комментария, поэтому пробел может быть лишним,
            // но если пользователь хочет добавить еще что-то, пробел нужен.
            // Давай добавлять пробел только если это не параметр со значением (типа min:10),
            // но так как мы предлагаем только ключи (min:), пробел полезен.

            var insertText = s

            // Если это простой параметр без значения (required, email, url), добавим пробел в конце,
            // чтобы пользователь мог сразу начать писать следующую аннотацию или закрыть комментарий
            if (!s.endsWith(":")) {
                insertText += " "
            }

            // Рассчитываем смещение начала замены
            var startOffsetDelta = 0

            if (!isPartialDirectiveInput) {
                // Ищем последний пробел в исходном контексте, чтобы заменить только текущее слово
                val lastSpaceIndex = rawContext.lastIndexOf(' ')

                if (lastSpaceIndex != -1) {
                    startOffsetDelta = lastSpaceIndex + 1
                }
            }

            val hint = ElvalConstants.VALIDATE_PARAMS[s]
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

            var builder = LookupElementBuilder.create(item.insertText)
                .withLookupString(item.lookupString)
                .bold()
                .withInsertHandler(SimpleReplaceHandler(actualStartOffset, item.insertText))
                .withIcon(AllIcons.Nodes.Parameter)

            if (!item.hint.isNullOrEmpty()) {
                builder = builder.withTailText(" (${item.hint})", true)
            }

            result.addElement(builder)
        }
    }
}
