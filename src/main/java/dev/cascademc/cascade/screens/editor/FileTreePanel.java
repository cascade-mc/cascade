package dev.cascademc.cascade.screens.editor;

import dev.cascademc.cascade.editor.ScriptDirectoryNode;
import dev.cascademc.cascade.editor.ScriptNode;
import dev.cascademc.cascade.editor.ScriptTreeBuilder;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class FileTreePanel {
    private final Path scriptsDir;
    private ScriptDirectoryNode scriptRoot;
    private ScriptNode selectedNode;
    private Consumer<Path> fileSelectionListener;

    public FileTreePanel(Path scriptsDir) {
        this.scriptsDir = scriptsDir;
    }

    public void initialize() throws IOException {
        Files.createDirectories(scriptsDir);
        scriptRoot = ScriptTreeBuilder.build(scriptsDir);
    }

    public void setFileSelectionListener(Consumer<Path> listener) {
        this.fileSelectionListener = listener;
    }

    public void render() {
        ImGui.beginChild("Sidebar", 250, 0, true);

        ImGui.text("Scripts");
        ImGui.separator();

        ImGui.pushStyleVar(ImGuiStyleVar.IndentSpacing, 10.0f);

        if (scriptRoot != null) {
            for (ScriptNode child : scriptRoot.getChildren()) {
                renderNode(child);
            }
        }

        ImGui.popStyleVar();
        ImGui.endChild();
    }

    private void renderNode(ScriptNode node) {
        if (node.isDirectory()) {
            renderDirectoryNode(node);
        } else {
            renderFileNode(node);
        }
    }

    private void renderDirectoryNode(ScriptNode node) {
        boolean open = ImGui.treeNodeEx(
                node.getName(),
                ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanFullWidth
        );

        if (open) {
            for (ScriptNode child : ((ScriptDirectoryNode) node).getChildren()) {
                renderNode(child);
            }
            ImGui.treePop();
        }
    }

    private void renderFileNode(ScriptNode node) {
        int flags = ImGuiTreeNodeFlags.Leaf
                | ImGuiTreeNodeFlags.NoTreePushOnOpen
                | ImGuiTreeNodeFlags.SpanFullWidth;

        if (node == selectedNode) {
            flags |= ImGuiTreeNodeFlags.Selected;
        }

        ImGui.treeNodeEx(node.getName(), flags);

        if (ImGui.isItemClicked()) {
            selectedNode = node;
            if (fileSelectionListener != null) {
                fileSelectionListener.accept(node.getPath());
            }
        }
    }
}