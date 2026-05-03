package com.arkannsk.elval.highlighting

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.util.TextRange

object ElvalHighlighter {

    fun highlightKeyword(holder: AnnotationHolder, start: Int, end: Int) {
        if (start >= end) return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(TextRange(start, end))
            .textAttributes(DefaultLanguageHighlighterColors.KEYWORD)
            .create()
    }

    fun highlightMetadata(holder: AnnotationHolder, start: Int, end: Int) {
        if (start >= end) return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(TextRange(start, end))
            .textAttributes(DefaultLanguageHighlighterColors.METADATA)
            .create()
    }

    fun highlightString(holder: AnnotationHolder, start: Int, end: Int) {
        if (start >= end) return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(TextRange(start, end))
            .textAttributes(DefaultLanguageHighlighterColors.STRING)
            .create()
    }

    fun highlightFunctionCall(holder: AnnotationHolder, start: Int, end: Int) {
        if (start >= end) return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(TextRange(start, end))
            .textAttributes(DefaultLanguageHighlighterColors.FUNCTION_CALL)
            .create()
    }
}
