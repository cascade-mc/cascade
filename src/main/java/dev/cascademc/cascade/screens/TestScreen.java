package dev.cascademc.cascade.screens;

import dev.cascademc.cascade.editor.ScriptDirectoryNode;
import dev.cascademc.cascade.editor.ScriptNode;
import dev.cascademc.cascade.editor.ScriptTreeBuilder;
import dev.cascademc.cascade.imgui.ImGuiImpl;
import dev.cascademc.cascade.imgui.RenderInterface;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestScreen extends Screen implements RenderInterface {

    private ScriptDirectoryNode scriptRoot;
    private ScriptNode selectedNode;

    private Path currentFilePath;
    private boolean fileDirty = false;

    private final ImString editorBuffer = new ImString(1024 * 64);

    public TestScreen() {
        super(Component.literal("Script Editor"));
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
            editorBuffer.set(Files.readString(path));
            fileDirty = false;
        } catch (IOException e) {
            editorBuffer.set("-- Failed to load file");
            e.printStackTrace();
        }
    }

    private void saveFile() {
        if (currentFilePath == null) return;

        try {
            Files.writeString(currentFilePath, editorBuffer.get());
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

                    if (fileDirty) {
                        ImGui.sameLine();
                        ImGui.textColored(1f, 0.5f, 0.2f, 1f, "*");
                    }

                    ImGui.separator();

                    if (ImGui.inputTextMultiline(
                            "##editor",
                            editorBuffer,
                            ImGui.getContentRegionAvailX(),
                            ImGui.getContentRegionAvailY() - 30,
                            ImGuiInputTextFlags.AllowTabInput
                    )) {
                        fileDirty = true;
                    }

                    if (fileDirty) {
                        if (ImGui.button("Save")) {
                            saveFile();
                        }
                    }
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
