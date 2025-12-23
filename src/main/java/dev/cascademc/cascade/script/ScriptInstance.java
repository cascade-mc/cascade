package dev.cascademc.cascade.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.nio.file.Path;

public class ScriptInstance {
    private final String id;
    private final Path scriptPath;
    private final String scriptContent;
    private final Globals globals;
    private boolean enabled;

    private LuaFunction onEnableHook;
    private LuaFunction onDisableHook;

    public ScriptInstance(String id, Path scriptPath, String scriptContent, Globals globals) {
        this.id = id;
        this.scriptPath = scriptPath;
        this.scriptContent = scriptContent;
        this.globals = globals;
        this.enabled = false;
    }

    public String getId() {
        return id;
    }

    public Path getScriptPath() {
        return scriptPath;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public Globals getGlobals() {
        return globals;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LuaFunction getOnEnableHook() {
        return onEnableHook;
    }

    public void setOnEnableHook(LuaFunction onEnableHook) {
        this.onEnableHook = onEnableHook;
    }

    public LuaFunction getOnDisableHook() {
        return onDisableHook;
    }

    public void setOnDisableHook(LuaFunction onDisableHook) {
        this.onDisableHook = onDisableHook;
    }

    public void callOnEnable() {
        if (onEnableHook != null) {
            try {
                onEnableHook.call();
            } catch (Exception e) {
                System.err.println("Error calling on_enable for script '" + id + "': " + e.getMessage());
            }
        }
    }

    public void callOnDisable() {
        if (onDisableHook != null) {
            try {
                onDisableHook.call();
            } catch (Exception e) {
                System.err.println("Error calling on_disable for script '" + id + "': " + e.getMessage());
            }
        }
    }
}