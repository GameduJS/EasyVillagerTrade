package de.gamedude.easyvillagertrade.scripting.core.script.actions;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.scripting.core.script.actions.base.Action;
import net.minecraft.client.MinecraftClient;

public class SelectAction extends Action {

    @Override
    public void performAction() {
        EasyVillagerTrade.getModBase().getSelectionInterface().selectClosestToPlayer(MinecraftClient.getInstance().player);
        finished = true;
    }

    @Override
    public void reset() {
        finished = false;
    }
}
