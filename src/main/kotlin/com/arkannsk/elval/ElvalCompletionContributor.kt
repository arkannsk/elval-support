package com.arkannsk.elval

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiComment

class ElvalCompletionContributor : CompletionContributor() {

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val position = parameters.position

        if (position !is PsiComment) return

        val commentText = position.text
        val cursorOffsetInFile = parameters.offset
        val commentStartOffset = position.textRange.startOffset
        val relativeCursorPos = cursorOffsetInFile - commentStartOffset

        if (relativeCursorPos > commentText.length) return

        val textBeforeCursor = commentText.substring(0, relativeCursorPos)

        if (!textBeforeCursor.contains("@")) return

        val lastEvIndex = textBeforeCursor.lastIndexOf("@evl:")
        val lastOaIndex = textBeforeCursor.lastIndexOf("@oa:")

        val isEvContext = lastEvIndex != -1 && (lastOaIndex == -1 || lastEvIndex > lastOaIndex)
        val isOaContext = lastOaIndex != -1 && (lastEvIndex == -1 || lastOaIndex > lastEvIndex)

        var keyStartOffset = -1
        var suffixAfterKey = ""

        if (isEvContext) {
            keyStartOffset = commentStartOffset + lastEvIndex
            suffixAfterKey = textBeforeCursor.substring(lastEvIndex + "@evl:".length)
        } else if (isOaContext) {
            keyStartOffset = commentStartOffset + lastOaIndex
            suffixAfterKey = textBeforeCursor.substring(lastOaIndex + "@oa:".length)
        }

        val trimmedSuffix = suffixAfterKey.trim()

        if (isEvContext) {
            EvllCompletionProvider.fillCompletions(result, keyStartOffset, cursorOffsetInFile, trimmedSuffix)
            result.stopHere()

        } else if (isOaContext) {
            OpenApiCompletionProvider.fillCompletions(result, cursorOffsetInFile, trimmedSuffix)

        } else if (textBeforeCursor.endsWith("@")) {
            val atSymbolOffset = commentStartOffset + textBeforeCursor.lastIndexOf("@")

            result.addElement(LookupElementBuilder.create("@evl:")
                .withLookupString("evl:")
                .withTailText(" (ElVal)")
                .bold()
                .withInsertHandler(ContextReplaceHandler(atSymbolOffset, "@evl:")))

            result.addElement(LookupElementBuilder.create("@oa:")
                .withLookupString("oa:")
                .withTailText(" (OpenAPI)")
                .bold()
                .withInsertHandler(ContextReplaceHandler(atSymbolOffset, "@oa:")))

            result.stopHere()
        }
    }

    // Выносим хендлеры на уровень файла или делаем их internal/public, чтобы они были видны
    internal class ContextReplaceHandler(private val startOffset: Int, private val replacementText: String) :
        InsertHandler<LookupElement> {

        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val editor = context.editor
            val currentCaretOffset = editor.caretModel.offset
            editor.document.deleteString(startOffset, currentCaretOffset)
            editor.document.insertString(startOffset, replacementText)
            editor.caretModel.moveToOffset(startOffset + replacementText.length)
        }
    }
}
