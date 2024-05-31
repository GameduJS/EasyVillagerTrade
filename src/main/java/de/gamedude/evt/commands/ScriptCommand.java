package de.gamedude.evt.commands;

import com.mojang.brigadier.CommandDispatcher;
import de.gamedude.evt.EasyVillagerTrade;
import de.gamedude.evt.script.ScriptManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class ScriptCommand implements ClientCommandRegistrationCallback {


    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {

        dispatcher.register(literal("script").executes(context -> {
            EasyVillagerTrade.getTradeWorkflow().toggle(false);
            try {
                EasyVillagerTrade.getTradeWorkflow().getHandler(ScriptManager.class).loadScript("defaultscript");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return 1;
        }));

    }
}
