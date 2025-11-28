package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class SelectState extends State {

    public SelectState() {
        super(TradeWorkflow.INSTANCE);
    }

    @Override
    public int run() {
        ClientPlayerEntity player = playerSupplier.get();
        int status = getTradeWorkflow().getHandler(SelectionInterface.class).selectClosestToPlayer(player);
        player.sendMessage(Text.of("Executed SelectState with status - " + status), true);
        return 0;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public void initState() {
        System.out.println("[DEBUG] SelectState.initState");
    }
}
