package de.gamedude.evt.logic;

import de.gamedude.evt.handler.TradeWorkflow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class State {

    protected final TradeWorkflow tradeWorkflow = TradeWorkflow.INSTANCE;
    protected final MinecraftClient client = MinecraftClient.getInstance();

    public abstract int run();

    public void initState() { }

    protected final Supplier<ClientPlayerEntity> player = () -> client.player;
}
