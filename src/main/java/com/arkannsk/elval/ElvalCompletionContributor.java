package com.arkannsk.elval;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ElvalCompletionContributor extends CompletionContributor {

    private static final List<String> EVL_DIRECTIVES = Arrays.asList("validate", "decor", "rewrite");

    private static final Map<String, String> VALIDATE_PARAMS = new HashMap<>();
    static {
        VALIDATE_PARAMS.put("required", "Required field");
        VALIDATE_PARAMS.put("min:", "Min value/length");
        VALIDATE_PARAMS.put("max:", "Max value/length");
        VALIDATE_PARAMS.put("len:", "Exact length");
        VALIDATE_PARAMS.put("gt:", "Greater than");
        VALIDATE_PARAMS.put("lt:", "Less than");
        VALIDATE_PARAMS.put("pattern:", "Regex pattern");
        VALIDATE_PARAMS.put("email", "Valid email");
        VALIDATE_PARAMS.put("url", "Valid URL");
        VALIDATE_PARAMS.put("uuid", "Valid UUID");
    }

    private static final Map<String, String> OA_KEYS = new HashMap<>();
    static {
        OA_KEYS.put("title", "Field title");
        OA_KEYS.put("description", "Field description");
        OA_KEYS.put("format", "Data format");
        OA_KEYS.put("example", "Example value");
        OA_KEYS.put("in", "Location");
        OA_KEYS.put("minimum", "Min number");
        OA_KEYS.put("maximum", "Max number");
        OA_KEYS.put("type", "Data type");
        OA_KEYS.put("items", "Array items");
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();

        if (!(position instanceof PsiComment)) {
            return;
        }

        PsiComment comment = (PsiComment) position;
        String commentText = comment.getText();

        int cursorOffsetInFile = parameters.getOffset();
        int commentStartOffset = comment.getTextRange().getStartOffset();
        int relativeCursorPos = cursorOffsetInFile - commentStartOffset;

        if (relativeCursorPos > commentText.length()) {
            relativeCursorPos = commentText.length();
        }

        String textBeforeCursor = commentText.substring(0, relativeCursorPos);

        if (!textBeforeCursor.contains("@")) {
            return;
        }

        int lastEvIndex = textBeforeCursor.lastIndexOf("@evl:");
        int lastOaIndex = textBeforeCursor.lastIndexOf("@oa:");

        boolean isEvContext = lastEvIndex != -1 && (lastOaIndex == -1 || lastEvIndex > lastOaIndex);
        boolean isOaContext = lastOaIndex != -1 && (lastEvIndex == -1 || lastOaIndex > lastEvIndex);

        int keyStartOffset = -1;
        String suffixAfterKey = "";

        if (isEvContext) {
            keyStartOffset = commentStartOffset + lastEvIndex;
            suffixAfterKey = textBeforeCursor.substring(lastEvIndex + "@evl:".length());
        } else if (isOaContext) {
            keyStartOffset = commentStartOffset + lastOaIndex;
            suffixAfterKey = textBeforeCursor.substring(lastOaIndex + "@oa:".length());
        }

        String trimmedSuffix = suffixAfterKey.trim();

        // --- ЛОГИКА ДЛЯ @evl ---
        if (isEvContext) {
            if (trimmedSuffix.startsWith("validate")) {
                String paramsPart = trimmedSuffix.substring("validate".length()).trim();

                for (Map.Entry<String, String> entry : VALIDATE_PARAMS.entrySet()) {
                    String param = entry.getKey();
                    String hint = entry.getValue();

                    if (param.startsWith(paramsPart)) {
                        String insertText = param.endsWith(":") || param.equals("required") ? param : param + " ";
                        int paramStartOffset = cursorOffsetInFile - paramsPart.length();

                        LookupElementBuilder builder = LookupElementBuilder.create(insertText)
                                .withLookupString(param)
                                .withTailText(" (" + hint + ")")
                                .bold();

                        result.addElement(builder.withInsertHandler(new SimpleReplaceInsertHandler(paramStartOffset, insertText)));
                    }
                }
                result.stopHere();
                return;
            }

            for (String directive : EVL_DIRECTIVES) {
                if (directive.startsWith(trimmedSuffix)) {
                    // Пропускаем только если после директивы стоит пробел (значит она уже выбрана и пользователь перешел к параметрам)
                    if (trimmedSuffix.startsWith(directive + " ")) continue;

                    String fullText = "@evl:" + directive + " ";
                    LookupElementBuilder builder = LookupElementBuilder.create(fullText)
                            .withLookupString(directive)
                            .withTailText(" (ElVal)")
                            .bold();

                    result.addElement(builder.withInsertHandler(new ContextReplacingInsertHandler(keyStartOffset, fullText)));
                }
            }
            result.stopHere();

            // --- ЛОГИКА ДЛЯ @oa ---
        } else if (isOaContext) {
            boolean addedSomething = false;

            for (Map.Entry<String, String> entry : OA_KEYS.entrySet()) {
                String key = entry.getKey();
                String hint = entry.getValue();

                if (key.startsWith(trimmedSuffix)) {
                    String insertText = key + " ";
                    // Заменяем только ту часть, которую пользователь начал вводить после @oa:
                    int paramStartOffset = cursorOffsetInFile - trimmedSuffix.length();

                    LookupElementBuilder builder = LookupElementBuilder.create(insertText)
                            .withLookupString(key)
                            .withTailText(" (" + hint + ")")
                            .bold();

                    result.addElement(builder.withInsertHandler(new SimpleReplaceInsertHandler(paramStartOffset, insertText)));
                    addedSomething = true;
                }
            }

            if (addedSomething) {
                result.stopHere();
            }
        }
        // --- ЛОГИКА ДЛЯ ПРОСТОГО @ ---
        else if (textBeforeCursor.endsWith("@")) {
            int atSymbolOffset = commentStartOffset + textBeforeCursor.lastIndexOf("@");

            result.addElement(LookupElementBuilder.create("@evl:")
                    .withLookupString("evl:")
                    .withTailText(" (ElVal)")
                    .bold()
                    .withInsertHandler(new ContextReplacingInsertHandler(atSymbolOffset, "@evl:")));

            result.addElement(LookupElementBuilder.create("@oa:")
                    .withLookupString("oa:")
                    .withTailText(" (OpenAPI)")
                    .bold()
                    .withInsertHandler(new ContextReplacingInsertHandler(atSymbolOffset, "@oa:")));

            result.stopHere();
        }
    }

    private static class SimpleReplaceInsertHandler implements InsertHandler<LookupElement> {
        private final int startOffset;
        private final String replacementText;
        public SimpleReplaceInsertHandler(int startOffset, String replacementText) {
            this.startOffset = startOffset;
            this.replacementText = replacementText;
        }
        @Override
        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
            Editor editor = context.getEditor();
            int currentCaretOffset = editor.getCaretModel().getOffset();
            editor.getDocument().deleteString(startOffset, currentCaretOffset);
            editor.getDocument().insertString(startOffset, replacementText);
            editor.getCaretModel().moveToOffset(startOffset + replacementText.length());
        }
    }

    private static class ContextReplacingInsertHandler implements InsertHandler<LookupElement> {
        private final int startOffset;
        private final String replacementText;
        public ContextReplacingInsertHandler(int startOffset, String replacementText) {
            this.startOffset = startOffset;
            this.replacementText = replacementText;
        }
        @Override
        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
            Editor editor = context.getEditor();
            int currentCaretOffset = editor.getCaretModel().getOffset();
            editor.getDocument().deleteString(startOffset, currentCaretOffset);
            editor.getDocument().insertString(startOffset, replacementText);
            editor.getCaretModel().moveToOffset(startOffset + replacementText.length());
        }
    }
}
