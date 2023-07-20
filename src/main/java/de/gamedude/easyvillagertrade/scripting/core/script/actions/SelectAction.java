package de.gamedude.easyvillagertrade.scripting.core.script.actions;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.scripting.core.script.actions.base.Action;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class SelectAction extends Action {

    @Override
    public void performAction() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null)
            return;
        EasyVillagerTrade.getModBase().getSelectionInterface().selectClosestToPlayer(player);
        finished = true;
    }
}
