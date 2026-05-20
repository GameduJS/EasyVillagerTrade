package de.gamedude.easyvillagertrade.core.interventions;


import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.utils.TradingState;

/**
 * Checks if a villager refuses his job.
 * This can happen in crowded areas with many jobless villagers causing the program to get "stuck".
 * -> Other villagers might be interested in the placed lectern.
 */

public class NoJobIntervention {

    private final EasyVillagerTradeBase base;
    private int ticks;

    private static final int MAX_WAIT_TICKS = 60;


    public NoJobIntervention(EasyVillagerTradeBase base) {
        this.base = base;
    }

    public void checkSystem(TradingState state) {
        if ( state != TradingState.WAIT_PROFESSION )
            this.ticks = 0;
        this.ticks++;

        if ( shouldIntervene() )
            this.trigger();
    }

    private boolean shouldIntervene() {
        return this.ticks >= MAX_WAIT_TICKS;
    }

    private void trigger() {
        this.ticks = 0;
        this.base.setState(TradingState.BREAK_WORKSTATION);
    }

}
