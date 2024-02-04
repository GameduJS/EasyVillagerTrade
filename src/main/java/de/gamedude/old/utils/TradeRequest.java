package de.gamedude.old.utils;

import net.minecraft.enchantment.Enchantment;

public record TradeRequest(Enchantment enchantment, int level, int maxPrice) {

    public boolean matches(TradeRequest request) {
        return level == request.level && request.maxPrice >= maxPrice && enchantment.getTranslationKey().equals(request.enchantment.getTranslationKey());
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
