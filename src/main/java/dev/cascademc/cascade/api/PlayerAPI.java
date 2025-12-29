package dev.cascademc.cascade.api;

import dev.cascademc.cascade.script.annotation.LuaAPI;
import dev.cascademc.cascade.script.annotation.LuaMethod;
import net.minecraft.client.Minecraft;

@LuaAPI(namespace = "player")
public class PlayerAPI {

    private final Minecraft mc = Minecraft.getInstance();

    @LuaMethod(description = "Makes the player jump")
    public void jump() {
        if (mc.player == null) return;

        mc.player.jumpFromGround();
    }
}