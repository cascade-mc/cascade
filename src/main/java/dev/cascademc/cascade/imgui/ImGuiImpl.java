package dev.cascademc.cascade.imgui;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import imgui.*;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlTexture;
import org.apache.commons.io.IOUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30C;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;

public final class ImGuiImpl {

    private final static ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private final static ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();

    private static short[] glyphRanges;
    private static ImFont defaultFont;
    private static ImFont boldFont;

    public static void create(final long handle) {
        ImGui.createContext();
        ImPlot.createContext();

        final ImGuiIO data = ImGui.getIO();
        data.setIniFilename("cascade.ini");

        defaultFont = loadFont("/fonts/Inter-Regular.ttf", 16);
        boldFont = loadFont("/fonts/Inter-Bold.ttf", 16);

        data.getFonts().build();

        data.setConfigFlags(ImGuiConfigFlags.DockingEnable);

        imGuiImplGlfw.init(handle, true);
        imGuiImplGl3.init();

        ImGui.styleColorsDark();
        applyColors();
        applyStyles();
    }

    private static void applyStyles() {
        ImGuiStyle style = ImGui.getStyle();
        style.setWindowRounding(5.0f);
        style.setFrameRounding(5.0f);
        style.setGrabRounding(5.0f);
        style.setTabRounding(5.0f);
        style.setPopupRounding(5.0f);
        style.setScrollbarRounding(5.0f);

        style.setWindowPadding(new ImVec2(8, 8));
        style.setFramePadding(new ImVec2(6, 4));
        style.setItemSpacing(new ImVec2(8, 6));
        style.setPopupBorderSize(0.0f);
    }

    private static void applyColors() {
        ImVec4[] colors = ImGui.getStyle().getColors();

        colors[ImGuiCol.WindowBg].set(new ImVec4(0.07f, 0.07f, 0.09f, 1.00f));
        colors[ImGuiCol.MenuBarBg].set(new ImVec4(0.12f, 0.12f, 0.15f, 1.00f));
        colors[ImGuiCol.PopupBg].set(new ImVec4(0.18f, 0.18f, 0.22f, 1.00f));
        colors[ImGuiCol.Header].set(new ImVec4(0.18f, 0.18f, 0.22f, 1.00f));
        colors[ImGuiCol.HeaderHovered].set(new ImVec4(0.30f, 0.30f, 0.40f, 1.00f));
        colors[ImGuiCol.HeaderActive].set(new ImVec4(0.25f, 0.25f, 0.35f, 1.00f));
        colors[ImGuiCol.Button].set(new ImVec4(0.20f, 0.22f, 0.27f, 1.00f));
        colors[ImGuiCol.ButtonHovered].set(new ImVec4(0.30f, 0.32f, 0.40f, 1.00f));
        colors[ImGuiCol.ButtonActive].set(new ImVec4(0.35f, 0.38f, 0.50f, 1.00f));
        colors[ImGuiCol.FrameBg].set(new ImVec4(0.15f, 0.15f, 0.18f, 1.00f));
        colors[ImGuiCol.FrameBgHovered].set(new ImVec4(0.22f, 0.22f, 0.27f, 1.00f));
        colors[ImGuiCol.FrameBgActive].set(new ImVec4(0.25f, 0.25f, 0.30f, 1.00f));
        colors[ImGuiCol.Tab].set(new ImVec4(0.18f, 0.18f, 0.22f, 1.00f));
        colors[ImGuiCol.TabHovered].set(new ImVec4(0.35f, 0.35f, 0.50f, 1.00f));
        colors[ImGuiCol.TabActive].set(new ImVec4(0.25f, 0.25f, 0.38f, 1.00f));
        colors[ImGuiCol.TabUnfocused].set(new ImVec4(0.13f, 0.13f, 0.17f, 1.00f));
        colors[ImGuiCol.TabUnfocusedActive].set(new ImVec4(0.20f, 0.20f, 0.25f, 1.00f));
        colors[ImGuiCol.TitleBg].set(new ImVec4(0.12f, 0.12f, 0.15f, 1.00f));
        colors[ImGuiCol.TitleBgActive].set(new ImVec4(0.15f, 0.15f, 0.20f, 1.00f));
        colors[ImGuiCol.TitleBgCollapsed].set(new ImVec4(0.10f, 0.10f, 0.12f, 1.00f));
        colors[ImGuiCol.Border].set(new ImVec4(0.20f, 0.20f, 0.25f, 0.50f));
        colors[ImGuiCol.BorderShadow].set(new ImVec4(0.00f, 0.00f, 0.00f, 0.00f));
        colors[ImGuiCol.Text].set(new ImVec4(0.90f, 0.90f, 0.95f, 1.00f));
        colors[ImGuiCol.TextDisabled].set(new ImVec4(0.50f, 0.50f, 0.55f, 1.00f));
        colors[ImGuiCol.CheckMark].set(new ImVec4(0.50f, 0.70f, 1.00f, 1.00f));
        colors[ImGuiCol.SliderGrab].set(new ImVec4(0.50f, 0.70f, 1.00f, 1.00f));
        colors[ImGuiCol.SliderGrabActive].set(new ImVec4(0.60f, 0.80f, 1.00f, 1.00f));
        colors[ImGuiCol.ResizeGrip].set(new ImVec4(0.50f, 0.70f, 1.00f, 0.50f));
        colors[ImGuiCol.ResizeGripHovered].set(new ImVec4(0.60f, 0.80f, 1.00f, 0.75f));
        colors[ImGuiCol.ResizeGripActive].set(new ImVec4(0.70f, 0.90f, 1.00f, 1.00f));
        colors[ImGuiCol.ScrollbarBg].set(new ImVec4(0.10f, 0.10f, 0.12f, 1.00f));
        colors[ImGuiCol.ScrollbarGrab].set(new ImVec4(0.30f, 0.30f, 0.35f, 1.00f));
        colors[ImGuiCol.ScrollbarGrabHovered].set(new ImVec4(0.40f, 0.40f, 0.50f, 1.00f));
        colors[ImGuiCol.ScrollbarGrabActive].set(new ImVec4(0.45f, 0.45f, 0.55f, 1.00f));

        ImGui.getStyle().setColors(colors);
    }

