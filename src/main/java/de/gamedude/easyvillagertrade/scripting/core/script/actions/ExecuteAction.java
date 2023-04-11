package de.gamedude.easyvillagertrade.scripting.core.script.actions;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.scripting.core.script.actions.base.Action;
import de.gamedude.easyvillagertrade.utils.TradingState;

public class ExecuteAction extends Action {

    @Override
    public void performAction() {
        EasyVillagerTrade.getModBase().setState(TradingState.CHECK_OFFERS);
        EasyVillagerTrade.getModBase().handleInteractionWithVillager();
        finished = true;
    }

    @Override
    public void reset() {
        finished = false;
    }
}
