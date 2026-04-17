package de.gamedude.easyvillagertrade.utils;


import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;

public record TradeRequest(Holder<Enchantment> enchantmentHolder, int level, int maxPrice) {

    public boolean matches(TradeRequest request) {
        return level == request.level && request.maxPrice >= maxPrice && equalEnchantment(enchantmentHolder, request.enchantmentHolder);
    }

    public Component getNameEnchantment() {
        return Enchantment.getFullname(enchantmentHolder, level);
    }

    public static boolean equalEnchantment(Holder<Enchantment> ench1, Holder<Enchantment> ench2) {
        return  ench1.value().description().equals(ench2.value().description());
    }

    @Override
    public String toString() {
        return "TradeRequest{" +
                "enchantment=" + enchantmentHolder +
                ", level=" + level +
                ", maxPrice=" + maxPrice +
                '}';
    }
}
