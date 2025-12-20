package dev.cascademc.cascade.script;

import dev.cascademc.cascade.CascadeClient;

import java.nio.file.Files;
import java.nio.file.Path;

public class ScriptManager {
    private final Path scriptsDir;
    private final LuaEnvironment luaEnv;

    public ScriptManager(Path scriptsDir) {
        this.scriptsDir = scriptsDir;
        this.luaEnv = new LuaEnvironment();
    }

    public void loadScript(String filename) {
        Path scriptPath = scriptsDir.resolve(filename);

        if (!Files.exists(scriptPath)) {
            CascadeClient.LOGGER.error("Script not found: {}", filename);
            throw new RuntimeException("Script not found: " + filename);
        }

        try {
            String script = Files.readString(scriptPath);
            luaEnv.execute(script);
            CascadeClient.LOGGER.info("Loaded script: {}", filename);
        } catch (Exception e) {
            CascadeClient.LOGGER.error("Error loading script: {}", filename, e);
            throw new RuntimeException("Failed to load script: " + filename, e);
        }
    }

    public LuaEnvironment getLuaEnvironment() {
        return luaEnv;
    }

    public Path getScriptsDirectory() {
        return scriptsDir;
    }
}