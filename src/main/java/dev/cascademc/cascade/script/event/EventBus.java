package dev.cascademc.cascade.script.event;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
    private static final EventBus INSTANCE = new EventBus();

    private final Map<String, Map<String, List<LuaFunction>>> eventHandlers = new ConcurrentHashMap<>();

    private EventBus() {}

    public static EventBus get() {
        return INSTANCE;
    }

    public void subscribe(String scriptId, String eventName, LuaFunction handler) {
        eventHandlers
                .computeIfAbsent(eventName, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(scriptId, k -> new ArrayList<>())
                .add(handler);
    }

    public void unsubscribe(String scriptId, String eventName) {
        Map<String, List<LuaFunction>> handlers = eventHandlers.get(eventName);
        if (handlers != null) {
            handlers.remove(scriptId);
        }
    }

    public void unsubscribeAll(String scriptId) {
        for (Map<String, List<LuaFunction>> handlers : eventHandlers.values()) {
            handlers.remove(scriptId);
        }
    }

    public void fire(String eventName, Varargs args) {
        Map<String, List<LuaFunction>> handlers = eventHandlers.get(eventName);
        if (handlers == null) return;

        for (Map.Entry<String, List<LuaFunction>> entry : handlers.entrySet()) {
            String scriptId = entry.getKey();
            for (LuaFunction handler : entry.getValue()) {
                try {
                    handler.invoke(args);
                } catch (LuaError e) {
                    System.err.println("Error in event handler for script '" + scriptId + "' on event '" + eventName + "': " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Unexpected error in event handler for script '" + scriptId + "': " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public void fire(String eventName) {
        fire(eventName, LuaValue.NONE);
    }

    public Set<String> getSubscribers(String eventName) {
        Map<String, List<LuaFunction>> handlers = eventHandlers.get(eventName);
        return handlers != null ? new HashSet<>(handlers.keySet()) : Collections.emptySet();
    }

    public boolean isSubscribed(String scriptId, String eventName) {
        Map<String, List<LuaFunction>> handlers = eventHandlers.get(eventName);
        return handlers != null && handlers.containsKey(scriptId);
    }
}