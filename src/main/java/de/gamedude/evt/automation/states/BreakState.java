package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.ParsingContext;

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

    public static State parse(String[] args, ParsingContext ctx) throws Exception {
        return new BreakState();
    }
}