    /**
     * Helper method to create ImVec4 color from RGBA values (0-255)
     */
    private static ImVec4 rgba(int r, int g, int b, int a) {
        return new ImVec4(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
    }

    public static void beginImGuiRendering() {
        // Minecraft will not bind the framebuffer unless it is needed, so do it manually and hope Vulcan never gets real:tm:
        final RenderTarget framebuffer = Minecraft.getInstance().getMainRenderTarget();
        GlStateManager._glBindFramebuffer(GL30C.GL_FRAMEBUFFER, ((GlTexture) framebuffer.getColorTexture()).getFbo(((GlDevice) RenderSystem.getDevice()).directStateAccess(), null));
        GL11C.glViewport(0, 0, framebuffer.width, framebuffer.height);

        imGuiImplGl3.newFrame();
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();
    }

    public static void endImGuiRendering() {
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());

        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long pointer = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();

            GLFW.glfwMakeContextCurrent(pointer);
        }
    }

    /**
     * Loads a font from the given path with the specified pixel size.
     *
     * @param path      The path to the font file.
     * @param pixelSize The desired pixel size of the font.
     * @return The loaded ImFont instance.
     */
    private static ImFont loadFont(final String path, final int pixelSize) {
        if (glyphRanges == null) {
            final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder();

            rangesBuilder.addRanges(ImGui.getIO().getFonts().getGlyphRangesDefault());
            rangesBuilder.addRanges(ImGui.getIO().getFonts().getGlyphRangesCyrillic());
            rangesBuilder.addRanges(ImGui.getIO().getFonts().getGlyphRangesJapanese());

            glyphRanges = rangesBuilder.buildRanges();
        }

        final ImFontConfig config = new ImFontConfig();
        config.setGlyphRanges(glyphRanges);
        try (final InputStream in = Objects.requireNonNull(ImGuiImpl.class.getResourceAsStream(path))) {
            final byte[] fontData = IOUtils.toByteArray(in);
            return ImGui.getIO().getFonts().addFontFromMemoryTTF(fontData, pixelSize, config);
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to load font from path: " + path, e);
        } finally {
            config.destroy();
        }
    }

    /**
     * Get the default font (regular weight)
     */
    public static ImFont getDefaultFont() {
        return defaultFont;
    }

    /**
     * Get the bold font
     */
    public static ImFont getBoldFont() {
        return boldFont;
    }

    public static void dispose() {
        imGuiImplGl3.shutdown();
        imGuiImplGlfw.shutdown();

        ImPlot.destroyContext();
        ImGui.destroyContext();
    }
}