package dev.cascademc.cascade.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.cascademc.cascade.script.annotation.LuaAPI;
import dev.cascademc.cascade.script.annotation.LuaFunction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ComponentSerialization;

@LuaAPI(namespace = "chat")
public class ChatAPI {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final GsonComponentSerializer GSON_SERIALIZER = GsonComponentSerializer.gson();
    private static final Gson GSON = new Gson();

    @LuaFunction(value = "print", description = "Prints a message to the chat")
    public void printChat(String message) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            Component adventureComponent = MINI_MESSAGE.deserialize(message);
            net.minecraft.network.chat.Component minecraftComponent = adventureToMinecraft(adventureComponent);
            client.player.displayClientMessage(minecraftComponent, false);
        }
    }

    @LuaFunction(description = "Clears all chat messages")
    public void clear() {
        Minecraft client = Minecraft.getInstance();
        client.gui.getChat().clearMessages(false);
    }

    public static net.minecraft.network.chat.Component adventureToMinecraft(Component adventureComponent) {
        String json = GSON_SERIALIZER.serialize(adventureComponent);
        JsonElement jsonElement = GSON.fromJson(json, JsonElement.class);
        return ComponentSerialization.CODEC.decode(JsonOps.INSTANCE, jsonElement)
                .getOrThrow()
                .getFirst();
    }
}