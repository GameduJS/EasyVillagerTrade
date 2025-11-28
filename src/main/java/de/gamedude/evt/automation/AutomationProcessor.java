package de.gamedude.evt.automation;

import de.gamedude.evt.handler.Handler;

import java.util.LinkedList;
import java.util.List;

public class AutomationProcessor implements Handler {

    private final List<State> queue = new LinkedList<>();

    public AutomationProcessor() { }

    public void tick() {
        State state = queue.get(0);
        state.run();

        if ( state.isDone() ) {
            queue.remove(0);
            return;
        }

        //??? Anything
    }

}
