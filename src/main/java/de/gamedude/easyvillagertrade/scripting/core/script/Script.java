package de.gamedude.easyvillagertrade.scripting.core.script;

import de.gamedude.easyvillagertrade.scripting.core.script.actions.base.Action;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.village.TradeOfferList;

import java.util.Iterator;
import java.util.List;

public class Script {

    private boolean triggered;
    private boolean active;

    private final List<Action> actionList;
    private int repetitionCount;
    private Action currentAction;

    private Iterator<Action> queue;
    private int index = 0;

    public Script(List<Action> actionList) {
        this.actionList = actionList;
    }

    private void actionIterator() {
        actionList.forEach(Action::reset);
        queue = actionList.iterator();
    }

    public void setRepetitionCount(int times) {
        this.repetitionCount = times;
        this.index = 0;
    }


    public boolean isTriggered() {
        return triggered && active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTriggered(boolean triggered) {
        if (!active)
            return;
        if (index < repetitionCount) {
            this.triggered = triggered;
            index++;
        }
    }

    /**
     * Triggered whenever an offer has been found
     * <p>Called on start of client tick {@link ClientTickEvents#START_CLIENT_TICK}
     * <p>{@link de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase#checkVillagerOffers(TradeOfferList)}
     * <p>{@link de.gamedude.easyvillagertrade.core.TradeRequestContainer#matchesAny(TradeRequest)}
     */
    public void triggerScript() {
        if (!active)
            return;
        if (!triggered)
            return;
        if (queue == null) {
            actionIterator();
        }

        if (currentAction == null || currentAction.isFinished()) {
            if (queue.hasNext())
                currentAction = queue.next();
            else
                disableScript();

        } else {
            currentAction.performAction();
            if (currentAction.isFinished())
                disableScript();
        }
    }

    private void disableScript() {
        this.triggered = false;
        this.queue = null;
        this.currentAction = null;
    }

}
