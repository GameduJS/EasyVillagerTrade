package de.gamedude.evt.autowalk;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.function.Supplier;

public abstract class AutoAction {

    public Supplier<ClientPlayerEntity> player(){
        return () ->  MinecraftClient.getInstance().player;
    }

}
