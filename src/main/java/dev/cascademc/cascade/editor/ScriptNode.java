package dev.cascademc.cascade.editor;

import java.nio.file.Path;

public abstract class ScriptNode {
    protected final Path path;

    public ScriptNode(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return path.getFileName().toString();
    }

    public abstract boolean isDirectory();
}
