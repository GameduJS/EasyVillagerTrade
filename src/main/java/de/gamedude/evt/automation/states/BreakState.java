package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.TradeWorkflow;

public class BreakState extends State {

    public BreakState() {
        super(TradeWorkflow.INSTANCE);
    }

    @Override
    public int run() {
        return 0;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void initState() {

    }
}
