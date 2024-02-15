package de.gamedude.evt.handler;

import de.gamedude.old.config.Config;

public interface Handler {

    default void loadConfig(Config config) { }

    default void reloadConfig(Config config) { }

    default <T extends Handler> T getHandler(Class<T> clazz) {
        return null; // TODO:
        // return EasyVillagerTrade.getTradeWorkFlowHandler().getHandler(clazz);
    }

}
