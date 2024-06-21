package de.gamedude.evt.logic;

import de.gamedude.evt.handler.TradeWithVillagerHandler;

public class BuyState extends State{

    @Override
    public int run() {
        tradeWorkflow.getHandler(TradeWithVillagerHandler.class).buy();
        return 1;
    }
}