package com.arkannsk.elval;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.lexer.EmptyLexer;
import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ElvalColorSettingsPage implements ColorSettingsPage {

    public static final TextAttributesKey ELVAL_KEYWORD =
            TextAttributesKey.createTextAttributesKey("ELVAL_KEYWORD",
                    DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey ELVAL_PARAM_NAME =
            TextAttributesKey.createTextAttributesKey("ELVAL_PARAM_NAME",
                    DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey ELVAL_PARAM_VALUE =
            TextAttributesKey.createTextAttributesKey("ELVAL_PARAM_VALUE",
                    DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey OA_ANNOTATION =
            TextAttributesKey.createTextAttributesKey("OA_ANNOTATION",
                    DefaultLanguageHighlighterColors.METADATA);

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("ElVal - Keyword (@evl:validate, @evl:decor)", ELVAL_KEYWORD),
            new AttributesDescriptor("ElVal - Parameter Name (min, max, pattern)", ELVAL_PARAM_NAME),
            new AttributesDescriptor("ElVal - Parameter Value (required, 10, email)", ELVAL_PARAM_VALUE),
            new AttributesDescriptor("OpenAPI Annotation (@oa:title, @oa:description)", OA_ANNOTATION),
    };

    @Override
    public @NotNull String getDisplayName() {
        return "ElVal Annotations";
    }

    @Override
    public @Nullable Icon getIcon() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new SyntaxHighlighterBase() {
            @NotNull
            @Override
            public Lexer getHighlightingLexer() {
                return new EmptyLexer();
            }

            @NotNull
            @Override
            public TextAttributesKey[] getTokenHighlights(@NotNull IElementType tokenType) {
                return TextAttributesKey.EMPTY_ARRAY;
            }
        };
    }

    @NotNull
    @Override
    public String getDemoText() {
        // Используем теги, которые мы мапим ниже.
        // Текст должен выглядеть как Go код с комментариями
        return """
                type User struct {
                    // <KEYWORD>@evl:validate</KEYWORD> <PARAM_NAME>min</PARAM_NAME>:<PARAM_VALUE>3</PARAM_VALUE> <PARAM_NAME>max</PARAM_NAME>:<PARAM_VALUE>50</PARAM_VALUE>
                    Name string
                   \s
                    // <KEYWORD>@evl:validate</KEYWORD> <PARAM_NAME>pattern</PARAM_NAME>:<PARAM_VALUE>email</PARAM_VALUE>
                    // <OA>@oa:title</OA> <PARAM_VALUE>"User Email"</PARAM_VALUE>
                    Email string
                   \s
                    // <KEYWORD>@evl:decor</KEYWORD> <PARAM_VALUE>uuid-gen</PARAM_VALUE>
                    ID string
                }
               \s""";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        Map<String, TextAttributesKey> map = new HashMap<>();
        map.put("KEYWORD", ELVAL_KEYWORD);
        map.put("PARAM_NAME", ELVAL_PARAM_NAME);
        map.put("PARAM_VALUE", ELVAL_PARAM_VALUE);
        map.put("OA", OA_ANNOTATION);
        return map;
    }
}
