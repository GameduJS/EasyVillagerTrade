package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.ParsingContext;
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
        player.sendMessage(Text.of("TODO: Executed SelectState with status - " + status), false);
        return 0;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public void initState() { }


    public static State parse(String[] args, ParsingContext ctx) throws Exception {
        if (args.length > 0) {
            throw new Exception("SELECT has no arguments (yet?)");
        }
        return new SelectState();
    }
}
