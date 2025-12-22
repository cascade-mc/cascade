package dev.cascademc.cascade.screens;

import dev.cascademc.cascade.imgui.ImGuiImpl;
import dev.cascademc.cascade.imgui.RenderInterface;
import dev.cascademc.cascade.screens.editor.EditorColorScheme;
import dev.cascademc.cascade.screens.editor.FileTreePanel;
import dev.cascademc.cascade.screens.editor.TextEditorPanel;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Path;

public class ScriptEditorScreen extends Screen implements RenderInterface {

    private final FileTreePanel fileTreePanel;
    private final TextEditorPanel textEditorPanel;

    public ScriptEditorScreen() {
        super(Component.literal("Script Editor"));

        Path scriptsDir = FabricLoader.getInstance()
                .getGameDir()
                .resolve("cascade")
                .resolve("scripts");

        this.fileTreePanel = new FileTreePanel(scriptsDir);
        this.textEditorPanel = new TextEditorPanel(new EditorColorScheme());

        fileTreePanel.setFileSelectionListener(textEditorPanel::loadFile);
    }

    @Override
    protected void init() {
        super.init();

        try {
            fileTreePanel.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(ImGuiIO io) {
        ImGui.pushFont(ImGuiImpl.getDefaultFont());

        ImGui.setNextWindowPos(50, 50, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(1200, 700, ImGuiCond.FirstUseEver);

        int windowFlags = ImGuiWindowFlags.NoCollapse;

        if (ImGui.begin("Script Editor", windowFlags)) {
            fileTreePanel.render();
            ImGui.sameLine();
            textEditorPanel.render();
            ImGui.end();
        }

        ImGui.popFont();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}