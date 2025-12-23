package dev.cascademc.cascade.input;

import dev.cascademc.cascade.mixin.accessor.KeyboardHandlerAccessor;
import dev.cascademc.cascade.mixin.accessor.MouseHandlerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InputHandler {

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static final Minecraft mc = Minecraft.getInstance();

    public static void press(String input) {
        if (isMouseButton(input)) {
            pressMouseButton(parseMouseButton(input));
        } else {
            pressKey(parseKey(input));
        }
    }

    public static void release(String input) {
        if (isMouseButton(input)) {
            releaseMouseButton(parseMouseButton(input));
        } else {
            releaseKey(parseKey(input));
        }
    }

    public static void click(String input, int delay) {
        if (isMouseButton(input)) {
            clickMouseButton(parseMouseButton(input), delay);
        } else {
            clickKey(parseKey(input), delay);
        }
    }

    public static boolean isPressed(String input) {
        if (isMouseButton(input)) {
            return isMouseButtonPressed(parseMouseButton(input));
        } else {
            return isKeyPressed(parseKey(input));
        }
    }

    private static boolean isMouseButton(String input) {
        String lower = input.toLowerCase();
        return lower.equals("mouse1") || lower.equals("mouse2") || lower.equals("mouse3") ||
                lower.equals("left") || lower.equals("right") || lower.equals("middle");
    }

    private static int parseMouseButton(String input) {
        return switch (input.toLowerCase()) {
            case "mouse1", "left" -> GLFW.GLFW_MOUSE_BUTTON_LEFT;
            case "mouse2", "right" -> GLFW.GLFW_MOUSE_BUTTON_RIGHT;
            case "mouse3", "middle" -> GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
            default -> throw new IllegalArgumentException("Unknown mouse button: " + input);
        };
    }

    private static int parseKey(String input) {
        String upper = input.toUpperCase();

        if (upper.length() == 1 && upper.charAt(0) >= 'A' && upper.charAt(0) <= 'Z') {
            return GLFW.GLFW_KEY_A + (upper.charAt(0) - 'A');
        }

        if (upper.length() == 1 && upper.charAt(0) >= '0' && upper.charAt(0) <= '9') {
            return GLFW.GLFW_KEY_0 + (upper.charAt(0) - '0');
        }

        if (upper.startsWith("F") && upper.length() > 1) {
            try {
                int fNum = Integer.parseInt(upper.substring(1));
                if (fNum >= 1 && fNum <= 12) {
                    return GLFW.GLFW_KEY_F1 + (fNum - 1);
                }
            } catch (NumberFormatException ignored) {}
        }

        return switch (upper) {
            case "SPACE" -> GLFW.GLFW_KEY_SPACE;
            case "ENTER", "RETURN" -> GLFW.GLFW_KEY_ENTER;
            case "ESCAPE", "ESC" -> GLFW.GLFW_KEY_ESCAPE;
            case "TAB" -> GLFW.GLFW_KEY_TAB;
            case "SHIFT", "LSHIFT" -> GLFW.GLFW_KEY_LEFT_SHIFT;
            case "RSHIFT" -> GLFW.GLFW_KEY_RIGHT_SHIFT;
            case "CONTROL", "CTRL", "LCTRL" -> GLFW.GLFW_KEY_LEFT_CONTROL;
            case "RCTRL" -> GLFW.GLFW_KEY_RIGHT_CONTROL;
            case "ALT", "LALT" -> GLFW.GLFW_KEY_LEFT_ALT;
            case "RALT" -> GLFW.GLFW_KEY_RIGHT_ALT;
            case "BACKSPACE" -> GLFW.GLFW_KEY_BACKSPACE;
            case "DELETE", "DEL" -> GLFW.GLFW_KEY_DELETE;
            case "INSERT" -> GLFW.GLFW_KEY_INSERT;
            case "HOME" -> GLFW.GLFW_KEY_HOME;
            case "END" -> GLFW.GLFW_KEY_END;
            case "PAGEUP" -> GLFW.GLFW_KEY_PAGE_UP;
            case "PAGEDOWN" -> GLFW.GLFW_KEY_PAGE_DOWN;
            case "UP" -> GLFW.GLFW_KEY_UP;
            case "DOWN" -> GLFW.GLFW_KEY_DOWN;
            case "LEFT" -> GLFW.GLFW_KEY_LEFT;
            case "RIGHT" -> GLFW.GLFW_KEY_RIGHT;
            default -> throw new IllegalArgumentException("Unknown key: " + input);
        };
    }

    private static void pressKey(int keyCode) {
        KeyEvent event = new KeyEvent(keyCode, 0, 0);
        getKeyboardHandler().cascade$keyPress(mc.getWindow().handle(), GLFW.GLFW_PRESS, event);
    }

    private static void releaseKey(int keyCode) {
        KeyEvent event = new KeyEvent(keyCode, 0, 0);
        getKeyboardHandler().cascade$keyPress(mc.getWindow().handle(), GLFW.GLFW_RELEASE, event);
    }

    private static void clickKey(int keyCode, int delay) {
        executor.submit(() -> {
            try {
                Thread.sleep(1000l);
                pressKey(keyCode);
                Thread.sleep(delay);
                releaseKey(keyCode);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private static void pressMouseButton(int button) {
        if (!shouldSimulateInput()) return;
        getMouseHandler().cascade$press(mc.getWindow().handle(), new MouseButtonInfo(button, 0), GLFW.GLFW_PRESS);
    }

    private static void releaseMouseButton(int button) {
        if (!shouldSimulateInput()) return;
        getMouseHandler().cascade$press(mc.getWindow().handle(), new MouseButtonInfo(button, 0), GLFW.GLFW_RELEASE);
    }

    private static void clickMouseButton(int button, int delay) {
        if (!shouldSimulateInput()) return;
        executor.submit(() -> {
            try {
                Thread.sleep(1000L);
                pressMouseButton(button);
                Thread.sleep(delay);
                releaseMouseButton(button);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    // now it should work how i want :)

    private static boolean isKeyPressed(int keyCode) {
        return GLFW.glfwGetKey(mc.getWindow().handle(), keyCode) == GLFW.GLFW_PRESS;
    }

    private static boolean isMouseButtonPressed(int button) {
        return GLFW.glfwGetMouseButton(mc.getWindow().handle(), button) == GLFW.GLFW_PRESS;
    }

    private static boolean shouldSimulateInput() {
        return mc.player != null;
    }

    private static MouseHandlerAccessor getMouseHandler() {
        return (MouseHandlerAccessor) mc.mouseHandler;
    }

    private static KeyboardHandlerAccessor getKeyboardHandler() {
        return (KeyboardHandlerAccessor) mc.keyboardHandler;
    }
}
