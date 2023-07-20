package de.gamedude.easyvillagertrade.utils;

public enum TradingState {
    CHECK_OFFERS(),
    WAIT_JOB_LOSS(),
    WAIT_PROFESSION(),
    BREAK_WORKSTATION(),
    PLACE_WORKSTATION(),
    INACTIVE(),
    MODE_SELECTION(),

    SELECT_TRADE(),
    APPLY_TRADE(),
    PICKUP_TRADE()
}
