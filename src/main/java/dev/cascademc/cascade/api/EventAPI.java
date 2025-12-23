package dev.cascademc.cascade.api;

import dev.cascademc.cascade.script.annotation.LuaAPI;
import dev.cascademc.cascade.script.annotation.LuaFunction;
import dev.cascademc.cascade.script.event.EventBus;

@LuaAPI(namespace = "event")
public class EventAPI {
    private final String currentScriptId;

    public EventAPI(String currentScriptId) {
        this.currentScriptId = currentScriptId;
    }

    @LuaFunction(description = "Register a handler for the tick event")
    public void tick(org.luaj.vm2.LuaFunction callback) {
        EventBus.get().subscribe(currentScriptId, "tick", callback);
    }
}