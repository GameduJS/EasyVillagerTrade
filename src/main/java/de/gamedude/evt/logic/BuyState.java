package de.gamedude.evt.logic;

import de.gamedude.evt.handler.TradeWorkflow;

public class BuyState  extends State{

    public BuyState(TradeWorkflow tradeWorkflow) {
        super(tradeWorkflow);
    }

    @Override
    public int run() {
        return 0;
    }
}