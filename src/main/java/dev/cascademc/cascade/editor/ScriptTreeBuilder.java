package dev.cascademc.cascade.editor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ScriptTreeBuilder {

    public static ScriptDirectoryNode build(Path root) throws IOException {
        ScriptDirectoryNode rootNode = new ScriptDirectoryNode(root);
        scanDirectory(rootNode);
        return rootNode;
    }

    private static void scanDirectory(ScriptDirectoryNode dirNode) throws IOException {
        try (Stream<Path> stream = Files.list(dirNode.getPath())) {
            stream.forEach(path -> {
                try {
                    if (Files.isDirectory(path)) {
                        ScriptDirectoryNode childDir = new ScriptDirectoryNode(path);
                        scanDirectory(childDir);
                        dirNode.addChild(childDir);
                    } else if (path.toString().endsWith(".lua")) {
                        dirNode.addChild(new ScriptFileNode(path));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
