package com.arkannsk.elval;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElvalAnnotator implements Annotator {

    // 1. Ключевые слова: @evl:validate, @evl:decor, @evl:rewrite
    private static final Pattern KEYWORD_PATTERN =
            Pattern.compile("@evl:(validate|decor|rewrite)\\b");

    // 2. Параметры key:value (min:10, pattern:email)
    // Группа 1: имя (min), Группа 2: значение (10)
    private static final Pattern PARAM_KV_PATTERN =
            Pattern.compile("\\b(min|max|len|pattern|contains|starts_with|ends_with|enum|url|http_url|dsn|gt|gte|lt|lte|eq|neq):([^\\s]+)");

    // 3. Простые параметры (required, url)
    private static final Pattern PARAM_SIMPLE_PATTERN =
            Pattern.compile("\\b(required|optional|not-zero|url|http_url|email|uuid|phone|ip)\\b");

    // 4. Декораторы: @evl:decor uuid-gen
    // Группа 1: значение (uuid-gen)
    private static final Pattern DECOR_VALUE_PATTERN =
            Pattern.compile("@evl:decor\\s+([\\w-]+)");

    // 5. OpenAPI: @oa:title "Value" или @oa:title Value
    // Группа 1: ключ (title)
    // Группа 2: значение в кавычках (без кавычек)
    // Группа 3: ключ без кавычек (если нет группы 2) - мы используем альтернативу
    // Лучше использовать два разных подхода или один универсальный.
    // Универсальный: @oa:key\s+"value" ИЛИ @oa:key\s+value
    private static final Pattern OA_QUOTED_PATTERN =
            Pattern.compile("@oa:([a-zA-Z.]+)\\s+\"([^\"]*)\"");

    private static final Pattern OA_SIMPLE_PATTERN =
            Pattern.compile("@oa:([a-zA-Z.]+)\\s+(\\S+)");

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof PsiComment)) {
            return;
        }

        String text = element.getText();
        int baseOffset = element.getTextRange().getStartOffset();

        if (!text.contains("@evl:") && !text.contains("@oa:")) {
            return;
        }

        // --- 1. Обработка @evl:decor ---
        Matcher decorMatcher = DECOR_VALUE_PATTERN.matcher(text);
        while (decorMatcher.find()) {
            // Красим @evl:decor
            highlight(holder, baseOffset + decorMatcher.start(), baseOffset + decorMatcher.start() + "@evl:decor".length(), ElvalColorSettingsPage.ELVAL_KEYWORD);
            // Красим значение (uuid-gen)
            highlight(holder, baseOffset + decorMatcher.start(1), baseOffset + decorMatcher.end(1), ElvalColorSettingsPage.ELVAL_PARAM_VALUE);
        }

        // --- 2. Обработка @evl:validate / @evl:rewrite ---
        Matcher keywordMatcher = KEYWORD_PATTERN.matcher(text);
        while (keywordMatcher.find()) {
            // Если это декоратор, пропускаем (он уже обработан выше)
            if (text.substring(keywordMatcher.start()).startsWith("@evl:decor")) {
                continue;
            }

            // Красим ключевое слово (@evl:validate)
            highlight(holder, baseOffset + keywordMatcher.start(), baseOffset + keywordMatcher.end(), ElvalColorSettingsPage.ELVAL_KEYWORD);

            // Ищем параметр после ключевого слова.
            // Так как мы хотим подсветить только ПЕРВЫЙ параметр после ключа (согласно правилу "одна аннотация - одна директива"),
            // мы берем подстроку после ключевого слова и ищем там.

            int afterKeywordEnd = keywordMatcher.end();

            // Проверяем, есть ли вообще что-то после ключа
            if (afterKeywordEnd >= text.length()) continue;

            // Ищем KV параметр (например, min:10)
            // Мы ищем его во всем тексте, но проверяем, что он идет сразу после пробела после ключа
            Matcher kvMatcher = PARAM_KV_PATTERN.matcher(text);
            if (kvMatcher.find(afterKeywordEnd)) {
                // Проверяем, что матч начинается сразу после возможных пробелов после ключа
                // Для простоты: если нашли match, и он близко к концу ключа, считаем его своим.
                // Но лучше проверить, что между ключом и параметром только пробелы.

                // Упрощенный вариант: просто красим первое вхождение параметра после ключа
                highlight(holder, baseOffset + kvMatcher.start(1), baseOffset + kvMatcher.end(1), ElvalColorSettingsPage.ELVAL_PARAM_NAME);
                highlight(holder, baseOffset + kvMatcher.start(2), baseOffset + kvMatcher.end(2), ElvalColorSettingsPage.ELVAL_PARAM_VALUE);
                continue; // Переходим к следующему ключевому слову
            }

            // Ищем простой параметр (например, required)
            Matcher simpleMatcher = PARAM_SIMPLE_PATTERN.matcher(text);
            if (simpleMatcher.find(afterKeywordEnd)) {
                highlight(holder, baseOffset + simpleMatcher.start(1), baseOffset + simpleMatcher.end(1), ElvalColorSettingsPage.ELVAL_PARAM_VALUE);
            }
        }

        // --- 3. Обработка @oa ---

        // Сначала обрабатываем значения в кавычках
        Matcher oaQuotedMatcher = OA_QUOTED_PATTERN.matcher(text);
        while (oaQuotedMatcher.find()) {
            // Красим ключ @oa:title
            highlight(holder, baseOffset + oaQuotedMatcher.start(), baseOffset + oaQuotedMatcher.end(1) + 1, ElvalColorSettingsPage.OA_ANNOTATION); // +1 чтобы захватить двоеточие? Нет, end(1) это конец группы 1.
            // Правильнее: start() до end(1) + длина "@oa:"? Нет.
            // start() -> начало @oa:...
            // end(1) -> конец слова title
            highlight(holder, baseOffset + oaQuotedMatcher.start(), baseOffset + oaQuotedMatcher.end(1), ElvalColorSettingsPage.OA_ANNOTATION);

            // Красим значение внутри кавычек
            highlight(holder, baseOffset + oaQuotedMatcher.start(2), baseOffset + oaQuotedMatcher.end(2), ElvalColorSettingsPage.ELVAL_PARAM_VALUE);
        }

        // Затем обрабатываем простые значения (без кавычек), но только если они НЕ были частью кавычек
        // Чтобы избежать дублирования, можно использовать более сложный подход, но для демо сойдет
        Matcher oaSimpleMatcher = OA_SIMPLE_PATTERN.matcher(text);
        while (oaSimpleMatcher.find()) {
            // Проверяем, не перекрыто ли это кавычками (грубая проверка)
            // Если группа 2 (кавычки) не найдена в этом месте... сложно.
            // Просто красим ключ
            highlight(holder, baseOffset + oaSimpleMatcher.start(), baseOffset + oaSimpleMatcher.end(1), ElvalColorSettingsPage.OA_ANNOTATION);
            // Красим значение
            highlight(holder, baseOffset + oaSimpleMatcher.start(2), baseOffset + oaSimpleMatcher.end(2), ElvalColorSettingsPage.ELVAL_PARAM_VALUE);
        }
    }

    private void highlight(@NotNull AnnotationHolder holder, int start, int end, @NotNull TextAttributesKey key) {
        if (start >= end) return;
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(new TextRange(start, end))
                .textAttributes(key)
                .create();
    }
}
