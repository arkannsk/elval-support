package com.arkannsk.elval

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement

class ElvalAnnotator : Annotator {

    // Регулярные выражения Kotlin
    private val keywordPattern = Regex("@evl:(validate|decor|rewrite)\\b")
    private val paramKvPattern = Regex("\\b(min|max|len|pattern|contains|starts_with|ends_with|enum|url|http_url|dsn|gt|gte|lt|lte|eq|neq):([^\\s]+)")
    private val paramSimplePattern = Regex("\\b(required|optional|not-zero|url|http_url|email|uuid|phone|ip)\\b")
    private val decorValuePattern = Regex("@evl:decor\\s+([\\w-]+)")
    private val oaQuotedPattern = Regex("@oa:([a-zA-Z.]+)\\s+\"([^\"]*)\"")
    private val oaSimplePattern = Regex("@oa:([a-zA-Z.]+)\\s+(\\S+)")

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is PsiComment) return

        val text = element.text
        val baseOffset = element.textRange.startOffset

        if (!text.contains("@evl:") && !text.contains("@oa:")) return

        // --- 1. Обработка @evl:decor ---
        decorValuePattern.findAll(text).forEach { match ->
            // Красим @evl:decor
            highlight(holder, baseOffset + match.range.first, baseOffset + match.range.first + "@evl:decor".length, ElvalColorSettingsPage.ELVAL_KEYWORD)
            // Красим значение (uuid-gen)
            val valueGroup = match.groups[1] ?: return@forEach
            highlight(holder, baseOffset + valueGroup.range.first, baseOffset + valueGroup.range.last + 1, ElvalColorSettingsPage.ELVAL_PARAM_VALUE)
        }

        // --- 2. Обработка @evl:validate / @evl:rewrite ---
        keywordPattern.findAll(text).forEach { match ->
            val fullMatch = match.value
            if (fullMatch.startsWith("@evl:decor")) return@forEach // Пропускаем, если это декоратор

            // Красим ключевое слово (@evl:validate)
            highlight(holder, baseOffset + match.range.first, baseOffset + match.range.last + 1, ElvalColorSettingsPage.ELVAL_KEYWORD)

            // Ищем параметры после ключевого слова
            val afterKeywordEnd = match.range.last + 1
            if (afterKeywordEnd >= text.length) return@forEach

            // Ищем KV параметр (например, min:10)
            val kvMatch = paramKvPattern.find(text, afterKeywordEnd)
            if (kvMatch != null) {
                val keyGroup = kvMatch.groups[1] ?: return@forEach
                val valueGroup = kvMatch.groups[2] ?: return@forEach

                highlight(holder, baseOffset + keyGroup.range.first, baseOffset + keyGroup.range.last + 1, ElvalColorSettingsPage.ELVAL_PARAM_NAME)
                highlight(holder, baseOffset + valueGroup.range.first, baseOffset + valueGroup.range.last + 1, ElvalColorSettingsPage.ELVAL_PARAM_VALUE)
                return@forEach // Переходим к следующему ключевому слову
            }

            // Ищем простой параметр (например, required)
            val simpleMatch = paramSimplePattern.find(text, afterKeywordEnd)
            if (simpleMatch != null) {
                highlight(holder, baseOffset + simpleMatch.range.first, baseOffset + simpleMatch.range.last + 1, ElvalColorSettingsPage.ELVAL_PARAM_VALUE)
            }
        }

        // --- 3. Обработка @oa ---

        // Сначала обрабатываем значения в кавычках
        oaQuotedPattern.findAll(text).forEach { match ->
            val keyGroup = match.groups[1] ?: return@forEach
            val valueGroup = match.groups[2] ?: return@forEach

            // Красим ключ @oa:title
            highlight(holder, baseOffset + match.range.first, baseOffset + keyGroup.range.last + 1, ElvalColorSettingsPage.OA_ANNOTATION)

            // Красим значение внутри кавычек
            highlight(holder, baseOffset + valueGroup.range.first, baseOffset + valueGroup.range.last + 1, ElvalColorSettingsPage.ELVAL_PARAM_VALUE)
        }

        // Затем обрабатываем простые значения (без кавычек)
        oaSimplePattern.findAll(text).forEach { match ->
            val keyGroup = match.groups[1] ?: return@forEach
            val valueGroup = match.groups[2] ?: return@forEach

            // Красим ключ
            highlight(holder, baseOffset + match.range.first, baseOffset + keyGroup.range.last + 1, ElvalColorSettingsPage.OA_ANNOTATION)

            // Красим значение
            highlight(holder, baseOffset + valueGroup.range.first, baseOffset + valueGroup.range.last + 1, ElvalColorSettingsPage.ELVAL_PARAM_VALUE)
        }
    }

    private fun highlight(holder: AnnotationHolder, start: Int, end: Int, key: TextAttributesKey) {
        if (start >= end) return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(TextRange(start, end))
            .textAttributes(key)
            .create()
    }
}
