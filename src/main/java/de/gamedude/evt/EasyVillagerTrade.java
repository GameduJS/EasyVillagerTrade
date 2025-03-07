package de.gamedude.evt;

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
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.Vec3d;
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
            tradeWorkflow.tickWorkflow();
        });

        ClientTickEvents.START_WORLD_TICK.register(client -> {
            MinecraftClient.getInstance().player.setVelocity( new Vec3d(0.06, 0 ,0) );
        });


        ClientCommandRegistrationCallback.EVENT.register(new EVTCommand(tradeWorkflow));
        UseBlockCallback.EVENT.register(tradeWorkflow.getHandler(SelectionInterface.class));
        UseEntityCallback.EVENT.register(tradeWorkflow.getHandler(SelectionInterface.class));
    }
}
