package dev.cascademc.cascade.api;

import dev.cascademc.cascade.input.InputHandler;
import dev.cascademc.cascade.script.annotation.LuaAPI;
import dev.cascademc.cascade.script.annotation.LuaFunction;

@LuaAPI(namespace = "input")
public class InputAPI {

    @LuaFunction(description = "Presses and holds a key or mouse button")
    public void press(String input) {
        InputHandler.press(input);
    }

    @LuaFunction(description = "Releases key or mouse button")
    public void release(String input) {
        InputHandler.release(input);
    }

    @LuaFunction(description = "Clicks a key or mouse button (presses and releases with a delay)")
    public void click(String input, int delay) {
        InputHandler.click(input, delay);
    }

    @LuaFunction(value = "click", description = "Clicks a key or mouse button with 15ms delay")
    public void clickDefault(String input) {
        InputHandler.click(input, 15);
    }

    @LuaFunction(description = "Checks if a key or mouse button is currently pressed")
    public boolean isPressed(String input) {
        return InputHandler.isPressed(input);
    }
}
