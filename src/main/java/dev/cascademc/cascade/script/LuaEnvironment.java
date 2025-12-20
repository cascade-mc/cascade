package dev.cascademc.cascade.script;

import dev.cascademc.cascade.api.ChatAPI;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaEnvironment {
    private final Globals globals;

    public LuaEnvironment() {
        this.globals = JsePlatform.standardGlobals();
        registerAPIs();
    }

    private void registerAPIs() {
        ChatAPI.register(globals);
    }

    public Globals getGlobals() {
        return globals;
    }

    public void execute(String script) {
        globals.load(script).call();
    }
}