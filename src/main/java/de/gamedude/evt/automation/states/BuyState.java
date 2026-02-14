package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.TradeWithVillagerHandler;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.ParsingContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class BuyState extends State {
    public BuyState() {
        super(TradeWorkflow.INSTANCE);
    }

    @Override
    public int run() {
        ClientPlayerEntity player = playerSupplier.get();
        int status = getTradeWorkflow().getHandler(TradeWithVillagerHandler.class).buy();
        player.sendMessage(Text.of("TODO: Executed BuyState with status - " + status), false);
        return 0;
    }

    @Override
    public boolean isDone() {
        this.tryOpenChat();
        return true;
    }

    @Override
    public void initState() { }

    public static State parse(String[] args, ParsingContext ctx) throws Exception {
        return new BuyState();
    }
}
