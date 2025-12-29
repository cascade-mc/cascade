package dev.cascademc.cascade.script;

import dev.cascademc.cascade.script.annotation.LuaAPI;
import dev.cascademc.cascade.script.annotation.LuaMethod;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes @LuaFunction annotations and registers them with the Lua environment
 */
public class LuaAnnotationProcessor {
    private final Globals globals;
    private final List<Object> apiInstances = new ArrayList<>();

    public LuaAnnotationProcessor(Globals globals) {
        this.globals = globals;
    }

    /**
     * Register an API class instance
     */
    public void registerAPI(Object apiInstance) {
        apiInstances.add(apiInstance);
        Class<?> clazz = apiInstance.getClass();

        LuaAPI apiAnnotation = clazz.getAnnotation(LuaAPI.class);
        String namespace = apiAnnotation != null ? apiAnnotation.namespace() : "";

        for (Method method : clazz.getDeclaredMethods()) {
            LuaMethod luaFunc = method.getAnnotation(LuaMethod.class);
            if (luaFunc != null) {
                registerMethod(apiInstance, method, luaFunc, namespace);
            }
        }
    }

    /**
     * Register a static API class
     */
    public void registerStaticAPI(Class<?> apiClass) {
        LuaAPI apiAnnotation = apiClass.getAnnotation(LuaAPI.class);
        String namespace = apiAnnotation != null ? apiAnnotation.namespace() : "";

        for (Method method : apiClass.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            LuaMethod luaFunc = method.getAnnotation(LuaMethod.class);
            if (luaFunc != null) {
                registerMethod(null, method, luaFunc, namespace);
            }
        }
    }

    private void registerMethod(Object instance, Method method, LuaMethod annotation, String namespace) {
        method.setAccessible(true);

        String functionName = annotation.value().isEmpty()
                ? method.getName()
                : annotation.value();

        if (!namespace.isEmpty()) {
            functionName = namespace + "." + functionName;
        }

        Class<?>[] paramTypes = method.getParameterTypes();

        String finalFunctionName = functionName;
        VarArgFunction luaFunction = new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                try {
                    Object result;

                    if (paramTypes.length == 0) {
                        result = method.invoke(instance);
                    } else if (paramTypes.length == 1) {
                        Object arg = convertLuaToJava(args.arg1(), paramTypes[0]);
                        result = method.invoke(instance, arg);
                    } else if (paramTypes.length == 2) {
                        Object arg1 = convertLuaToJava(args.arg(1), paramTypes[0]);
                        Object arg2 = convertLuaToJava(args.arg(2), paramTypes[1]);
                        result = method.invoke(instance, arg1, arg2);
                    } else {
                        Object[] javaArgs = new Object[paramTypes.length];
                        for (int i = 0; i < paramTypes.length; i++) {
                            javaArgs[i] = convertLuaToJava(args.arg(i + 1), paramTypes[i]);
                        }
                        result = method.invoke(instance, javaArgs);
                    }

                    return convertJavaToLua(result);

                } catch (Exception e) {
                    throw new RuntimeException("Error calling Lua function: " + finalFunctionName, e);
                }
            }
        };

        if (functionName.contains(".")) {
            String[] parts = functionName.split("\\.");
            LuaValue table = globals.get(parts[0]);

            if (table.isnil()) {
                table = LuaValue.tableOf();
                globals.set(parts[0], table);
            }

            table.set(parts[1], luaFunction);
        } else {
            globals.set(functionName, luaFunction);
        }
    }

    private Object convertLuaToJava(LuaValue luaValue, Class<?> targetType) {
        if (targetType == LuaValue.class || targetType == Varargs.class) {
            return luaValue;
        } else if (targetType == String.class) {
            return luaValue.tojstring();
        } else if (targetType == int.class || targetType == Integer.class) {
            return luaValue.toint();
        } else if (targetType == double.class || targetType == Double.class) {
            return luaValue.todouble();
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return luaValue.toboolean();
        } else if (targetType == long.class || targetType == Long.class) {
            return luaValue.tolong();
        }

        return luaValue;
    }

    private Varargs convertJavaToLua(Object result) {
        return switch (result) {
            case null -> LuaValue.NIL;
            case Void unused -> LuaValue.NIL;
            case LuaValue luaValue -> luaValue;
            case Varargs varargs -> varargs;
            case String s -> LuaValue.valueOf(s);
            case Number number -> LuaValue.valueOf(number.doubleValue());
            case Boolean b -> LuaValue.valueOf(b);
            default -> LuaValue.NIL;
        };

    }
}