package de.gamedude.easyvillagertrade.scripting.core.script.actions.base;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.scripting.movement.InputHandler;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.function.Supplier;

public abstract class Action {

    protected static final InputHandler inputHandler = EasyVillagerTrade.getModBase().getScriptCache().getInputHandler();

    protected boolean finished = false;
    public boolean isFinished() {
        return finished;
    }

    public abstract void performAction();

    public void reset() {
        this.finished = false;
    }

    protected <X extends Throwable, T> Number parseNumberOrThrow(String tryParse, Supplier<? extends X> supplier) throws X {
        Number i;
        try {
            i = NumberUtils.createNumber(tryParse);
        } catch (NumberFormatException e) {
            throw supplier.get();
        }
        return i;
    }

}
