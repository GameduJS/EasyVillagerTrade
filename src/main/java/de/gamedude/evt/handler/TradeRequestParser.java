package de.gamedude.evt.handler;

import de.gamedude.evt.utils.TradeRequest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class TradeRequestParser {

    private static final Registry<Enchantment> ENCHANTMENTS = Registries.ENCHANTMENT;

    public TradeRequest parseCommandInput(String enchantmentName, int level, int cost) {
        return parseUiInput(enchantmentName, String.valueOf(level), String.valueOf(cost));
    }

    public TradeRequest parseUiInput(String enchantmentName, String levelString, String costString) {
        if(!isInteger(levelString) || !isInteger(costString))
            return null;

        Enchantment enchantment = ENCHANTMENTS.get(new Identifier(enchantmentName));
        int level = MathHelper.clamp(Integer.parseInt(levelString), 1, enchantment.getMaxLevel());
        int cost = MathHelper.clamp(Integer.parseInt(costString), 1, 64);

        return new TradeRequest(enchantment, level, cost);
    }

    private boolean isInteger(String tryParse) {
        try {
            Integer.parseInt(tryParse);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
