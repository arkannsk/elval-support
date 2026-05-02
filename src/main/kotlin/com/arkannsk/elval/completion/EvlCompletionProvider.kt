package com.arkannsk.elval.completion

import com.arkannsk.elval.ElvalConstants
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder

object EvlCompletionProvider {

    fun fillCompletions(
        result: CompletionResultSet,
        prefixEndIndex: Int,
        cursorOffset: Int,
        rawContext: String
    ) {
        val trimmedContext = rawContext.trim()

        // Проверяем, начинается ли контекст с известной директивы
        val matchedDirective = ElvalConstants.EVL_DIRECTIVES.firstOrNull { directive ->
            trimmedContext.startsWith(directive)
        }

        if (matchedDirective != null) {
            // Директива найдена (например, "validate"). Предлагаем параметры.
            // Вычисляем, что написано после директивы
            val afterDirective = trimmedContext.substring(matchedDirective.length).trimStart()

            handleEvParams(result, cursorOffset, afterDirective, matchedDirective)
        } else {
            // Директива не распознана или неполная. Предлагаем список директив.
            for (directive in ElvalConstants.EVL_DIRECTIVES) {
                if (directive.startsWith(trimmedContext)) {
                    val insertText = "$directive "
                    val startOffset = cursorOffset - trimmedContext.length
                    result.addElement(LookupElementBuilder.create(insertText)
                        .withLookupString(directive)
                        .withTailText(" (ElVal)")
                        .bold()
                        .withInsertHandler(SimpleReplaceHandler(startOffset, insertText)))
                }
            }
        }
    }

    private fun handleEvParams(
        result: CompletionResultSet,
        cursorOffset: Int,
        paramsPart: String,
        directive: String
    ) {
        // Пока поддерживаем параметры только для validate
        if (directive == "validate") {
            for ((param, hint) in ElvalConstants.VALIDATE_PARAMS) {
                if (param.startsWith(paramsPart)) {
                    val insertText = if (param.endsWith(":") || param == "required") param else "$param "
                    val startOffset = cursorOffset - paramsPart.length

                    result.addElement(LookupElementBuilder.create(insertText)
                        .withLookupString(param)
                        .withTailText(" ($hint)")
                        .bold()
                        .withInsertHandler(SimpleReplaceHandler(startOffset, insertText)))
                }
            }
        }
        // Для decor и rewrite можно добавить аналогичную логику в будущем
    }

    // Функция для получения списка подсказок на основе контекста (для тестов и использования в Contributor)
    fun getSuggestions(rawContext: String): List<String> {
        val trimmedContext = rawContext.trim()
        val suggestions = mutableListOf<String>()

        if (trimmedContext.isEmpty()) {
            // Если пусто, предлагаем все директивы
            return ElvalConstants.EVL_DIRECTIVES.toList()
        }

        // Проверяем, начинается ли контекст с известной директивы (например, "validate ...")
        val matchedDirective = ElvalConstants.EVL_DIRECTIVES.firstOrNull { directive ->
            trimmedContext.startsWith(directive)
        }

        if (matchedDirective != null) {
            // Директива найдена. Предлагаем параметры.
            val afterDirective = trimmedContext.substring(matchedDirective.length).trimStart()

            if (matchedDirective == "validate") {
                for ((param, _) in ElvalConstants.VALIDATE_PARAMS) {
                    if (param.startsWith(afterDirective)) {
                        suggestions.add(param)
                    }
                }
            } else if (matchedDirective == "decor") {
                // TODO: Добавить параметры для decor в будущем
            } else if (matchedDirective == "rewrite") {
                // TODO: Добавить параметры для rewrite в будущем
            }
        } else {
            // Директива не распознана полностью.
            // Проверяем, является ли trimmedContext ПРЕФИКСОМ какой-либо директивы.
            // Например, "ev" является префиксом для "validate"? Нет.
            // "val" является префиксом для "validate"? Да.
            // "dec" является префиксом для "decor"? Да.

            for (directive in ElvalConstants.EVL_DIRECTIVES) {
                if (directive.startsWith(trimmedContext)) {
                    suggestions.add(directive)
                }
            }
        }

        return suggestions
    }

    internal class SimpleReplaceHandler(private val startOffset: Int, private val replacementText: String) :
        InsertHandler<LookupElement> {

        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val editor = context.editor
            val currentCaretOffset = editor.caretModel.offset

            if (startOffset < 0 || startOffset > editor.document.textLength) return

            editor.document.deleteString(startOffset, currentCaretOffset)
            editor.document.insertString(startOffset, replacementText)
            editor.caretModel.moveToOffset(startOffset + replacementText.length)
        }
    }
}
