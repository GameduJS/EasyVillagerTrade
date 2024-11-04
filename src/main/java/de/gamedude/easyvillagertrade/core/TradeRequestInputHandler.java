package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class TradeRequestInputHandler {

    public int handleInputUI(String enchantmentInput, String levelInput, String priceInput, Consumer<TradeRequest> tradeRequestConsumer) {
        RegistryEntry<Enchantment> enchantmentEntry = getEnchantment(enchantmentInput);
        if(enchantmentEntry == null)
            return 1; // no valid enchantment
        if(notInt(priceInput))
            return 2; // no valid price
        Enchantment enchantment = enchantmentEntry.value();
        int price = MathHelper.clamp(Integer.parseInt(priceInput), 1, 64);

        if(levelInput.equals("*")) { // add all possible levels
            for(int levelIterator = 1; levelIterator <= enchantment.getMaxLevel(); levelIterator++) {
                TradeRequest request = new TradeRequest(enchantmentEntry, levelIterator, price);
                tradeRequestConsumer.accept(request);
            }
            return 0;
        }
        if(notInt(levelInput))
            return 3; // no valid level
        int level = MathHelper.clamp(Integer.parseInt(levelInput), 1, enchantment.getMaxLevel());

        TradeRequest request = new TradeRequest(enchantmentEntry, level, price);
        tradeRequestConsumer.accept(request);
        return 0;
    }

    public TradeRequest parseCommandInput(Enchantment enchantment, int inputLevel, int maxPrice) {
        int level = mapLevel(enchantment, inputLevel);
        int price = mapPrice(maxPrice);
        return new TradeRequest(getRegistry().getEntry(enchantment), level, price);
    }

    public RegistryEntry<Enchantment> getEnchantment(String enchantmentInput) {
        Registry<Enchantment> enchantmentRegistry = getRegistry();
        return enchantmentRegistry.stream().filter(enchantment -> enchantment.description().getString().equalsIgnoreCase(enchantmentInput.trim())).findFirst().map(enchantmentRegistry::getEntry).orElse(null);
    }

    private int mapPrice(int maxPriceInput) {
        return MathHelper.clamp(maxPriceInput, 1, 64);
    }

    private int mapLevel(Enchantment enchantment, int inputLevel) {
        return MathHelper.clamp(inputLevel, 1, enchantment.getMaxLevel());
    }

    private boolean notInt(String tryParse) {
        try {
            Integer.parseInt(tryParse);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }

    private Registry<Enchantment> getRegistry() {
        return MinecraftClient.getInstance().world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
    }

}
