package de.gamedude.easyvillagertrade.screen.widget;

import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class EnchantmentInputWidget extends EditBox {
    private String suggestion;

    private final Map<String, Holder.Reference<Enchantment>> cachedEnchantments = new HashMap<>();

    public EnchantmentInputWidget(int x, int y, int width, int height) {
        super(Minecraft.getInstance().font, x, y, width, height, Component.empty());
        this.setResponder( getChangeListener() );

        getRegistry().listElements().forEach(enchantmentReference -> {
            String key = enchantmentReference.value().description().getString().toLowerCase();
            this.cachedEnchantments.put(key, enchantmentReference);
        });

        System.out.println(
                Enchantment.getFullname(getRegistry().get(Enchantments.AQUA_AFFINITY).get(), 1)
        );
    }

    private Consumer<String> getChangeListener() {
        return text -> {
            String searchInput = text.trim().toLowerCase();

            boolean match = this.cachedEnchantments.containsKey(searchInput);
            if (match)
                this.setTextColor(ARGB.color(255, 255, 255, 0));
            else
                this.setTextColor(-2039584);

            String cleanInputForReplace = text.toLowerCase().replace("+", "");
            suggestion = getPossibleEnchantmentNameOrElse(searchInput)
                    .toLowerCase().replaceFirst(Pattern.quote(cleanInputForReplace), "");
            setSuggestion(suggestion);
        };
    }

    private String getPossibleEnchantmentNameOrElse(String input) {
        if ( input == null || input.isBlank() )
            return "";

        return this.cachedEnchantments.keySet().stream()
                .filter(cleanName -> cleanName.startsWith(input.toLowerCase()))
                .findFirst()
                .orElse("");
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if ( keyCode == GLFW.GLFW_KEY_ENTER ) {
            setEnchantmentText();
            return true;
        }
        return super.keyPressed(event);
    }

    /*
    @Nullable
    @Override
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        if (navigation.getDirection() == ScreenDirection.DOWN)
            FocusNavigationEvent
            setEnchantmentText();
        return super.getNavigationPath(navigation);
    }
     */

    private void setEnchantmentText() {
        setMessage(Component.literal(
                StringUtils.capitalize(getMessage().getString() + ((suggestion == null) ? "" : suggestion))
        ));
    }

    private HolderLookup.RegistryLookup<Enchantment> getRegistry() {
        return Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        //return VanillaRegistries.createLookup().lookupOrThrow(Registries.ENCHANTMENT);
    }
}