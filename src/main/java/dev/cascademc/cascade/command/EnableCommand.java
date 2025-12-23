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

public class EnableCommand {
    private final ScriptManager scriptManager;

    public EnableCommand(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    public LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("enable")
                .then(ClientCommandManager.argument("scriptId", StringArgumentType.greedyString())
                        .suggests(this::suggestDisabledScripts)
                        .executes(this::execute)
                );
    }

    private int execute(CommandContext<FabricClientCommandSource> context) {
        String scriptId = StringArgumentType.getString(context, "scriptId");

        try {
            scriptManager.enableScript(scriptId);
            sendMessage(context, Component.text("Enabled script: ", NamedTextColor.GREEN)
                    .append(Component.text(scriptId, NamedTextColor.YELLOW)));
            return 1;
        } catch (Exception e) {
            sendMessage(context, Component.text("Failed to enable script: " + scriptId, NamedTextColor.RED));
            CascadeClient.LOGGER.error("Failed to enable script via command", e);
            return 0;
        }
    }

    private CompletableFuture<Suggestions> suggestDisabledScripts(
            CommandContext<FabricClientCommandSource> context,
            SuggestionsBuilder builder
    ) {
        scriptManager.getLoadedScripts().keySet().stream()
                .filter(id -> !scriptManager.isEnabled(id))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    private void sendMessage(CommandContext<FabricClientCommandSource> context, Component component) {
        context.getSource().sendFeedback(ChatAPI.adventureToMinecraft(component));
    }
}