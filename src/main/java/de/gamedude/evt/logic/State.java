package de.gamedude.evt.logic;

import de.gamedude.evt.handler.TradeWorkflow;
import net.minecraft.client.MinecraftClient;

public abstract class State {

    protected final TradeWorkflow tradeWorkflow;
    protected final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    public State(TradeWorkflow tradeWorkflow) {
        this.tradeWorkflow = tradeWorkflow;
    }

    public abstract int run();
}
