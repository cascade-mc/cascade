package dev.cascademc.cascade.screens;

import dev.cascademc.cascade.editor.ScriptDirectoryNode;
import dev.cascademc.cascade.editor.ScriptNode;
import dev.cascademc.cascade.editor.ScriptTreeBuilder;
import dev.cascademc.cascade.imgui.ImGuiImpl;
import dev.cascademc.cascade.imgui.RenderInterface;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorCoordinates;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.extension.texteditor.flag.TextEditorPaletteIndex;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TestScreen extends Screen implements RenderInterface {

    private ScriptDirectoryNode scriptRoot;
    private ScriptNode selectedNode;

    private Path currentFilePath;
    private boolean fileDirty = false;

    private final TextEditor editor = new TextEditor();

    public TestScreen() {
        super(Component.literal("Script Editor"));
        setupEditor();
        setupEditorColors();
    }

    private void setupEditor() {
        TextEditorLanguageDefinition lang = TextEditorLanguageDefinition.Lua();

        Map<String, String> identifierMap = new HashMap<>();

        identifierMap.put("chat.print", "Print a message to chat\nchat.print(message: string)");
        identifierMap.put("chat.clear", "Clear the chat\nchat.clear()");

        lang.setIdentifiers(identifierMap);

        editor.setLanguageDefinition(lang);
        editor.setShowWhitespaces(false);
    }

    private void setupEditorColors() {
        int[] palette = editor.getPalette();

        palette[TextEditorPaletteIndex.Keyword] = rgbToAbgr(0xC678DD);
        palette[TextEditorPaletteIndex.Identifier] = rgbToAbgr(0xE06C75);
        palette[TextEditorPaletteIndex.KnownIdentifier] = rgbToAbgr(0x61AFEF);
        palette[TextEditorPaletteIndex.String] = rgbToAbgr(0x98C379);
        palette[TextEditorPaletteIndex.Punctuation] = rgbToAbgr(0xABB2BF);
        palette[TextEditorPaletteIndex.Cursor] = rgbToAbgr(0xB7BECC);

        palette[TextEditorPaletteIndex.Number] = rgbToAbgr(0xD19A66);
        palette[TextEditorPaletteIndex.Comment] = rgbToAbgr(0x5C6370);
        palette[TextEditorPaletteIndex.Preprocessor] = rgbToAbgr(0xC678DD);

        palette[TextEditorPaletteIndex.Background] = rgbToAbgr(0x18181E);
        palette[TextEditorPaletteIndex.Selection] = rgbToAbgr(0x292D35);
        palette[TextEditorPaletteIndex.LineNumber] = rgbToAbgr(0x4B5263);
        palette[TextEditorPaletteIndex.CurrentLineFill] = rgbToAbgr(0x21252D);
        palette[TextEditorPaletteIndex.CurrentLineFillInactive] = rgbToAbgr(0x252A33);
        palette[TextEditorPaletteIndex.CurrentLineEdge] = rgbToAbgr(0x292D35);

        palette[TextEditorPaletteIndex.ErrorMarker] = rgbToAbgr(0xE06C75);
        palette[TextEditorPaletteIndex.Breakpoint] = rgbToAbgr(0x61AFEF);

        editor.setPalette(palette);
    }

    private int rgbToAbgr(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int a = 0xFF;

        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    @Override
    protected void init() {
        super.init();

        try {
            Path scriptsDir = FabricLoader.getInstance()
                    .getGameDir()
                    .resolve("cascade")
                    .resolve("scripts");

            Files.createDirectories(scriptsDir);
            scriptRoot = ScriptTreeBuilder.build(scriptsDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renderNode(ScriptNode node) {
        if (node.isDirectory()) {
            boolean open = ImGui.treeNodeEx(
                    node.getName(),
                    ImGuiTreeNodeFlags.OpenOnArrow
                            | ImGuiTreeNodeFlags.SpanFullWidth
            );

            if (open) {
                for (ScriptNode child : ((ScriptDirectoryNode) node).getChildren()) {
                    renderNode(child);
                }
                ImGui.treePop();
            }
        } else {
            int flags = ImGuiTreeNodeFlags.Leaf
                    | ImGuiTreeNodeFlags.NoTreePushOnOpen
                    | ImGuiTreeNodeFlags.SpanFullWidth;

            if (node == selectedNode) {
                flags |= ImGuiTreeNodeFlags.Selected;
            }

            ImGui.treeNodeEx(node.getName(), flags);

            if (ImGui.isItemClicked()) {
                selectedNode = node;
                loadFile(node.getPath());
            }
        }
    }

    private void loadFile(Path path) {
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

    @Override
    public void render(ImGuiIO io) {
        ImGui.pushFont(ImGuiImpl.getDefaultFont());

        ImGui.setNextWindowPos(50, 50, imgui.flag.ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(1200, 700, imgui.flag.ImGuiCond.FirstUseEver);

        int windowFlags = ImGuiWindowFlags.NoCollapse;

        if (ImGui.begin("Script Editor", windowFlags)) {

            ImGui.beginChild("Sidebar", 250, 0, true);
            {
                ImGui.text("Scripts");
                ImGui.separator();

                ImGui.pushStyleVar(imgui.flag.ImGuiStyleVar.IndentSpacing, 10.0f);

                if (scriptRoot != null) {
                    for (ScriptNode child : scriptRoot.getChildren()) {
                        renderNode(child);
                    }
                }

                ImGui.popStyleVar();
            }
            ImGui.endChild();

            ImGui.sameLine();

            ImGui.beginChild("Content", 0, 0, false);
            {
                if (currentFilePath != null) {
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

                    ImGui.separator();

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

                    ImGui.separator();

                    editor.render("##editor");

                } else {
                    ImGui.text("Select a script from the left to begin editing.");
                }
            }
            ImGui.endChild();

            ImGui.end();
        }

        ImGui.popFont();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}