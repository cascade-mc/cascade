package dev.cascademc.cascade.screens.editor;

import dev.cascademc.cascade.script.LuaEnvironment;
import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorCoordinates;
import imgui.extension.texteditor.TextEditorLanguageDefinition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TextEditorPanel {
    private final TextEditor editor;
    private Path currentFilePath;
    private boolean fileDirty;

    public TextEditorPanel(EditorColorScheme colorScheme) {
        this.editor = new TextEditor();
        this.fileDirty = false;

        TextEditorLanguageDefinition lang = TextEditorLanguageDefinition.Lua();
        lang.setIdentifiers(LuaEnvironment.getIdentifierDocumentation());
        editor.setLanguageDefinition(lang);
        editor.setShowWhitespaces(false);

        colorScheme.applyToEditor(editor);
    }

    public void render() {
        ImGui.beginChild("Content", 0, 0, false);

        if (currentFilePath != null) {
            renderEditorToolbar();
            ImGui.separator();
            renderEditorButtons();
            ImGui.separator();
            editor.render("##editor");
        } else {
            ImGui.text("Select a script from the left to begin editing.");
        }

        ImGui.endChild();
    }

    private void renderEditorToolbar() {
        ImGui.text(currentFilePath.getFileName().toString());

        ImGui.sameLine();
        TextEditorCoordinates pos = editor.getCursorPosition();
        ImGui.text(" | Line " + (pos.mLine + 1) + ", Col " + (pos.mColumn + 1));

        if (editor.isTextChanged()) {
            fileDirty = true;
        }

        if (fileDirty) {
            ImGui.sameLine();
            ImGui.textColored(1f, 0.5f, 0.2f, 1f, "*");
        }
    }

    private void renderEditorButtons() {
        if (ImGui.button("Save")) {
            saveFile();
        }

        ImGui.sameLine();
        if (ImGui.button("Undo") && editor.canUndo()) {
            editor.undo(1);
        }

        ImGui.sameLine();
        if (ImGui.button("Redo") && editor.canRedo()) {
            editor.redo(1);
        }
    }

    public void loadFile(Path path) {
        try {
            currentFilePath = path;
            String content = Files.readString(path);
            editor.setText(content);
            fileDirty = false;
        } catch (IOException e) {
            editor.setText("-- Failed to load file");
            e.printStackTrace();
        }
    }

    private void saveFile() {
        if (currentFilePath == null) return;

        try {
            Files.writeString(currentFilePath, editor.getText());
            fileDirty = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}