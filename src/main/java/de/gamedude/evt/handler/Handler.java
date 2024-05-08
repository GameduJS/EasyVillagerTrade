package de.gamedude.evt.handler;

import de.gamedude.evt.EasyVillagerTrade;
import de.gamedude.evt.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public interface Handler {

    default void loadConfig(Config config) { }

    default void reloadConfig(Config config) { this.loadConfig(config);}

    default <T extends Handler> T getHandler(Class<T> clazz) {
        return EasyVillagerTrade.getTradeWorkflow().getHandler(clazz);
    }

    default ClientPlayerEntity player() {
        return MinecraftClient.getInstance().player;
    }

}
