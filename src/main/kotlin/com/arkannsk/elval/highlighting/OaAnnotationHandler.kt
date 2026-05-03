package com.arkannsk.elval.highlighting

import com.intellij.lang.annotation.AnnotationHolder

object OaAnnotationHandler {

    fun annotate(text: String, baseOffset: Int, holder: AnnotationHolder) {
        var i = 0
        val len = text.length

        while (i < len) {
            if (text[i].isWhitespace() || text[i] == '/') {
                i++
                continue
            }

            if (text.substring(i).startsWith("@oa:")) {
                val keyEnd = findKeyEnd(text, i + 4)
                val fullKey = text.substring(i + 4, keyEnd)

                // Подсвечиваем ключ
                ElvalHighlighter.highlightMetadata(holder, baseOffset + i, baseOffset + keyEnd)

                i = keyEnd

                skipWhitespace(text, i).let { valueStart ->
                    if (valueStart < len && text[valueStart] != '\n' && text[valueStart] != '\r') {
                        if (fullKey == "in") {
                            handleInValue(text, valueStart, baseOffset, holder)
                        } else {
                            handleGenericValue(text, valueStart, baseOffset, holder)
                        }
                    }
                }
                continue
            }
            i++
        }
    }

    private fun handleInValue(text: String, start: Int, baseOffset: Int, holder: AnnotationHolder) {
        val inValueEnd = findWordEnd(text, start)
        ElvalHighlighter.highlightKeyword(holder, baseOffset + start, baseOffset + inValueEnd)

        skipWhitespace(text, inValueEnd).let { paramNameStart ->
            if (paramNameStart < text.length && text[paramNameStart] != '\n' && text[paramNameStart] != '\r') {
                val paramNameEnd = findWordEnd(text, paramNameStart)
                ElvalHighlighter.highlightString(holder, baseOffset + paramNameStart, baseOffset + paramNameEnd)
            }
        }
    }

    private fun handleGenericValue(text: String, start: Int, baseOffset: Int, holder: AnnotationHolder) {
        var valueEnd = text.length

        if (text[start] == '"') {
            val closeQuote = text.indexOf('"', start + 1)
            if (closeQuote != -1) {
                valueEnd = closeQuote + 1
            }
        } else {
            val nextAt = text.indexOf('@', start)
            if (nextAt != -1 && nextAt > start) {
                val beforeNextAt = skipWhitespaceBackwards(text, nextAt)
                if (beforeNextAt > start) {
                    valueEnd = beforeNextAt
                }
            }
        }

        ElvalHighlighter.highlightString(holder, baseOffset + start, baseOffset + valueEnd)
    }

    private fun findKeyEnd(text: String, start: Int): Int {
        var j = start
        while (j < text.length && (text[j].isLetterOrDigit() || text[j] == '.' || text[j] == '_')) {
            j++
        }
        return j
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

    private fun skipWhitespaceBackwards(text: String, pos: Int): Int {
        var j = pos - 1
        while (j >= 0 && text[j].isWhitespace()) {
            j--
        }
        return j + 1
    }
}
