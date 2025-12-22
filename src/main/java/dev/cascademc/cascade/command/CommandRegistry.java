package dev.cascademc.cascade.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.cascademc.cascade.CascadeClient;
import dev.cascademc.cascade.api.ChatAPI;
import dev.cascademc.cascade.screens.TestScreen;
import dev.cascademc.cascade.script.ScriptManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class CommandRegistry {
    private final ScriptManager scriptManager;

    public CommandRegistry(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    public void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("loadscript")
                        .then(ClientCommandManager.argument("filename", StringArgumentType.greedyString())
                                .suggests(this::suggestScripts)
                                .executes(this::executeLoadScript)
                        )
        );

        dispatcher.register(
                ClientCommandManager.literal("cascademenu")
                        .executes(context -> {
                            Minecraft client = Minecraft.getInstance();
                            client.execute(() -> {
                                client.setScreen(new TestScreen());
                            });
                            return 1;
                        })
        );
    }

    private int executeLoadScript(CommandContext<FabricClientCommandSource> context) {
        String input = StringArgumentType.getString(context, "filename");

        input = input.replace("\\", "/");

        if (!input.endsWith(".lua")) {
            input += ".lua";
        }

        try {
            scriptManager.loadScript(input);

            sendMessage(context, Component.text("Loaded script: ", NamedTextColor.GREEN)
                    .append(Component.text(input, NamedTextColor.YELLOW)));
            return 1;
        } catch (Exception e) {
            sendMessage(context, Component.text("Failed to load script: " + input, NamedTextColor.RED));
            CascadeClient.LOGGER.error("Failed to load script via command", e);
            return 0;
        }
    }

    private CompletableFuture<Suggestions> suggestScripts(
            CommandContext<FabricClientCommandSource> context,
            SuggestionsBuilder builder
    ) {
        try {
            Path scriptsDir = scriptManager.getScriptsDirectory();

            if (Files.exists(scriptsDir)) {
                try (Stream<Path> paths = Files.walk(scriptsDir)) {
                    paths
                            .filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".lua"))
                            .map(scriptsDir::relativize)
                            .map(path -> path.toString().replace("\\", "/"))
                            .forEach(builder::suggest);
                }
            }
        } catch (IOException e) {
            CascadeClient.LOGGER.error("Error listing scripts for suggestions", e);
        }

        return builder.buildFuture();
    }

    private void sendMessage(CommandContext<FabricClientCommandSource> context, Component component) {
        context.getSource().sendFeedback(ChatAPI.adventureToMinecraft(component));
    }
}