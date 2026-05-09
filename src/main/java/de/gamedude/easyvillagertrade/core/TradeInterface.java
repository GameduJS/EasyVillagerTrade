package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.utils.TradingState;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

public class TradeInterface {

    private final EasyVillagerTradeBase modBase;
    private final Minecraft minecraftClient;
    private int tradeSlotID;

    public TradeInterface(EasyVillagerTradeBase modBase) {
        this.modBase = modBase;
        this.minecraftClient = Minecraft.getInstance();
    }

    public void setTradeSlotID(int tradeSlotID) {
        this.tradeSlotID = tradeSlotID;
    }

    public void selectTrade() {
        modBase.handleInteractionWithVillager();
        minecraftClient.getConnection().send(new ServerboundSelectTradePacket(tradeSlotID));
        modBase.setState(TradingState.APPLY_TRADE);
    }

    public void applyTrade() {
        minecraftClient.player.containerMenu.clicked(2, 0, ContainerInput.PICKUP, minecraftClient.player);
        modBase.setState(TradingState.PICKUP_TRADE);
    }

    public void pickupBook() {
        Player player = minecraftClient.player;
        int freeSlot = getFreeSlot();

        if (0 <= freeSlot && freeSlot <= 8)
            freeSlot += 30;
        else if (freeSlot != -999)
            freeSlot -= 6;
        else
            player.sendOverlayMessage(Component.translatable("evt.logic.book_drop"));

        minecraftClient.player.containerMenu.clicked(freeSlot, 0, ContainerInput.PICKUP, minecraftClient.player);
        modBase.setState(TradingState.INACTIVE);
    }

    private int getFreeSlot() {
        NonNullList<ItemStack> list = minecraftClient.player.getInventory().getNonEquipmentItems();

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
