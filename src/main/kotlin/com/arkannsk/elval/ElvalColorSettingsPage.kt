package com.arkannsk.elval

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.lexer.EmptyLexer
import com.intellij.lexer.Lexer
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NotNull
import javax.swing.Icon

class ElvalColorSettingsPage : ColorSettingsPage {

    companion object {
        // Создаем свои ключи атрибутов на основе стандартных цветов
        val ELVAL_KEYWORD = TextAttributesKey.createTextAttributesKey(
            "ELVAL_KEYWORD",
            DefaultLanguageHighlighterColors.KEYWORD
        )

        val ELVAL_PARAM_NAME = TextAttributesKey.createTextAttributesKey(
            "ELVAL_PARAM_NAME",
            DefaultLanguageHighlighterColors.NUMBER // Или FUNCTION_CALL, если нравится больше
        )

        val ELVAL_PARAM_VALUE = TextAttributesKey.createTextAttributesKey(
            "ELVAL_PARAM_VALUE",
            DefaultLanguageHighlighterColors.STRING
        )

        val OA_ANNOTATION = TextAttributesKey.createTextAttributesKey(
            "OA_ANNOTATION",
            DefaultLanguageHighlighterColors.METADATA
        )

        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("ElVal - Keyword (@evl:validate, @evl:decor)", ELVAL_KEYWORD),
            AttributesDescriptor("ElVal - Parameter Name (min, max, pattern)", ELVAL_PARAM_NAME),
            AttributesDescriptor("ElVal - Parameter Value (required, 10, email)", ELVAL_PARAM_VALUE),
            AttributesDescriptor("OpenAPI Annotation (@oa:title, @oa:description)", OA_ANNOTATION),
        )
    }

    override fun getDisplayName(): String = "ElVal Annotations"

    override fun getIcon(): Icon? = null

    @NotNull
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    @NotNull
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    @NotNull
    override fun getHighlighter(): SyntaxHighlighter {
        return object : SyntaxHighlighterBase() {
            @NotNull
            override fun getHighlightingLexer(): Lexer {
                return EmptyLexer()
            }

            @NotNull
            override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
                return TextAttributesKey.EMPTY_ARRAY
            }
        }
    }

    @NotNull
    override fun getDemoText(): String {
        // Используем теги <KEYWORD>, <PARAM_NAME> и т.д., которые мы замапим ниже
        return """
            type User struct {
                // <KEYWORD>@evl:validate</KEYWORD> <PARAM_NAME>min</PARAM_NAME>:<PARAM_VALUE>3</PARAM_VALUE> <PARAM_NAME>max</PARAM_NAME>:<PARAM_VALUE>50</PARAM_VALUE>
                Name string
                
                // <KEYWORD>@evl:validate</KEYWORD> <PARAM_NAME>pattern</PARAM_NAME>:<PARAM_VALUE>email</PARAM_VALUE>
                // <OA>@oa:title</OA> <PARAM_VALUE>"User Email"</PARAM_VALUE>
                Email string
                
                // <KEYWORD>@evl:decor</KEYWORD> <PARAM_VALUE>uuid-gen</PARAM_VALUE>
                ID string
            }
        """.trimIndent()
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? {
        return mapOf(
            "KEYWORD" to ELVAL_KEYWORD,
            "PARAM_NAME" to ELVAL_PARAM_NAME,
            "PARAM_VALUE" to ELVAL_PARAM_VALUE,
            "OA" to OA_ANNOTATION
        )
    }
}
