package com.arkannsk.elval.highlighting

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange

object ElvalHighlighter {

    fun highlightKeyword(holder: AnnotationHolder, start: Int, end: Int) {
        if (start >= end) return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(TextRange(start, end))
            .textAttributes(ElvalColorSettingsPage.ELVAL_KEYWORD)
            .create()
    }

    fun highlightFunctionCall(holder: AnnotationHolder, start: Int, end: Int) {
        if (start >= end) return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(TextRange(start, end))
            .textAttributes(ElvalColorSettingsPage.ELVAL_PARAM_NAME)
            .create()
    }

    fun highlightString(holder: AnnotationHolder, start: Int, end: Int) {
        if (start >= end) return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(TextRange(start, end))
            .textAttributes(ElvalColorSettingsPage.ELVAL_PARAM_VALUE)
            .create()
    }

    fun highlightMetadata(holder: AnnotationHolder, start: Int, end: Int) {
        if (start >= end) return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(TextRange(start, end))
            .textAttributes(ElvalColorSettingsPage.OA_ANNOTATION)
            .create()
    }
}
