package de.gamedude.easyvillagertrade.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;

public record TradeRequest(RegistryEntry<Enchantment> enchantment, int level, int maxPrice) {

    public boolean matches(TradeRequest request) {
        return level == request.level && request.maxPrice >= maxPrice && equalEnchantment(enchantment, request.enchantment);
    }

    public static boolean equalEnchantment(RegistryEntry<Enchantment> ench1, RegistryEntry<Enchantment> ench2) {
        return  ench1.getIdAsString().equals(ench2.getIdAsString());
    }

    @Override
    public String toString() {
        return "TradeRequest{" +
                "enchantment=" + enchantment +
                ", level=" + level +
                ", maxPrice=" + maxPrice +
                '}';
    }
}
