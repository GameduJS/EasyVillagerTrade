package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;

public class TradeRequestInputHandler {

    public TradeRequest handleCommandInput(Enchantment enchantment, int inputLevel, int maxPrice) {
        int level = mapLevel(enchantment, inputLevel);
        int price = mapPrice(maxPrice);

        return new TradeRequest(getRegistry().getEntry(enchantment), level, price);
    }

    public TradeRequest handleGUIInput(String enchantmentInput, String levelInput, String priceInput) {
        RegistryEntry<Enchantment> enchantment = getEnchantment(enchantmentInput);
        if (enchantment == null)
            return null;
        int level = isInteger(levelInput) ? mapLevel(enchantment.value(), Integer.parseInt(levelInput)) : -1;
        int maxPrice = isInteger(priceInput) ? mapPrice(Integer.parseInt(priceInput)) : -1;
        if (level == -1 || maxPrice == -1)
            return null;
        return new TradeRequest(enchantment, level, maxPrice);
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

    private boolean isInteger(String tryParse) {
        try {
            Integer.parseInt(tryParse);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private Registry<Enchantment> getRegistry() {
        return MinecraftClient.getInstance().world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
    }

}
