package de.gamedude.old.core;

import de.gamedude.old.EasyVillagerTrade;
import de.gamedude.old.config.Config;

public interface ConfigDependent {

    void loadConfig(Config config);

    void reloadConfig(Config config);

    default <T extends ConfigDependent> T getHandler(Class<T> clazz) {
        return EasyVillagerTrade.getTradeWorkFlowHandler().getHandler(clazz);
    }

}
