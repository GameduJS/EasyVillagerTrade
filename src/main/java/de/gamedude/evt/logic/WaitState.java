package de.gamedude.evt.logic;

import de.gamedude.evt.handler.TradeWorkflow;

public class WaitState extends State {

    int ticks;

    public WaitState(TradeWorkflow tradeWorkflow, int ticks) {
        super(tradeWorkflow);
        this.ticks = ticks;
    }

    @Override
    public int run() {
        return ticks-- <= 0 ? 1 : 0;
    }
}
