package de.gamedude.evt.handler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.function.Predicate;
import java.util.stream.IntStream;

public class TradeWithVillagerHandler implements Handler {

    private int slotIndex;

    public void setTradeIndex(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    public void buy() {
        ScreenHandler screenHandler = player().currentScreenHandler;
        if(!(screenHandler instanceof MerchantScreenHandler merchantScreen))
            return;
        merchantScreen.setRecipeIndex(slotIndex);
        merchantScreen.switchTo(slotIndex);
        player().networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(slotIndex));

        MinecraftClient.getInstance().interactionManager.clickSlot(
                screenHandler.syncId, 2, 0,
                SlotActionType.PICKUP, player());

        int slotToClick = player().getInventory().getEmptySlot();
        if(slotToClick <= 8) slotToClick+=30; // Move to hotbar

        long emptySlotsInHandled = IntStream.of(0, 1).mapToObj(screenHandler::getSlot).filter(Predicate.not(Slot::hasStack)).count();
        if(player().getInventory().main.stream().filter(ItemStack::isEmpty).count() <= 2 - emptySlotsInHandled)
            slotToClick = -999;

        if(slotToClick == -999)
            player().sendMessage(Text.of("§cBook dropped out of inventory"));
        MinecraftClient.getInstance().interactionManager.clickSlot(screenHandler.syncId, slotToClick, 0, SlotActionType.PICKUP, player());

        player().closeHandledScreen();
    }


}
