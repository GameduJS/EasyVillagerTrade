package de.gamedude.evt.handler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public interface Handler {

    default <T extends Handler> T getHandler(Class<T> clazz) {
        return TradeWorkflow.INSTANCE.getHandler(clazz);
    }

    default ClientPlayerEntity player() {
        return MinecraftClient.getInstance().player;
    }

}
