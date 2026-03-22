package de.gamedude.evt.handler;

import de.gamedude.evt.automation.Feedback;
import de.gamedude.evt.utils.TradeRequest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.function.Predicate;
import java.util.stream.IntStream;

public class TradeWithVillagerHandler implements Handler {

    private int slotIndex = -1;
    private TradeRequest requestedTrade;

    public boolean shouldSwitchState() {
        return slotIndex != -1;
    }

    public void setTradeIndex(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    public void setRequestedTrade(TradeRequest requestedTrade) {
        this.requestedTrade = requestedTrade;
    }

    public TradeRequest getRequestedTrade() {
        return requestedTrade;
    }

    public Feedback buy() {
        ScreenHandler screenHandler = player().currentScreenHandler;
        if(!(screenHandler instanceof MerchantScreenHandler merchantScreen)) // normally this should not pop up
            return Feedback.FAILURE.with("[No merchantscreenhandler]");
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
            return Feedback.FAILURE.with("{DEV Note:} Inventory is full");

        MinecraftClient.getInstance().interactionManager.clickSlot(screenHandler.syncId, slotToClick, 0, SlotActionType.PICKUP, player());

        this.slotIndex = -1;
        return Feedback.SUCCESS; //TODO can add message here instead of BuyState class
    }


}
