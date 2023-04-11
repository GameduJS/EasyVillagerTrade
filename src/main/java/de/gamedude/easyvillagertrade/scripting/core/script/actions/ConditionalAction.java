package de.gamedude.easyvillagertrade.scripting.core.script.actions;

import de.gamedude.easyvillagertrade.scripting.core.script.actions.base.Action;

import java.util.*;

/**
 * TODO:
 * - Debuging / Rework
 */
public class ConditionalAction extends Action {

    private final int indexQueryCondition;
    private final Map<Boolean, List<Action>> conditionalActionMap;

    private Iterator<Action> actionIterator;
    private int repetitionCount;

    private Action currentAction;

    public ConditionalAction(String indexQueryCondition) {
        this.indexQueryCondition = this.parseNumberOrThrow(indexQueryCondition, () -> new NumberFormatException("'" + indexQueryCondition + "' isnt a valid integer")).intValue();
        this.conditionalActionMap = new HashMap<>();
    }

    public void addConditionalAction(boolean isIFClause, Action action) {
        this.conditionalActionMap.computeIfAbsent(isIFClause, k -> new ArrayList<>()).add(action);
    }

    public void setRepetitionCount(int repetitionCount) {
        this.repetitionCount = repetitionCount;
    }

    @Override
    public void performAction() {
        if (actionIterator == null) {
            actionIterator = conditionalActionMap.get(repetitionCount == indexQueryCondition).iterator();
        }

        if(currentAction == null) {
            if(actionIterator.hasNext())
                currentAction = actionIterator.next();
            else return;
        }
        if(currentAction.isFinished()) {
            if(actionIterator.hasNext())
                currentAction = actionIterator.next();
            else
                finished = true;
        } else {
            currentAction.performAction();
        }
    }

    @Override
    public void reset() {
        finished = false;
        conditionalActionMap.values().forEach(actionList -> actionList.forEach(Action::reset));
    }
}
