package com.arkannsk.elval

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder

object EvllCompletionProvider {

    fun fillCompletions(
        result: CompletionResultSet,
        keyStartOffset: Int,
        cursorOffsetInFile: Int,
        trimmedSuffix: String
    ) {
        // 1. Параметры validate
        if (trimmedSuffix.startsWith("validate")) {
            val paramsPart = trimmedSuffix.substringAfter("validate").trim()

            ElvalConstants.VALIDATE_PARAMS.forEach { (param, hint) ->
                if (param.startsWith(paramsPart)) {
                    val insertText = if (param.endsWith(":") || param == "required") param else "$param "
                    val paramStartOffset = cursorOffsetInFile - paramsPart.length

                    val builder = LookupElementBuilder.create(insertText)
                        .withLookupString(param)
                        .withTailText(" ($hint)")
                        .bold()

                    result.addElement(builder.withInsertHandler(SimpleReplaceHandler(paramStartOffset, insertText)))
                }
            }
            return
        }

        // 2. Директивы evl
        ElvalConstants.EVL_DIRECTIVES.forEach { directive ->
            if (directive.startsWith(trimmedSuffix)) {
                if (trimmedSuffix.startsWith("$directive ")) return@forEach

                val fullText = "@evl:$directive "
                val builder = LookupElementBuilder.create(fullText)
                    .withLookupString(directive)
                    .withTailText(" (ElVal)")
                    .bold()

                result.addElement(builder.withInsertHandler(ContextReplaceHandler(keyStartOffset, fullText)))
            }
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
