package de.gamedude.evt.handler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class TradeWithVillagerHandler implements Handler {

    public void a() {
        /*ScreenHandler screenHandler = player().currentScreenHandler;
        if(!(screenHandler instanceof MerchantScreenHandler merchantScreen))
            return;
        merchantScreen.setRecipeIndex(recipeIndex);
        merchantScreen.switchTo(recipeIndex);
        player().networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(recipeIndex));

        // buy whatever the villager is selling
        MinecraftClient.getInstance().interactionManager.clickSlot(
                screenHandler.syncId, 2, recipeIndex,
                SlotActionType.PICKUP, player());
        */
    }

}
