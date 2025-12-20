package dev.cascademc.cascade.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ComponentSerialization;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class ChatAPI {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final GsonComponentSerializer GSON_SERIALIZER = GsonComponentSerializer.gson();
    private static final Gson GSON = new Gson();

    public static void register(Globals globals) {
        globals.set("print_chat", new PrintChatFunction());
    }

    private static class PrintChatFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            Minecraft client = Minecraft.getInstance();
            if (client.player != null) {
                Component adventureComponent = MINI_MESSAGE.deserialize(arg.tojstring());

                net.minecraft.network.chat.Component minecraftComponent = adventureToMinecraft(adventureComponent);

                client.player.displayClientMessage(minecraftComponent, false);
            }
            return LuaValue.NIL;
        }
    }

    public static net.minecraft.network.chat.Component adventureToMinecraft(Component adventureComponent) {
        String json = GSON_SERIALIZER.serialize(adventureComponent);
        JsonElement jsonElement = GSON.fromJson(json, JsonElement.class);

        return ComponentSerialization.CODEC.decode(JsonOps.INSTANCE, jsonElement)
                .getOrThrow()
                .getFirst();
    }
}