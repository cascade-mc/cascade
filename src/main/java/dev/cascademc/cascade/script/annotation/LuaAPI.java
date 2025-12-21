package dev.cascademc.cascade.script.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as containing Lua API functions
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LuaAPI {
    /**
     * Optional namespace prefix for all functions in this class
     */
    String namespace() default "";
}