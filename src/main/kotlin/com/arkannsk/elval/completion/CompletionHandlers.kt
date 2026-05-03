package com.arkannsk.elval.completion

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement

class SimpleReplaceHandler(private val startOffset: Int, private val replacementText: String) :
    InsertHandler<LookupElement> {

    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        val editor = context.editor
        val currentCaretOffset = editor.caretModel.offset

        if (startOffset < 0 || startOffset > editor.document.textLength) return

        editor.document.deleteString(startOffset, currentCaretOffset)
        editor.document.insertString(startOffset, replacementText)
        editor.caretModel.moveToOffset(startOffset + replacementText.length)
    }
}