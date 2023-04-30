package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.utils.TradingState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

public class TradeInterface {

    private final EasyVillagerTradeBase modBase;
    private final MinecraftClient minecraftClient;
    private int tradeSlotID;

    public TradeInterface(EasyVillagerTradeBase modBase) {
        this.modBase = modBase;
        this.minecraftClient = MinecraftClient.getInstance();
    }

    public void setTradeSlotID(int tradeSlotID) {
        this.tradeSlotID = tradeSlotID;
    }

    public void selectTrade() {
        modBase.handleInteractionWithVillager();
        minecraftClient.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(tradeSlotID));

        modBase.setState(TradingState.APPLY_TRADE);
    }

    public void applyTrade() {
        PlayerEntity player = minecraftClient.player;
        ScreenHandler currentScreenHandler = player.currentScreenHandler;
        minecraftClient.interactionManager.clickSlot(currentScreenHandler.syncId, 2, 0, SlotActionType.PICKUP, minecraftClient.player);

        modBase.setState(TradingState.PICKUP_TRADE);
    }

    public void pickupBook() {
        PlayerEntity player = minecraftClient.player;
        ScreenHandler currentScreenHandler = player.currentScreenHandler;
        int freeSlot = getFreeSlot();

        if (0 <= freeSlot && freeSlot <= 8)
            freeSlot += 30;
        else if (freeSlot != -999)
            freeSlot -= 6;
        else
            player.sendMessage(Text.translatable("evt.logic.book_drop"));

        minecraftClient.interactionManager.clickSlot(currentScreenHandler.syncId, freeSlot, 0, SlotActionType.PICKUP, minecraftClient.player);
        modBase.setState(TradingState.INACTIVE);
    }

    private int getFreeSlot() {
        DefaultedList<ItemStack> list = minecraftClient.player.getInventory().main;

        long sumOfEmpty = list.stream().filter(ItemStack::isEmpty).count();
        if (sumOfEmpty <= 2)
            return -999;
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).isEmpty())
                return i;
        }
        return -999;
    }
}
