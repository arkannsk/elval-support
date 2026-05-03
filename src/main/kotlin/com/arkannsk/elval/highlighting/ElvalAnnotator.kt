package com.arkannsk.elval.highlighting

import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement

class ElvalAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: com.intellij.lang.annotation.AnnotationHolder) {
        if (element !is PsiComment) return

        val text = element.text
        val baseOffset = element.textRange.startOffset

        if (!text.contains("@evl:") && !text.contains("@oa:")) return

        if (text.contains("@evl:")) {
            EvlAnnotationHandler.annotate(text, baseOffset, holder)
        }

        if (text.contains("@oa:")) {
            OaAnnotationHandler.annotate(text, baseOffset, holder)
        }
    }
}
