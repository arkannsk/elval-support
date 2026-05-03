package com.arkannsk.elval.highlighting

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement

class ElvalAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is PsiComment) return

        val text = element.text
        val baseOffset = element.textRange.startOffset
        val len = text.length

        var i = 0
        while (i < len) {
            // Пропускаем пробелы и разделители
            if (text[i].isWhitespace() || text[i] == '/') {
                i++
                continue
            }

            // --- 1. Проверка на @evl: или @oa: ---
            if (text[i] == '@') {
                val rest = text.substring(i)

                // Обработка @evl:...
                if (rest.startsWith("@evl:")) {
                    val directiveEnd = findWordEnd(text, i + 5) // после "@evl:"
                    val directiveName = text.substring(i + 5, directiveEnd)

                    // Красим саму директиву (@evl:validate)
                    highlight(holder, baseOffset + i, baseOffset + directiveEnd, DefaultLanguageHighlighterColors.KEYWORD)

                    i = directiveEnd

                    // Если это декоратор, следующее слово — это значение
                    if (directiveName == "decor") {
                        skipWhitespace(text, i).let { nextStart ->
                            if (nextStart < len) {
                                val valueEnd = findWordEnd(text, nextStart)
                                highlight(holder, baseOffset + nextStart, baseOffset + valueEnd, DefaultLanguageHighlighterColors.STRING)
                                i = valueEnd
                            }
                        }
                    }
                    continue
                }

                // Обработка @oa:...
                if (rest.startsWith("@oa:")) {
                    val keyEnd = findKeyEnd(text, i + 4) // после "@oa:"
                    // Красим ключ (@oa:title или @oa:rewrite.type)
                    highlight(holder, baseOffset + i, baseOffset + keyEnd, DefaultLanguageHighlighterColors.METADATA)

                    i = keyEnd

                    // Пропускаем пробелы до значения
                    skipWhitespace(text, i).let { valueStart ->
                        if (valueStart < len && text[valueStart] != '\n' && text[valueStart] != '\r') {

                            var valueEnd = len // По умолчанию берем до конца строки

                            // Проверяем, начинается ли значение с кавычки
                            if (text[valueStart] == '"') {
                                val closeQuote = text.indexOf('"', valueStart + 1)
                                if (closeQuote != -1) {
                                    valueEnd = closeQuote + 1 // Включаем закрывающую кавычку
                                }
                            } else {
                                // Если без кавычек, ищем конец строки или следующую аннотацию
                                val nextAt = text.indexOf('@', valueStart)
                                if (nextAt != -1 && nextAt > valueStart) {
                                    // Если нашли следующую аннотацию, обрезаем значение перед ней
                                    // Но нужно убедиться, что это не часть текущего значения (например, в тексте описания)
                                    // Для простоты: если следующий символ после пробела - @, то считаем это новой аннотацией
                                    val beforeNextAt = skipWhitespaceBackwards(text, nextAt)
                                    if (beforeNextAt > valueStart) {
                                        valueEnd = beforeNextAt
                                    }
                                }
                                // Иначе оставляем valueEnd = len (до конца строки)
                            }

                            // Красим всё значение целиком
                            highlight(holder, baseOffset + valueStart, baseOffset + valueEnd, DefaultLanguageHighlighterColors.STRING)
                            i = valueEnd
                        }
                    }
                    continue
                }
            }

            // --- 2. Проверка на параметры валидации (если мы внутри @evl:validate) ---
            // Это упрощенная логика: если видим слово, похожее на параметр, красим его.
            // Для точности лучше отслеживать состояние "мы внутри validate", но для начала попробуем так:

            val wordEnd = findWordEnd(text, i)
            val word = text.substring(i, wordEnd)

            // Список известных параметров (можно вынести в константы)
            if (isEvParam(word)) {
                // Если параметр типа min:10, то красим 'min:' как имя, '10' как значение
                if (word.contains(":")) {
                    val colonIndex = word.indexOf(':')
                    highlight(holder, baseOffset + i, baseOffset + i + colonIndex + 1, DefaultLanguageHighlighterColors.FUNCTION_CALL) // Имя параметра
                    if (colonIndex + 1 < word.length) {
                        highlight(holder, baseOffset + i + colonIndex + 1, baseOffset + wordEnd, DefaultLanguageHighlighterColors.NUMBER) // Значение
                    }
                } else {
                    // Простой параметр (required, email)
                    highlight(holder, baseOffset + i, baseOffset + wordEnd, DefaultLanguageHighlighterColors.FUNCTION_CALL)
                }
            }

            i = wordEnd
        }
    }

    // Вспомогательные функции

    private fun isEvParam(word: String): Boolean {
        return word in setOf(
            "required", "optional", "not-zero", "email", "uuid", "phone", "ip", "url", "http_url",
            "trim", "lowercase", "uppercase", "inline", "flatten", "time-now", "uuid-gen"
        ) || word.matches(Regex("^(min|max|len|gt|lt|gte|lte|eq|neq|pattern|enum|contains|starts_with|ends_with|ctx-get|httpctx-get|env-get|default|prefix|suffix|ref|type):.*"))
    }

    private fun findWordEnd(text: String, start: Int): Int {
        var j = start
        while (j < text.length && !text[j].isWhitespace() && text[j] != '\n' && text[j] != '\r') {
            j++
        }
        return j
    }

    private fun findKeyEnd(text: String, start: Int): Int {
        var j = start
        while (j < text.length && (text[j].isLetterOrDigit() || text[j] == '.' || text[j] == '_')) {
            j++
        }
        return j
    }

    private fun skipWhitespace(text: String, start: Int): Int {
        var j = start
        while (j < text.length && text[j].isWhitespace()) {
            j++
        }
        return j
    }

    private fun skipWhitespaceBackwards(text: String, pos: Int): Int {
        var j = pos - 1
        while (j >= 0 && text[j].isWhitespace()) {
            j--
        }
        return j + 1
    }

    private fun highlight(holder: AnnotationHolder, start: Int, end: Int, key: TextAttributesKey) {
        if (start >= end) return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(TextRange(start, end))
            .textAttributes(key)
            .create()
    }
}
