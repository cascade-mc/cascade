package dev.cascademc.cascade.editor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ScriptDirectoryNode extends ScriptNode {

    private final List<ScriptNode> children = new ArrayList<>();

    public ScriptDirectoryNode(Path path) {
        super(path);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    public List<ScriptNode> getChildren() {
        return children;
    }

    public void addChild(ScriptNode node) {
        children.add(node);
    }
}
