package de.gamedude.evt.commands;

import com.mojang.brigadier.CommandDispatcher;
import de.gamedude.evt.autowalk.AutoWalkEngine;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class ScriptCommand implements ClientCommandRegistrationCallback {

    private final AutoWalkEngine autoWalkEngine;

    public ScriptCommand(AutoWalkEngine autoWalkEngine) {
        this.autoWalkEngine = autoWalkEngine;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {

        dispatcher.register(literal("script").executes(context -> {
            return 1;
        }));

    }
}
