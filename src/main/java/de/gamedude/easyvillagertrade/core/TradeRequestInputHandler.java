package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Consumer;

public class TradeRequestInputHandler {

    public int handleInputUI(String enchantmentInput, String levelInput, String priceInput, Consumer<TradeRequest> tradeRequestConsumer) {
        Holder<Enchantment> enchantmentEntry = getEnchantment(enchantmentInput);
        if(enchantmentEntry == null)
            return 1; // no valid enchantment
        if(notInt(priceInput))
            return 2; // no valid price
        int price = Math.clamp(Integer.parseInt(priceInput), 1, 64);

        if(levelInput.equals("*")) { // add all possible levels
            for(int levelIterator = 1; levelIterator <= enchantmentEntry.value().getMaxLevel(); levelIterator++) {
                TradeRequest request = new TradeRequest(enchantmentEntry, levelIterator, price);
                tradeRequestConsumer.accept(request);
            }
            return 0;
        }
        if(notInt(levelInput))
            return 3; // no valid level
        int level = Math.clamp(Integer.parseInt(levelInput), 1, enchantmentEntry.value().getMaxLevel());

        TradeRequest request = new TradeRequest(enchantmentEntry, level, price);
        tradeRequestConsumer.accept(request);
        return 0;
    }

    public TradeRequest parseCommandInput(Holder<Enchantment> enchantment, int inputLevel, int maxPrice) {
        int level = mapLevel(enchantment.value(), inputLevel);
        int price = mapPrice(maxPrice);
        return new TradeRequest(enchantment, level, price);
    }


    public Holder<Enchantment> getEnchantment(String enchantmentInput) {
        HolderLookup.RegistryLookup<Enchantment> registry = getRegistry();
        return registry.listElements().filter(enchantmentReference -> enchantmentReference.value().description().getString()
                .equalsIgnoreCase(enchantmentInput.trim())).findFirst().orElse(null);
    }

    private int mapPrice(int maxPriceInput) {
        return Math.clamp(maxPriceInput, 1, 64);
    }

    private int mapLevel(Enchantment enchantment, int inputLevel) {
        return Math.clamp(inputLevel, 1, enchantment.getMaxLevel());
    }

    private boolean notInt(String tryParse) {
        try {
            Integer.parseInt(tryParse);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }

    private HolderLookup.RegistryLookup<Enchantment> getRegistry() {
        return Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
    }

}
