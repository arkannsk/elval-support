package com.arkannsk.elval

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder

object OpenApiCompletionProvider {

    fun fillCompletions(
        result: CompletionResultSet,
        cursorOffsetInFile: Int,
        trimmedSuffix: String
    ) {
        var addedSomething = false

        ElvalConstants.OA_KEYS.forEach { (key, hint) ->
            if (key.startsWith(trimmedSuffix)) {
                val insertText = "$key "
                val paramStartOffset = cursorOffsetInFile - trimmedSuffix.length

                val builder = LookupElementBuilder.create(insertText)
                    .withLookupString(key)
                    .withTailText(" ($hint)")
                    .bold()

                result.addElement(builder.withInsertHandler(SimpleReplaceHandler(paramStartOffset, insertText)))
                addedSomething = true
            }
        }

        if (addedSomething) {
            result.stopHere()
        }
    }

    internal class SimpleReplaceHandler(private val startOffset: Int, private val replacementText: String) :
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
