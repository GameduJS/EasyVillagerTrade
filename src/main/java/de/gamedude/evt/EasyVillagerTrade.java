package de.gamedude.evt;

import de.gamedude.evt.autowalk.AutoWalkEngine;
import de.gamedude.evt.commands.EVTCommand;
import de.gamedude.evt.commands.ScriptCommand;
import de.gamedude.evt.handler.TradeRequestContainer;
import de.gamedude.evt.screen.TradeSelectScreen;
import de.gamedude.evt.script.OldScriptManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class EasyVillagerTrade implements ClientModInitializer {

    private static final KeyBinding SCREEN_KEY_BINDING = new KeyBinding("Deine Mutter", GLFW.GLFW_KEY_F6, "de.gamedude.evt");

    private static AutoWalkEngine autoWalkEngine = new AutoWalkEngine();
    private TradeRequestContainer tradeRequestContainer;

    @Override
    public void onInitializeClient() {
        this.tradeRequestContainer = new TradeRequestContainer();

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if(SCREEN_KEY_BINDING.wasPressed())
                client.setScreen(new TradeSelectScreen());
        });

        ClientCommandRegistrationCallback.EVENT.register(new ScriptCommand(autoWalkEngine));
        ClientCommandRegistrationCallback.EVENT.register(new EVTCommand(tradeRequestContainer));
    }

    public static AutoWalkEngine getAutoWalkEngine() {
        return autoWalkEngine;
    }
}
