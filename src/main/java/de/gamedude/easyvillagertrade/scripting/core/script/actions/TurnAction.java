package de.gamedude.easyvillagertrade.scripting.core.script.actions;

import de.gamedude.easyvillagertrade.scripting.core.script.actions.base.Action;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class TurnAction extends Action {

    private final float yaw;
    private final float pitch;

    public TurnAction(String yaw, String pitch) {
        this.yaw = this.parseNumberOrThrow(yaw, () -> new IllegalArgumentException(yaw + " is not a double!")).floatValue();
        this.pitch = this.parseNumberOrThrow(pitch, () -> new IllegalArgumentException(pitch + " is not a double!")).floatValue();
    }

    @Override
    public void performAction() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        player.setYaw(this.yaw);
        player.setPitch(this.pitch);
        finished = true;
    }

    @Override
    public void reset() {
        finished = false;
    }
}
