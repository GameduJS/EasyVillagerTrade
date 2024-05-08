package de.gamedude.evt;

import de.gamedude.evt.autowalk.AutoWalkEngine;
import de.gamedude.evt.commands.EVTCommand;
import de.gamedude.evt.commands.ScriptCommand;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.screen.TradeSelectScreen;
import de.gamedude.evt.script.ScriptManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class EasyVillagerTrade implements ClientModInitializer {

    private static final KeyBinding SCREEN_KEY_BINDING = new KeyBinding("Open Screen", GLFW.GLFW_KEY_F6, "de.gamedude.evt");
    private static AutoWalkEngine autoWalkEngine;
    private static TradeWorkflow tradeWorkflow;

    @Override
    public void onInitializeClient() {
        autoWalkEngine = new AutoWalkEngine();
        tradeWorkflow = new TradeWorkflow();

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if(SCREEN_KEY_BINDING.wasPressed())
                client.setScreen(new TradeSelectScreen());
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> tradeWorkflow.getHandler(ScriptManager.class).copyDefaultToCache());


        ClientCommandRegistrationCallback.EVENT.register(new ScriptCommand());
        ClientCommandRegistrationCallback.EVENT.register(new EVTCommand(tradeWorkflow));
    }

    public static AutoWalkEngine getAutoWalkEngine() {
        return autoWalkEngine;
    }

    public static TradeWorkflow getTradeWorkflow() {
        return tradeWorkflow;
    }
}
