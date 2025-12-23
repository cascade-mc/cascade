package dev.cascademc.cascade.script;

import dev.cascademc.cascade.CascadeClient;
import dev.cascademc.cascade.api.EventAPI;
import dev.cascademc.cascade.script.event.EventBus;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptManager {
    private final Path scriptsDir;
    private final Map<String, ScriptInstance> loadedScripts = new ConcurrentHashMap<>();
    private final Map<String, ScriptInstance> enabledScripts = new ConcurrentHashMap<>();

    public ScriptManager(Path scriptsDir) {
        this.scriptsDir = scriptsDir;
    }

    public void loadScript(String filename) {
        Path scriptPath = scriptsDir.resolve(filename);

        if (!Files.exists(scriptPath)) {
            CascadeClient.LOGGER.error("Script not found: {}", filename);
            throw new RuntimeException("Script not found: " + filename);
        }

        try {
            String scriptId = filename.replace(".lua", "");

            if (loadedScripts.containsKey(scriptId)) {
                unloadScript(scriptId);
            }

            String scriptContent = Files.readString(scriptPath);

            LuaEnvironment luaEnv = new LuaEnvironment();
            Globals globals = luaEnv.getGlobals();

            EventAPI eventAPI = new EventAPI(scriptId);
            luaEnv.getProcessor().registerAPI(eventAPI);

            ScriptInstance instance = new ScriptInstance(scriptId, scriptPath, scriptContent, globals);

            loadedScripts.put(scriptId, instance);
            CascadeClient.LOGGER.info("Loaded script: {}", filename);

        } catch (Exception e) {
            CascadeClient.LOGGER.error("Error loading script: {}", filename, e);
            throw new RuntimeException("Failed to load script: " + filename, e);
        }
    }

    public void unloadScript(String scriptId) {
        if (enabledScripts.containsKey(scriptId)) {
            disableScript(scriptId);
        }

        ScriptInstance instance = loadedScripts.remove(scriptId);
        if (instance != null) {
            CascadeClient.LOGGER.info("Unloaded script: {}", scriptId);
        }
    }

    public void reloadScript(String scriptId) {
        ScriptInstance instance = loadedScripts.get(scriptId);
        if (instance == null) {
            throw new RuntimeException("Script not loaded: " + scriptId);
        }

        boolean wasEnabled = enabledScripts.containsKey(scriptId);
        String filename = instance.getScriptPath().getFileName().toString();

        loadScript(filename);

        if (wasEnabled) {
            enableScript(scriptId);
        }
    }

    public void enableScript(String scriptId) {
        ScriptInstance instance = loadedScripts.get(scriptId);
        if (instance == null) {
            throw new RuntimeException("Script not loaded: " + scriptId);
        }

        if (enabledScripts.containsKey(scriptId)) {
            CascadeClient.LOGGER.warn("Script already enabled: {}", scriptId);
            return;
        }

        instance.getGlobals().load(instance.getScriptContent()).call();

        LuaValue onEnable = instance.getGlobals().get("on_enable");
        if (onEnable instanceof LuaFunction) {
            instance.setOnEnableHook((LuaFunction) onEnable);
        }

        LuaValue onDisable = instance.getGlobals().get("on_disable");
        if (onDisable instanceof LuaFunction) {
            instance.setOnDisableHook((LuaFunction) onDisable);
        }

        instance.setEnabled(true);
        enabledScripts.put(scriptId, instance);
        instance.callOnEnable();

        CascadeClient.LOGGER.info("Enabled script: {}", scriptId);
    }

    public void disableScript(String scriptId) {
        ScriptInstance instance = enabledScripts.remove(scriptId);
        if (instance == null) {
            CascadeClient.LOGGER.warn("Script not enabled: {}", scriptId);
            return;
        }

        instance.callOnDisable();
        EventBus.get().unsubscribeAll(scriptId);
        instance.setEnabled(false);

        CascadeClient.LOGGER.info("Disabled script: {}", scriptId);
    }

    public void toggleScript(String scriptId) {
        if (enabledScripts.containsKey(scriptId)) {
            disableScript(scriptId);
        } else {
            enableScript(scriptId);
        }
    }

    public void runScript(String filename) {
        Path scriptPath = scriptsDir.resolve(filename);

        if (!Files.exists(scriptPath)) {
            CascadeClient.LOGGER.error("Script not found: {}", filename);
            throw new RuntimeException("Script not found: " + filename);
        }

        try {
            String scriptContent = Files.readString(scriptPath);
            LuaEnvironment luaEnv = new LuaEnvironment();
            luaEnv.execute(scriptContent);
            CascadeClient.LOGGER.info("Ran script: {}", filename);
        } catch (Exception e) {
            CascadeClient.LOGGER.error("Error running script: {}", filename, e);
            throw new RuntimeException("Failed to run script: " + filename, e);
        }
    }

    public Map<String, ScriptInstance> getLoadedScripts() {
        return Collections.unmodifiableMap(loadedScripts);
    }

    public Map<String, ScriptInstance> getEnabledScripts() {
        return Collections.unmodifiableMap(enabledScripts);
    }

    public Path getScriptsDirectory() {
        return scriptsDir;
    }

    public boolean isLoaded(String scriptId) {
        return loadedScripts.containsKey(scriptId);
    }

    public boolean isEnabled(String scriptId) {
        return enabledScripts.containsKey(scriptId);
    }
}