package dev.cascademc.cascade.mixin.imgui;

import dev.cascademc.cascade.imgui.ImGuiImpl;
import dev.cascademc.cascade.script.event.EventBus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import com.mojang.blaze3d.platform.Window;
import org.luaj.vm2.LuaValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    @Final
    private Window window;

    @Shadow private long clientTickCount;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initImGui(GameConfig args, CallbackInfo ci) {
        ImGuiImpl.create(window.handle());
    }

    @Inject(method = "close", at = @At("HEAD"))
    public void closeImGui(CallbackInfo ci) {
        ImGuiImpl.dispose();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        EventBus.get().fire("tick", LuaValue.valueOf(clientTickCount));
    }

}