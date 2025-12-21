package dev.cascademc.cascade.script;

import dev.cascademc.cascade.api.ChatAPI;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaEnvironment {
    private final Globals globals;
    private final LuaAnnotationProcessor processor;

    public LuaEnvironment() {
        this.globals = JsePlatform.standardGlobals();
        this.processor = new LuaAnnotationProcessor(globals);
        registerAPIs();
    }

    private void registerAPIs() {
        processor.registerAPI(new ChatAPI());
    }

    public Globals getGlobals() {
        return globals;
    }

    public LuaAnnotationProcessor getProcessor() {
        return processor;
    }

    public void execute(String script) {
        globals.load(script).call();
    }
}