package com.arkannsk.elval.highlighting

import com.intellij.lang.annotation.AnnotationHolder

object EvlAnnotationHandler {

    private val SIMPLE_PARAMS = setOf(
        "required", "optional", "not-zero",
        "email", "uuid", "phone", "ip", "url", "http_url", "dsn",
        "trim", "lowercase", "uppercase",
        "inline", "flatten",
        "time-now", "uuid-gen"
    )

    private val KV_PARAM_PREFIXES = listOf(
        "min", "max", "len",
        "gt", "lt", "gte", "lte", "eq", "neq",
        "pattern", "enum", "contains", "starts_with", "ends_with",
        "ctx-get", "httpctx-get", "env-get",
        "default", "prefix", "suffix",
        "ref", "type"
    )

    private fun isKvParam(paramName: String): Boolean {
        return KV_PARAM_PREFIXES.any { prefix -> paramName.startsWith("$prefix:") }
    }

    fun annotate(text: String, baseOffset: Int, holder: AnnotationHolder) {
        var i = 0
        val len = text.length

        while (i < len) {
            // Пропускаем пробелы и слеши комментариев
            if (text[i].isWhitespace() || text[i] == '/') {
                i++
                continue
            }

            // Проверяем начало директивы @evl:
            if (text.substring(i).startsWith("@evl:")) {
                val directiveEnd = findWordEnd(text, i + 5)
                val directiveName = text.substring(i + 5, directiveEnd)

                // Подсвечиваем саму директиву (@evl:validate)
                ElvalHighlighter.highlightKeyword(holder, baseOffset + i, baseOffset + directiveEnd)

                i = directiveEnd

                // Если это декоратор, обрабатываем его значение отдельно и завершаем обработку этой директивы
                if (directiveName == "decor") {
                    handleDecorValue(text, i, baseOffset, holder)
                    // После обработки значения декоратора, i уже стоит в конце значения.
                    // Цикл продолжится, но так как дальше обычно идет конец строки или новая аннотация, это ок.
                    continue
                }

                // Найдем конец текущей строки комментария
                val endOfLine = text.indexOfAny(charArrayOf('\n', '\r'), i)
                val lineEnd = if (endOfLine == -1) len else endOfLine

                // Парсим параметры от i до lineEnd
                var paramCursor = i
                while (paramCursor < lineEnd) {
                    // Пропускаем пробелы
                    while (paramCursor < lineEnd && text[paramCursor].isWhitespace()) {
                        paramCursor++
                    }
                    if (paramCursor >= lineEnd) break

                    val wordStart = paramCursor
                    val wordEnd = findWordEnd(text, paramCursor)
                    val word = text.substring(wordStart, wordEnd)

                    if (SIMPLE_PARAMS.contains(word)) {
                        ElvalHighlighter.highlightFunctionCall(holder, baseOffset + wordStart, baseOffset + wordEnd)
                    } else if (isKvParam(word)) {
                        val colonIndex = word.indexOf(':')
                        if (colonIndex > 0) {
                            ElvalHighlighter.highlightFunctionCall(holder, baseOffset + wordStart, baseOffset + wordStart + colonIndex + 1)
                            if (colonIndex + 1 < word.length) {
                                ElvalHighlighter.highlightString(holder, baseOffset + wordStart + colonIndex + 1, baseOffset + wordEnd)
                            }
                        }
                    }

                    paramCursor = wordEnd
                }

                // Перемещаем основной указатель i в конец строки, чтобы не обрабатывать эту строку повторно
                i = lineEnd
                continue
            }

            i++
        }
    }

    private fun handleDecorValue(text: String, start: Int, baseOffset: Int, holder: AnnotationHolder) {
        skipWhitespace(text, start).let { valueStart ->
            if (valueStart < text.length) {
                val valueEnd = findWordEnd(text, valueStart)
                ElvalHighlighter.highlightString(holder, baseOffset + valueStart, baseOffset + valueEnd)
            }
        }
    }

    private fun findWordEnd(text: String, start: Int): Int {
        var j = start
        while (j < text.length && !text[j].isWhitespace() && text[j] != '\n' && text[j] != '\r') {
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
}
