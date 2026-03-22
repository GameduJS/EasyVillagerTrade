package de.gamedude.evt.automation;

import de.gamedude.evt.handler.TradeWorkflow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;

import java.util.function.Supplier;

/**
 * Represent a single command of a script <br>
 * Performs a single actions until it's done <br>
 * May accept arguments
 */
public abstract class State {
    private final TradeWorkflow tradeWorkflow;
    protected final MinecraftClient client = MinecraftClient.getInstance();

    public State(TradeWorkflow tradeWorkflow) {
        this.tradeWorkflow = tradeWorkflow;
    }

    /**
     * Implement logic when state is ticked
     * @return int - current state
     */
    public abstract int run();

    /**
     * Implement logic for when state is done
     * @return boolean - whether state is finished
     */
    public abstract boolean isDone();

    /**
     * Initializes state
     */
    public abstract void initState();

    /**
     * @return TradeWorkflow - Instance of the main class to use for
     */
    protected TradeWorkflow getTradeWorkflow() { return tradeWorkflow; }

    /**
     * Supplier<ClientPlayerEntity>  - Returns player instance as supplier as soon as its loaded
     */
    protected Supplier<ClientPlayerEntity> playerSupplier = () -> client.player;

    /**
     * Tries to open the chat so the program will run it the background without setting F3+P
     * "hadChatOpen" can be changed anywhere.
     * automatically closes any other ScreenHandler
     * This function should be called in the "isDone" method of a state that needed another screen
     */
    protected void tryOpenChat() {
        if ( MinecraftClient.getInstance().isWindowFocused() )
            return;
        if (!(playerSupplier.get().currentScreenHandler instanceof PlayerScreenHandler)) {
            playerSupplier.get().closeHandledScreen();
        }
        if ( !tradeWorkflow.hadChatOpen )
            return;
        if (client.currentScreen != null)
            return;
        client.execute(() ->
                client.setScreen(new ChatScreen("")));
    }
}
