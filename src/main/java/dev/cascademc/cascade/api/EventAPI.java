package dev.cascademc.cascade.api;

import dev.cascademc.cascade.script.annotation.LuaAPI;
import dev.cascademc.cascade.script.annotation.LuaMethod;
import dev.cascademc.cascade.script.event.EventBus;
import org.luaj.vm2.LuaFunction;

@LuaAPI(namespace = "event")
public class EventAPI {
    private final String currentScriptId;

    public EventAPI(String currentScriptId) {
        this.currentScriptId = currentScriptId;
    }

    @LuaMethod(description = "Register a handler for the tick event")
    public void tick(LuaFunction callback) {
        EventBus.get().subscribe(currentScriptId, "tick", callback);
    }
}