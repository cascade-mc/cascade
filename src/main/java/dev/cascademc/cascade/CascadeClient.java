package dev.cascademc.cascade;

import dev.cascademc.cascade.command.CommandRegistry;
import dev.cascademc.cascade.script.ScriptManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class CascadeClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("cascade");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Cascade");

        Path scriptsDir = FabricLoader.getInstance()
                .getGameDir()
                .resolve("cascade")
                .resolve("scripts");

        try {
            Files.createDirectories(scriptsDir);
        } catch (Exception e) {
            LOGGER.error("Failed to create scripts directory", e);
            return;
        }

        ScriptManager scriptManager = new ScriptManager(scriptsDir);
        CommandRegistry commandRegistry = new CommandRegistry(scriptManager);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            commandRegistry.registerCommands(dispatcher);
        });
    }
}