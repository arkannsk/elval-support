package com.arkannsk.elval.highlighting

import com.intellij.lang.annotation.AnnotationHolder

object EvlAnnotationHandler {

    // Простые параметры (без значений)
    private val SIMPLE_PARAMS = setOf(
        "required", "optional", "not-zero",
        "email", "uuid", "phone", "ip", "url", "http_url", "dsn",
        "trim", "lowercase", "uppercase",
        "inline", "flatten",
        "time-now", "uuid-gen"
    )

    // Regex для KV-параметров (ключ:значение)
    // Поддерживает: min:, max:, len:, gt:, lt:, gte:, lte:, eq:, neq:, pattern:, enum:, contains:, starts_with:, ends_with:, ctx-get:, httpctx-get:, env-get:, default:, prefix:, suffix:, ref:, type:
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
            if (text[i].isWhitespace() || text[i] == '/') {
                i++
                continue
            }

            if (text.substring(i).startsWith("@evl:")) {
                val directiveEnd = findWordEnd(text, i + 5)
                val directiveName = text.substring(i + 5, directiveEnd)

                // Подсвечиваем директиву
                ElvalHighlighter.highlightKeyword(holder, baseOffset + i, baseOffset + directiveEnd)

                i = directiveEnd

                if (directiveName == "decor") {
                    handleDecorValue(text, i, baseOffset, holder)
                    continue
                }

                // Обработка параметров validate/rewrite
                skipWhitespace(text, i).let { paramStart ->
                    if (paramStart < len && text[paramStart] != '\n' && text[paramStart] != '\r') {
                        val paramEnd = findWordEnd(text, paramStart)
                        val paramName = text.substring(paramStart, paramEnd)

                        if (SIMPLE_PARAMS.contains(paramName)) {
                            // Простой параметр (required, email...)
                            ElvalHighlighter.highlightFunctionCall(holder, baseOffset + paramStart, baseOffset + paramEnd)
                        } else if (isKvParam(paramName)) {
                            // KV-параметр (min:10, pattern:email...)
                            val colonIndex = paramName.indexOf(':')
                            if (colonIndex > 0) {
                                // Имя параметра (min:)
                                ElvalHighlighter.highlightFunctionCall(holder, baseOffset + paramStart, baseOffset + paramStart + colonIndex + 1)
                                // Значение (10, email...)
                                if (colonIndex + 1 < paramName.length) {
                                    ElvalHighlighter.highlightString(holder, baseOffset + paramStart + colonIndex + 1, baseOffset + paramEnd)
                                }
                            }
                        }
                        i = paramEnd
                    }
                }
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
