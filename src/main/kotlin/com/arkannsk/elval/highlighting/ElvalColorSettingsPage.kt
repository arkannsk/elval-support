package com.arkannsk.elval.highlighting

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
        // Ключи атрибутов
        val ELVAL_KEYWORD = TextAttributesKey.createTextAttributesKey(
            "ELVAL_KEYWORD",
            DefaultLanguageHighlighterColors.KEYWORD
        )

        val ELVAL_PARAM_NAME = TextAttributesKey.createTextAttributesKey(
            "ELVAL_PARAM_NAME",
            DefaultLanguageHighlighterColors.FUNCTION_CALL
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
            AttributesDescriptor("ElVal - Directive (@evl:validate, @evl:decor)", ELVAL_KEYWORD),
            AttributesDescriptor("ElVal - Parameter Name (min:, max:, pattern:, contains:)", ELVAL_PARAM_NAME),
            AttributesDescriptor("ElVal - Parameter Value (required, email, 10, README)", ELVAL_PARAM_VALUE),
            AttributesDescriptor("OpenAPI - Annotation (@oa:title, @oa:in, @oa:description)", OA_ANNOTATION),
        )
    }

    override fun getDisplayName(): String = "ElVal & OpenAPI Annotations"

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
        return """
            type Document struct {
                // <KEYWORD>@evl:validate</KEYWORD> <PARAM_NAME>required</PARAM_NAME>
                // <KEYWORD>@evl:validate</KEYWORD> <PARAM_NAME>contains:</PARAM_NAME><PARAM_VALUE>README</PARAM_VALUE>
                Name string
                
                // <KEYWORD>@evl:validate</KEYWORD> <PARAM_NAME>starts_with:</PARAM_NAME><PARAM_VALUE>https://</PARAM_VALUE>
                // <KEYWORD>@evl:validate</KEYWORD> <PARAM_NAME>ends_with:</PARAM_NAME><PARAM_VALUE>.com</PARAM_VALUE>
                URL string
                
                // <KEYWORD>@evl:decor</KEYWORD> <PARAM_VALUE>uuid-gen</PARAM_VALUE>
                ID string
                
                // <OA>@oa:in</OA> <PARAM_VALUE>query</PARAM_VALUE> page
                // <OA>@oa:description</OA> <PARAM_VALUE>"Page number"</PARAM_VALUE>
                Page int
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
