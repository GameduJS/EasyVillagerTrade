package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.config.Config;

public interface ConfigDependent {

    void loadConfig(Config config);

    void reloadConfig(Config config);

    default <T extends ConfigDependent> T getHandler(Class<T> clazz) {
        return EasyVillagerTrade.getTradeWorkFlowHandler().getHandler(clazz);
    }

}
