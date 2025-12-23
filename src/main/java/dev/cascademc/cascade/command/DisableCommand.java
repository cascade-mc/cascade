package dev.cascademc.cascade.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.cascademc.cascade.CascadeClient;
import dev.cascademc.cascade.api.ChatAPI;
import dev.cascademc.cascade.script.ScriptManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.CompletableFuture;

public class DisableCommand {
    private final ScriptManager scriptManager;

    public DisableCommand(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    public LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("disable")
                .then(ClientCommandManager.argument("scriptId", StringArgumentType.greedyString())
                        .suggests(this::suggestEnabledScripts)
                        .executes(this::execute)
                );
    }

    private int execute(CommandContext<FabricClientCommandSource> context) {
        String scriptId = StringArgumentType.getString(context, "scriptId");

        try {
            scriptManager.disableScript(scriptId);
            sendMessage(context, Component.text("Disabled script: ", NamedTextColor.GREEN)
                    .append(Component.text(scriptId, NamedTextColor.YELLOW)));
            return 1;
        } catch (Exception e) {
            sendMessage(context, Component.text("Failed to disable script: " + scriptId, NamedTextColor.RED));
            CascadeClient.LOGGER.error("Failed to disable script via command", e);
            return 0;
        }
    }

    private CompletableFuture<Suggestions> suggestEnabledScripts(
            CommandContext<FabricClientCommandSource> context,
            SuggestionsBuilder builder
    ) {
        scriptManager.getEnabledScripts().keySet().forEach(builder::suggest);
        return builder.buildFuture();
    }

    private void sendMessage(CommandContext<FabricClientCommandSource> context, Component component) {
        context.getSource().sendFeedback(ChatAPI.adventureToMinecraft(component));
    }
}