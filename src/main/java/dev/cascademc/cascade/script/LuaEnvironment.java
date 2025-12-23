package dev.cascademc.cascade.script;

import dev.cascademc.cascade.api.ChatAPI;
import dev.cascademc.cascade.api.EventAPI;
import dev.cascademc.cascade.api.InputAPI;
import dev.cascademc.cascade.api.PlayerAPI;
import dev.cascademc.cascade.script.annotation.LuaAPI;
import dev.cascademc.cascade.script.annotation.LuaFunction;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class LuaEnvironment {
    private final Globals globals;
    private final LuaAnnotationProcessor processor;
    private static final Map<String, String> identifierDocumentation = new HashMap<>();

    public LuaEnvironment() {
        this.globals = JsePlatform.standardGlobals();
        this.processor = new LuaAnnotationProcessor(globals);
        registerAPIs();
    }

    private void registerAPIs() {
        registerAPI(new ChatAPI());
        registerAPI(new InputAPI());
        registerAPI(new PlayerAPI());
    }

    private void registerAPI(Object apiInstance) {
        processor.registerAPI(apiInstance);
        buildDocumentation(apiInstance);
    }

    private void buildDocumentation(Object apiInstance) {
        Class<?> clazz = apiInstance.getClass();
        LuaAPI apiAnnotation = clazz.getAnnotation(LuaAPI.class);
        String namespace = apiAnnotation != null ? apiAnnotation.namespace() : "";

        for (Method method : clazz.getDeclaredMethods()) {
            LuaFunction luaFunc = method.getAnnotation(LuaFunction.class);
            if (luaFunc != null) {
                String functionName = luaFunc.value().isEmpty()
                        ? method.getName()
                        : luaFunc.value();

                if (!namespace.isEmpty()) {
                    functionName = namespace + "." + functionName;
                }

                String documentation = buildMethodDocumentation(method, luaFunc);
                identifierDocumentation.put(functionName, documentation);
            }
        }
    }

    private String buildMethodDocumentation(Method method, LuaFunction annotation) {
        StringBuilder doc = new StringBuilder();

        if (!annotation.description().isEmpty()) {
            doc.append(annotation.description()).append("\n");
        }

        doc.append(buildMethodSignature(method));

        return doc.toString();
    }

    private String buildMethodSignature(Method method) {
        StringBuilder signature = new StringBuilder();
        signature.append(method.getName()).append("(");

        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) signature.append(", ");
            signature.append(getSimpleTypeName(paramTypes[i]));
        }

        signature.append(")");
        return signature.toString();
    }

    private String getSimpleTypeName(Class<?> type) {
        if (type == String.class) return "string";
        if (type == int.class || type == Integer.class) return "number";
        if (type == double.class || type == Double.class) return "number";
        if (type == boolean.class || type == Boolean.class) return "boolean";
        if (type == long.class || type == Long.class) return "number";
        return type.getSimpleName();
    }

    public static Map<String, String> getIdentifierDocumentation() {
        return new HashMap<>(identifierDocumentation);
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