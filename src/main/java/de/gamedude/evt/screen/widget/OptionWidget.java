package de.gamedude.evt.screen.widget;

import de.gamedude.evt.config.Config;
import de.gamedude.evt.screen.OptionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class OptionWidget extends AlwaysSelectedEntryListWidget.Entry<OptionWidget> {

    public static final Config CONFIG = OptionScreen.CONFIG;

    private final String configKey, title;
    private final Class<?> valueTypeClass;
    private TextFieldWidget textField;

    public OptionWidget(Class<?> valueTypeClass, String title, String configKey) {
        this.title = title;
        this.configKey = configKey;
        this.valueTypeClass = valueTypeClass;
    }

    // TODO: suggestion completion
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
                Identifier identifier = Identifier.tryParse(textField.getText());
                if (identifier == null || !Registries.SOUND_EVENT.containsId(identifier)) {
                    textField.setEditableColor(Color.RED.getRGB());
                    return;
                }
                CONFIG.addProperty(configKey, identifier.getPath());
            }
        }
    }

    private void trySuggestion() {
        String text = textField.getText();
        switch (valueTypeClass.getSimpleName()) {
            case "SoundEvent" -> Registries.SOUND_EVENT.stream().map(SoundEvent::getId).filter(identifier -> identifier.getPath().startsWith(text)).findFirst()
                    .ifPresentOrElse(identifier -> this.textField.setSuggestion(identifier.getPath().replaceFirst(text, "")), () -> this.textField.setSuggestion(""));
            case "Integer" -> this.textField.setSuggestion(text.isBlank() ? "1" : "");
            case "Float" -> this.textField.setSuggestion(text.isBlank() ? "1.0" : "");
            case "Boolean" -> Stream.of("true", "false").filter(s -> s.startsWith(text.toLowerCase())).findFirst()
                    .ifPresentOrElse(s -> this.textField.setSuggestion(s.replaceFirst(text.toLowerCase(), "")), () -> this.textField.setSuggestion(""));
        }
    }

    private void checkAndWrite(Function<Number, ?> function) {
        try {
            Number number = NumberUtils.createNumber(textField.getText());
            Object object = function.apply(number);
            textField.setText(object.toString());
            CONFIG.addProperty(configKey, object);
            textField.setEditableColor(-1);
        } catch (NumberFormatException e) {
            textField.setEditableColor(Color.RED.getRGB());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.textField == null)
            return isMouseOver(mouseX, mouseY);
        if (this.textField.mouseClicked(mouseX, mouseY, button)) {
            this.textField.setEditableColor(-1);
            return true;
        } else {
            parseValue();
            return isMouseOver(mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(this.textField == null)
            return false;
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            this.textField.setFocused(false);
            parseValue();
        }
        if(this.textField.keyPressed(keyCode, scanCode, modifiers)) {
            trySuggestion();
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(this.textField != null) {
            if(this.textField.charTyped(chr, modifiers)) {
                trySuggestion();
                return true;
            }
        }
        return false;
    }

    @Override
    public void setFocused(boolean focused) {
        this.textField.setFocused(focused);
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if(textField == null) {
            this.textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x, y + entryHeight / 2 , entryWidth, entryHeight / 2, Text.empty());
            this.textField.setMaxLength(50);
            this.textField.setEditableColor(-1);
            if (CONFIG.getProperty(configKey) != null)
                this.textField.setText(CONFIG.getProperty(configKey).getAsString());
            trySuggestion();
        }

        context.fill(x, y, x + entryWidth, y + entryHeight,  ColorHelper.Argb.getArgb(255,255, 192, 203));
        context.drawBorder(textField.getX(), textField.getY(), textField.getWidth(), textField.getHeight(), ColorHelper.Argb.getArgb(255,200, 180, 120));
        context.drawText(MinecraftClient.getInstance().textRenderer, title + ": ", x + 1, y +1, Color.decode("#009999").getRGB()  , false);

        this.textField.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public Text getNarration() {
        return Text.literal(title);
    }
}
