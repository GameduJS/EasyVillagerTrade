package de.gamedude.evt.autowalk;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public abstract class AutoAction {

    public ClientPlayerEntity getPlayer(){
        return MinecraftClient.getInstance().player;
    }

}
