package dev.cascademc.cascade.editor;

import java.nio.file.Path;

public class ScriptFileNode extends ScriptNode {

    public ScriptFileNode(Path path) {
        super(path);
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
