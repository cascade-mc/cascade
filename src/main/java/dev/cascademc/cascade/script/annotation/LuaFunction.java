package dev.cascademc.cascade.script.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a Lua function to be registered
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LuaFunction {
    /**
     * The name of the function in Lua. Defaults to the method name.
     */
    String value() default "";

    /**
     * Description of what the function does
     */
    String description() default "";
}