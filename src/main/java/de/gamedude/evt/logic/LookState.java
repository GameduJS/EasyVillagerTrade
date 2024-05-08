package de.gamedude.evt.logic;

import de.gamedude.evt.handler.TradeWorkflow;

public class LookState  extends State{

    public LookState(TradeWorkflow tradeWorkflow, boolean toFormerRotation) {
        super(tradeWorkflow);
    }
    public LookState(TradeWorkflow tradeWorkflow, float yaw, float pitch) {
        super(tradeWorkflow);
    }

    @Override
    public int run() {
        return 0;
    }
}
