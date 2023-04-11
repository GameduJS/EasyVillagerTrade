package de.gamedude.easyvillagertrade.scripting.core.script.actions.base;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.function.Supplier;

public abstract class Action implements Runnable {

    protected boolean finished = false;
    public boolean isFinished() {
        return finished;
    }

    public abstract void performAction();

    public abstract void reset();

    @Override
    public void run() {
        this.performAction();
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
