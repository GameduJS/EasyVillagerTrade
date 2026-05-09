package de.gamedude.easyvillagertrade.screen.widget;

import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.function.Consumer;

public class EnchantmentInputWidget extends EditBox {
    private String suggestion;

    public EnchantmentInputWidget(int x, int y, int width, int height) {
        super(Minecraft.getInstance().font, x, y, width, height, Component.empty());
        this.setResponder( getChangeListener() );
    }

    private Consumer<String> getChangeListener() {
        return text -> {
            HolderLookup.RegistryLookup<Enchantment> registryLookup = getRegistry();

            boolean match = registryLookup.listElements().map(Holder.Reference::value).map(Enchantment::description).map(Component::getString).anyMatch(text.trim()::equalsIgnoreCase);
            if ( match )
                this.setTextColor(.getArgb(255, 255, 255, 0));
            else
                this.setTextColor(-2039584);

            suggestion = getPossibleEnchantmentNameOrElse(text).toLowerCase().replaceFirst(text.toLowerCase().replace("+", ""), "");
            setSuggestion(suggestion);
        };
    }

    private String getPossibleEnchantmentNameOrElse(String input) {
        String enchantmentName = null;
        for(Enchantment enchantment : getRegistry()) {
            boolean multipleLevels = enchantment.getMaxLevel() == 1;
            String[] parts = Enchantment.getName(getRegistry().getEntry(enchantment), 1).getString().split(" ");
            String name = Strings.join((multipleLevels) ? parts : Arrays.copyOf(parts, parts.length - 1), " ");

            if(name.toLowerCase().startsWith(input.toLowerCase())) {
                enchantmentName = name;
                break;
            }
        }
        if (enchantmentName == null)
            return "";
        return enchantmentName;
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        int keyCode = keyInput.key();
        if(keyCode == GLFW.GLFW_KEY_ENTER) {
            setEnchantmentText();
            return true;
        }
        return super.keyPressed(keyInput);
    }

    @Nullable
    @Override
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        if (navigation.getDirection() == NavigationDirection.DOWN)
            setEnchantmentText();
        return super.getNavigationPath(navigation);
    }

    private void setEnchantmentText() {
        setText(StringUtils.capitalize(getText() + ((suggestion == null) ? "": suggestion)));
    }

    private HolderLookup.RegistryLookup<Enchantment> getRegistry() {
        return VanillaRegistries.createLookup().lookupOrThrow(Registries.ENCHANTMENT);
    }
}