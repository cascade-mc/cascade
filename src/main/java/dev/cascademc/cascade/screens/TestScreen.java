package dev.cascademc.cascade.screens;

import dev.cascademc.cascade.imgui.RenderInterface;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.type.ImBoolean;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestScreen extends Screen implements RenderInterface {

    private static final ImBoolean showDemoWindow = new ImBoolean(false);

    public TestScreen() {
        super(Component.literal("Test Screen"));
    }

    @Override
    public void render(ImGuiIO io) {
        if (ImGui.begin("Hello, World!")) {
            ImGui.setWindowSize(800, 600);
            ImGui.checkbox("Show Demo WIndow", showDemoWindow);
            ImGui.end();
        }

        ImGui.showDemoWindow(showDemoWindow);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
