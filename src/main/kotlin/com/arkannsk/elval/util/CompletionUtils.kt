package com.arkannsk.elval.util

import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import javax.swing.Icon

object CompletionUtils {

    /**
     * Создает элемент автодополнения с высоким приоритетом (1000.0).
     * Возвращает LookupElement, который может быть обернут в PrioritizedLookupElement внутри.
     */
    fun createPrioritizedElement(
        lookupString: String,
        insertText: String,
        startOffset: Int,
        hint: String? = null,
        icon: Icon = AllIcons.Nodes.Parameter
    ): LookupElement { // <-- Изменили тип возврата на LookupElement

        var builder = LookupElementBuilder.create(insertText)
            .withLookupString(lookupString)
            .bold()
            .withIcon(icon)
            .withInsertHandler(SimpleReplaceHandler(startOffset, insertText))

        if (!hint.isNullOrEmpty()) {
            builder = builder.withTailText(" ($hint)", true)
        }

        // withPriority возвращает LookupElement, который внутри является PrioritizedLookupElement
        return PrioritizedLookupElement.withPriority(builder, 1000.0)
    }
}
