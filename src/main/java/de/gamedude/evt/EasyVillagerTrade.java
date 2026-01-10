package de.gamedude.evt;

import de.gamedude.evt.commands.AutomationTestCommand;
import de.gamedude.evt.commands.EVTCommand;
import de.gamedude.evt.config.Config;
import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.screen.TradeSelectScreen;
import de.gamedude.evt.script.ScriptManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class EasyVillagerTrade implements ClientModInitializer {

    private static final KeyBinding SCREEN_KEY_BINDING = new KeyBinding("Open Screen", GLFW.GLFW_KEY_F6, "de.gamedude.evt");
    public static final Config CONFIG = new Config("easyvillagertrade");

    @Override
    public void onInitializeClient() {
        TradeWorkflow tradeWorkflow = TradeWorkflow.INSTANCE;

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if(SCREEN_KEY_BINDING.wasPressed())
                client.setScreen(new TradeSelectScreen());
            tradeWorkflow.tick();
            tradeWorkflow.tickDebug();
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(tradeWorkflow.getHandler(ScriptManager.class));
        ClientCommandRegistrationCallback.EVENT.register(new EVTCommand(tradeWorkflow));
        ClientCommandRegistrationCallback.EVENT.register(new AutomationTestCommand());
        UseBlockCallback.EVENT.register(tradeWorkflow.getHandler(SelectionInterface.class));
        UseEntityCallback.EVENT.register(tradeWorkflow.getHandler(SelectionInterface.class));
    }
}
