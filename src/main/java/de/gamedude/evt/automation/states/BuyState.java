package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.Feedback;
import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.TradeRequestContainer;
import de.gamedude.evt.handler.TradeWithVillagerHandler;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.ParsingContext;
import de.gamedude.evt.utils.TradeRequest;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class BuyState extends State {
    public BuyState() {
        super(TradeWorkflow.INSTANCE);
    }

    @Override
    public int run() {
        ClientPlayerEntity player = playerSupplier.get();
        TradeWithVillagerHandler tWVH = getTradeWorkflow().getHandler(TradeWithVillagerHandler.class);
        TradeRequestContainer tRC = getTradeWorkflow().getHandler(TradeRequestContainer.class);
        Feedback feedback = tWVH.buy();

        // TODO Logic on failure, should program stop?

        if ( feedback.success() ) {
            TradeRequest request = tWVH.getRequestedTrade();
            tRC.removeRequestByEnchantment(request.enchantment());
            feedback.with(String.format("{DEV Note}: Successfully bought: %s for %s emeralds", request.enchantment().toString(), request.cost()));
        }

        player.sendMessage(Text.of("TODO: Executed BuyState with status - " + feedback.getStatus()), false);
        player.sendMessage(Text.of(feedback.getReason()), false);
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
