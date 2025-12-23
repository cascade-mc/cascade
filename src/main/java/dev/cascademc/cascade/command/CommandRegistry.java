package dev.cascademc.cascade.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.cascademc.cascade.script.ScriptManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class CommandRegistry {
    private final ScriptManager scriptManager;

    public CommandRegistry(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    public void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("cascade")
                        .then(new LoadCommand(scriptManager).build())
                        .then(new UnloadCommand(scriptManager).build())
                        .then(new ReloadCommand(scriptManager).build())
                        .then(new EnableCommand(scriptManager).build())
                        .then(new DisableCommand(scriptManager).build())
                        .then(new ToggleCommand(scriptManager).build())
                        .then(new RunCommand(scriptManager).build())
                        .then(new MenuCommand().build())
        );
    }
}