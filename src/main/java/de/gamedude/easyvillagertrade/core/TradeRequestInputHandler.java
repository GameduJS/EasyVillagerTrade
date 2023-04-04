package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public class TradeRequestInputHandler {

    private static final Registry<Enchantment> ENCHANTMENT_REGISTRY = Registry.ENCHANTMENT;

    public TradeRequest handleCommandInput(Enchantment enchantment, int inputLevel, int maxPrice) {
        int level = mapLevel(enchantment, inputLevel);
        int price = mapPrice(maxPrice);

        return new TradeRequest(enchantment, level, price);
    }

    public TradeRequest handleGUIInput(String enchantmentInput, String levelInput, String priceInput) {
        Enchantment enchantment = getEnchantment(enchantmentInput);
        if(enchantment == null)
            return null;
        int level = isInteger(levelInput) ? mapLevel(enchantment, Integer.parseInt(levelInput)) : -1;
        int maxPrice = isInteger(priceInput) ? mapPrice(Integer.parseInt(priceInput)) : - 1;
        if(level == -1 || maxPrice == -1)
            return null;
        return new TradeRequest(enchantment, level, maxPrice);
    }

    public Enchantment getEnchantment(String enchantmentInput) {
        for(Enchantment enchantment : ENCHANTMENT_REGISTRY)
            if(Text.translatable(enchantment.getTranslationKey()).getString().equals(enchantmentInput))
                return enchantment;
        return null;
    }

    private int mapPrice(int maxPriceInput) {
        int price = Math.min(maxPriceInput, 64);
        price = Math.max(price, 1);
        return price;
    }
    private int mapLevel(Enchantment enchantment, int inputLevel) {
        int finalLevel = enchantment.getMaxLevel();

        if(inputLevel <= 0)
            finalLevel = 1;
        else if(inputLevel < finalLevel)
            finalLevel = inputLevel;
        return finalLevel;
    }
    private boolean isInteger(String tryParse) {
        try {
            Integer.parseInt(tryParse);
        } catch( NumberFormatException e) {
            return false;
        }
        return true;
    }

}
