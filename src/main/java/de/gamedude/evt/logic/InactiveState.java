package de.gamedude.evt.logic;

import de.gamedude.evt.handler.TradeWorkflow;

public class InactiveState extends State{

    public InactiveState(TradeWorkflow tradeWorkflow) {
        super(tradeWorkflow);
    }

    @Override
    public int run() {
        return 0;
    }
}
