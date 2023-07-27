package de.gamedude.easyvillagertrade.scripting.core.script;

import de.gamedude.easyvillagertrade.scripting.core.script.actions.base.Action;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.village.TradeOfferList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Script {

    public static Script ofAction(List<Action> actionList) {
        return new Script(actionList);
    }

    private final List<Action> actionList;
    private boolean triggered;

    private int repetitionCount;

    private Action currentAction;
    private Iterator<Action> queue;
    private int index = 0;

    private Script(List<Action> actionList) {
        this.actionList = actionList;
    }

    private void actionIterator() {
        queue = actionList.iterator();
    }

    public void setRepetitionCount(int times) {
        this.repetitionCount = times;
        this.index = 0;
    }

    public boolean setTriggered(boolean triggered) {
        if (index < repetitionCount) {
            this.triggered = triggered;
            index++;
            return true;
        }
        return false;
    }

    /**
     * Triggered whenever an offer has been found
     * <p>Called on start of client tick {@link ClientTickEvents#START_CLIENT_TICK}
     * <p>{@link de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase#checkVillagerOffers(TradeOfferList)}
     * <p>{@link de.gamedude.easyvillagertrade.core.TradeRequestContainer#matchesAny(TradeRequest)}
     */
    public void tickScript() {
        if (!triggered)
            return;
        if (queue == null) {
            actionIterator();
            System.out.println("[DEBUG] Script.tickScript: " + queue.hasNext());
        }

        if (currentAction == null || currentAction.isFinished()) {
            if (queue.hasNext()) {
                currentAction = queue.next();
                System.out.println("[DEBUG] Script.tickScript: " + "Queueing Script #1");
            } else {
                disableScript();
                System.out.println("[DEBUG] Script.tickScript: " + "Disabled Script #2");
            }
        } else {
            currentAction.performAction();
            if (currentAction.isFinished()) {
                disableScript();
                System.out.println("[DEBUG] Script.tickScript: " + "OFIFIFIF");
            }
        }
    }

    private void disableScript() {
        this.triggered = false;
        this.queue = null;
        this.currentAction = null;
    }

}
