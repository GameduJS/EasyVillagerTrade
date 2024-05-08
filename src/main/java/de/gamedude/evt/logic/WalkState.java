package de.gamedude.evt.logic;

import de.gamedude.evt.handler.TradeWorkflow;

public class WalkState  extends State{

    public WalkState(TradeWorkflow tradeWorkflow, int dx, int dz) {
        super(tradeWorkflow);
    }

    @Override
    public int run() {
        return 0;
    }
}
