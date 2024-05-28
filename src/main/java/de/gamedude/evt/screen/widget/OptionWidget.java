package de.gamedude.evt.screen.widget;

import de.gamedude.evt.config.Config;
import de.gamedude.evt.screen.OptionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Function;

public class OptionWidget implements Selectable, Drawable, Element {

    public static final Config CONFIG = OptionScreen.CONFIG;
    private boolean focused;

    private final int x, y, width, height;
    private final String configKey, title;
    private final Class<?> valueTypeClass;

    private final TextFieldWidget textField;

    public OptionWidget(Class<?> valueTypeClass, String title, String configKey, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.title = title;
        this.configKey = configKey;
        this.valueTypeClass = valueTypeClass;

        this.textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x + 1, y + height / 2, width - 1, Math.max(height / 3, MinecraftClient.getInstance().textRenderer.fontHeight - 1), Text.of(""));
        this.textField.setMaxLength(50);
        this.textField.setDrawsBackground(false);
        this.textField.setPlaceholder(Text.of("No value..."));

        if (CONFIG.getProperty(configKey) != null)
            this.textField.setText(CONFIG.getProperty(configKey).getAsString());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(x, y, x + width, y + height, new Color(111, 111, 111, 255).getRGB());
        context.drawText(MinecraftClient.getInstance().textRenderer, title + ": ", x + 1, y + 1, Color.WHITE.getRGB(), false);
        context.fill(textField.getX(), textField.getY(), textField.getX() + textField.getWidth(), textField.getY() + textField.getHeight(), Color.GRAY.getRGB());
        this.textField.render(context, mouseX, mouseY, delta);
    }

    public void tick() {
        if (this.textField.isFocused())
            this.textField.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.textField.mouseClicked(mouseX, mouseY, button)) {
            this.textField.setFocused(true);
            this.textField.setEditableColor(14737632);
            return true;
        } else {
            parseValue();
            return isMouseOver(mouseX, mouseY);
        }
    }

    private void parseValue() {
        if(textField.getText().isBlank())
            return;
        switch (valueTypeClass.getSimpleName()) {
            case "Float" -> checkAndWrite(Number::floatValue);
            case "Integer" -> checkAndWrite(Number::intValue);
            case "Double" -> checkAndWrite(Number::doubleValue);
            case "Byte" -> checkAndWrite(Number::byteValue);
            case "Boolean" -> {
                boolean bl = Boolean.parseBoolean(textField.getText());
                CONFIG.addProperty(configKey, bl);
                textField.setText(bl + "");
            }
            case "SoundEvent" -> {
                Identifier identifier = new Identifier(textField.getText());
                if (!Registries.SOUND_EVENT.containsId(identifier)) {
                    textField.setEditableColor(Color.RED.getRGB());
                    return;
                }
                CONFIG.addProperty(configKey, identifier.getPath());
            }
        }
    }

    private void checkAndWrite(Function<Number, ?> function) {
        try {
            Number number = NumberUtils.createNumber(textField.getText());
            Object object = function.apply(number);
            textField.setText(object.toString());
            CONFIG.addProperty(configKey, object);
            textField.setEditableColor(14737632);
        } catch (NumberFormatException e) {
            textField.setEditableColor(Color.RED.getRGB());
        }
    }

    @Nullable
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return !this.isFocused() ? GuiNavigationPath.of(this) : null;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            unfocusTextField();
            parseValue();
        }
        return this.textField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.textField.charTyped(chr, modifiers);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return x <= mouseX && mouseX <= x + width && y <= mouseY && mouseY <= y + height;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) { }

    @Override
    public Selectable.SelectionType getType() {
        if (this.isFocused())
            return Selectable.SelectionType.FOCUSED;
        else
            return Selectable.SelectionType.NONE;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    public void unfocusTextField() {
        this.textField.setFocused(false);
    }
}
