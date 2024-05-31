package de.gamedude.evt.logic;

import de.gamedude.evt.autowalk.ViewAction;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.utils.ActionInterface;

public class LookState  extends State{

    public LookState(TradeWorkflow tradeWorkflow, boolean toFormerRotation) {
        super(tradeWorkflow);
    }

    public LookState(TradeWorkflow tradeWorkflow, float yaw, float pitch) {
        super(tradeWorkflow);
        ((ActionInterface) player.get()).easyVillagerTrade$setWalkAction(new ViewAction(pitch, yaw));
    }

    @Override
    public int run() {
        return ((ActionInterface) player.get()).easyVillagerTrade$getWalkaction() == null ? 1 : 0;
    }
}
