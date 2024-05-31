package de.gamedude.evt.handler;

import de.gamedude.evt.utils.TradeRequest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class TradeRequestParser implements Handler {

    private static final Registry<Enchantment> ENCHANTMENTS = Registries.ENCHANTMENT;

    public TradeRequest parse(Enchantment enchantment, int levelRaw, int costRaw) {
        int level = MathHelper.clamp(levelRaw, 1, enchantment.getMaxLevel());
        int cost = MathHelper.clamp(costRaw, 1, 64);
        return new TradeRequest(enchantment, level, cost);
    }

    public TradeRequest parseUiInput(String enchantmentName, String levelString, String costString) {
        if(!isInteger(levelString) || !isInteger(costString))
            return null;

        Enchantment enchantment = getEnchantment(enchantmentName);
        if(enchantment == null)
            return null;
        return parse(enchantment, Integer.parseInt(levelString), Integer.parseInt(costString));
    }

    private boolean isInteger(String tryParse) {
        try {
            Integer.parseInt(tryParse);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Enchantment getEnchantment(String input) {
        return ENCHANTMENTS.stream().filter(enchantment -> Text.translatable(enchantment.getTranslationKey()).getString().equalsIgnoreCase(input.trim())).findFirst().orElse(null);
    }

}
