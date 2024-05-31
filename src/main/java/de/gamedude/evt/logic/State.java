package de.gamedude.evt.logic;

import de.gamedude.evt.handler.TradeWorkflow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.function.Supplier;

public abstract class State {

    protected final TradeWorkflow tradeWorkflow;
    protected final MinecraftClient client = MinecraftClient.getInstance();

    public State(TradeWorkflow tradeWorkflow) {
        this.tradeWorkflow = tradeWorkflow;
    }

    public abstract int run();

    protected Supplier<ClientPlayerEntity> player = () -> client.player;
}
