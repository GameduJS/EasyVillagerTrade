package de.gamedude.easyvillagertrade.screen.widget;

import joptsimple.internal.Strings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.function.Consumer;

public class EnchantmentInputWidget extends TextFieldWidget {

    private static final Registry<Enchantment> ENCHANTMENT_REGISTRY = Registries.ENCHANTMENT;
    private String suggestion;

    public EnchantmentInputWidget(int x, int y, int width, int height) {
        super(MinecraftClient.getInstance().textRenderer, x, y, width, height, Text.empty());
        setSuggestion("Enchantment");

        this.setChangedListener(getChangeListener());
    }

    private Consumer<String> getChangeListener() {
        return text -> {
            if (ENCHANTMENT_REGISTRY.stream().map(Enchantment::getTranslationKey).map(Text::translatable).map(Text::getString).anyMatch(text.trim()::equalsIgnoreCase))
                this.setEditableColor(ColorHelper.Argb.getArgb(255, 255, 255, 0));
            else
                this.setEditableColor(0xE0E0E0);

            suggestion = getPossibleEnchantmentNameOrElse(text).toLowerCase().replaceFirst(text.toLowerCase().replace("+", ""), "");
            setSuggestion(suggestion);
        };
    }

    private String getPossibleEnchantmentNameOrElse(String input) {
        String enchantmentName = null;
        for(Enchantment enchantment : ENCHANTMENT_REGISTRY) {
            boolean multipleLevels = enchantment.getMaxLevel() == 1;
            String[] parts = enchantment.getName(1).getString().split(" ");
            String name = Strings.join((multipleLevels) ? parts : Arrays.copyOf(parts, parts.length - 1), " ");

            if(name.toLowerCase().startsWith(input.toLowerCase())) {
                enchantmentName = name;
                break;
            }
        }
        if (enchantmentName == null)
            enchantmentName = "";
        return enchantmentName;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_ENTER) {
            setEnchantmentText();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        if (navigation instanceof GuiNavigation.Tab)
            setEnchantmentText();
        return super.getNavigationPath(navigation);
    }

    private void setEnchantmentText() {
        setText(StringUtils.capitalize(getText() + ((suggestion == null) ? "": suggestion)));
    }
}
