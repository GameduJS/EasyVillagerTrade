package de.gamedude.easyvillagertrade.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class EnchantmentInputWidget extends EditBox {

    private static final int COLOR_VALID = ARGB.color(255, 255, 255, 0);
    private static final int COLOR_DEFAULT = 0xFF_E0E0E0;

    private String suggestion = "";

    private final Map<String, Holder.Reference<Enchantment>> cachedEnchantments = new HashMap<>();

    public EnchantmentInputWidget(int x, int y, int width, int height) {
        super(Minecraft.getInstance().font, x, y, width, height, Component.empty());
        buildEnchantmentCache();
        setResponder(buildChangeListener());
    }

    private void buildEnchantmentCache() {
        getEnchantmentRegistry().listElements().forEach(ref -> {
            String key = ref.value().description().getString().toLowerCase();
            cachedEnchantments.put(key, ref);
        });
    }

    private Consumer<String> buildChangeListener() {
        return text -> {
            String input = text.trim().toLowerCase();
            String cleanInput = text.toLowerCase().replace("+", "");

            setTextColor(cachedEnchantments.containsKey(input) ? COLOR_VALID : COLOR_DEFAULT);

            String match = findFirstMatchingName(input);
            suggestion = match.toLowerCase().replaceFirst(Pattern.quote(cleanInput), "");
            setSuggestion(suggestion);
        };
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ENTER) {
            applyCurrentSuggestion();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent navigationEvent) {
        if (navigationEvent.getVerticalDirectionForInitialFocus() == ScreenDirection.DOWN)
            applyCurrentSuggestion();
        return super.nextFocusPath(navigationEvent);
    }

    private void applyCurrentSuggestion() {
        if (suggestion == null || suggestion.isBlank()) return;

        String completed = getValue() + suggestion;
        setValue(StringUtils.capitalize(completed));
        setSuggestion("");
        suggestion = "";

        setTextColor(cachedEnchantments.containsKey(completed.toLowerCase())
                ? COLOR_VALID : COLOR_DEFAULT);
    }

    private String findFirstMatchingName(String input) {
        if (input == null || input.isBlank()) return "";

        return cachedEnchantments.keySet().stream()
                .filter(name -> name.startsWith(input))
                .findFirst()
                .orElse("");
    }

    private HolderLookup.RegistryLookup<Enchantment> getEnchantmentRegistry() {
        return Minecraft.getInstance().level
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT);
    }
}