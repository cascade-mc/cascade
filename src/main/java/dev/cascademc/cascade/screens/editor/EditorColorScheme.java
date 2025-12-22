package dev.cascademc.cascade.screens.editor;

import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.flag.TextEditorPaletteIndex;

public class EditorColorScheme {
    private static final int KEYWORD = 0xC678DD;
    private static final int IDENTIFIER = 0xE06C75;
    private static final int KNOWN_IDENTIFIER = 0x61AFEF;
    private static final int STRING = 0x98C379;
    private static final int PUNCTUATION = 0xABB2BF;
    private static final int CURSOR = 0xB7BECC;
    private static final int NUMBER = 0xD19A66;
    private static final int COMMENT = 0x5C6370;
    private static final int PREPROCESSOR = 0xC678DD;
    private static final int BACKGROUND = 0x18181E;
    private static final int SELECTION = 0x292D35;
    private static final int LINE_NUMBER = 0x4B5263;
    private static final int CURRENT_LINE_FILL = 0x21252D;
    private static final int CURRENT_LINE_FILL_INACTIVE = 0x252A33;
    private static final int CURRENT_LINE_EDGE = 0x292D35;
    private static final int ERROR_MARKER = 0xE06C75;
    private static final int BREAKPOINT = 0x61AFEF;

    public void applyToEditor(TextEditor editor) {
        int[] palette = editor.getPalette();

        palette[TextEditorPaletteIndex.Keyword] = rgbToAbgr(KEYWORD);
        palette[TextEditorPaletteIndex.Identifier] = rgbToAbgr(IDENTIFIER);
        palette[TextEditorPaletteIndex.KnownIdentifier] = rgbToAbgr(KNOWN_IDENTIFIER);
        palette[TextEditorPaletteIndex.String] = rgbToAbgr(STRING);
        palette[TextEditorPaletteIndex.Punctuation] = rgbToAbgr(PUNCTUATION);
        palette[TextEditorPaletteIndex.Cursor] = rgbToAbgr(CURSOR);
        palette[TextEditorPaletteIndex.Number] = rgbToAbgr(NUMBER);
        palette[TextEditorPaletteIndex.Comment] = rgbToAbgr(COMMENT);
        palette[TextEditorPaletteIndex.Preprocessor] = rgbToAbgr(PREPROCESSOR);
        palette[TextEditorPaletteIndex.Background] = rgbToAbgr(BACKGROUND);
        palette[TextEditorPaletteIndex.Selection] = rgbToAbgr(SELECTION);
        palette[TextEditorPaletteIndex.LineNumber] = rgbToAbgr(LINE_NUMBER);
        palette[TextEditorPaletteIndex.CurrentLineFill] = rgbToAbgr(CURRENT_LINE_FILL);
        palette[TextEditorPaletteIndex.CurrentLineFillInactive] = rgbToAbgr(CURRENT_LINE_FILL_INACTIVE);
        palette[TextEditorPaletteIndex.CurrentLineEdge] = rgbToAbgr(CURRENT_LINE_EDGE);
        palette[TextEditorPaletteIndex.ErrorMarker] = rgbToAbgr(ERROR_MARKER);
        palette[TextEditorPaletteIndex.Breakpoint] = rgbToAbgr(BREAKPOINT);

        editor.setPalette(palette);
    }

    private int rgbToAbgr(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int a = 0xFF;

        return (a << 24) | (b << 16) | (g << 8) | r;
    }
}