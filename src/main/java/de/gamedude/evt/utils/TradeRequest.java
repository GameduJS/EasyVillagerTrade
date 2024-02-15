package de.gamedude.evt.utils;


import net.minecraft.enchantment.Enchantment;

public record TradeRequest(Enchantment enchantment, int level, int cost) {

    public boolean matchRequest(TradeRequest other) {
        return enchantment.getTranslationKey().equals(other.enchantment.getTranslationKey()) && level == other.level && cost <= other.cost;
    }

    @Override
    public String toString() {
        return "TradeRequest{" +
                "enchantment=" + enchantment +
                ", level=" + level +
                ", cost=" + cost +
                '}';
    }
}
