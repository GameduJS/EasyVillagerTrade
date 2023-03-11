package de.gamedude.easyvillagertrade.commands;

import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandRegister {

    private final EasyVillagerTradeBase tradingHandler;
    public CommandRegister(EasyVillagerTradeBase tradingHandler) {
        this.tradingHandler = tradingHandler;
    }

    public void init() {
        ClientCommandRegistrationCallback.EVENT.register(new EasyVillagerTradeCommand(tradingHandler));
    }

}
