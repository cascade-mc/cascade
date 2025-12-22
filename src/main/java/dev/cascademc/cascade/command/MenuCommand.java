package dev.cascademc.cascade.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import dev.cascademc.cascade.screens.ScriptEditorScreen;

public class MenuCommand {

    public LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("menu")
                .executes(this::execute);
    }

    private int execute(CommandContext<FabricClientCommandSource> context) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.setScreen(new ScriptEditorScreen()));
        return 1;
    }
}