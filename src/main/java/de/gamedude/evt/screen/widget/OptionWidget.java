package de.gamedude.evt.screen.widget;

import de.gamedude.evt.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OptionWidget<T> extends AbstractParentElement implements Selectable, Drawable {

    static final Identifier RESOURCE_PACKS_TEXTURE = new Identifier("textures/gui/resource_packs.png");
    private final int x, y, width, height;
    private final Config config = new Config("easyvillagertrade");
    private final String configKey, title;

    public OptionWidget(String title, String configKey, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.title = title;
        this.configKey = configKey;

    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(x, y, x + width, y + height, Color.GRAY.getRGB());
        context.drawText(MinecraftClient.getInstance().textRenderer, title, x, y, Color.WHITE.getRGB(), false);
        context.drawText(MinecraftClient.getInstance().textRenderer, config.getProperty(configKey).getAsString(), x + width / 2, y + height / 2, Color.WHITE.getRGB(), false);

        context.drawTexture(RESOURCE_PACKS_TEXTURE, x, y, 96.0F, 32.0F, 32, 32, 256, 256);
        context.drawTexture(RESOURCE_PACKS_TEXTURE, x, y + 100, 96.0F, 0.0F, 32, 32, 256, 256);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public List<? extends Element> children() {
        return List.of();
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    public static class Builder<T> {

        private int x,y,width,height;
        private final String configKey, title;

        public static <T> Builder<T> builder(String title, String configKey) {
            return new Builder<>(title, configKey);
        }

        private Builder(String title, String configKey) {
            this.title = title;
            this.configKey = configKey;
        }

        public Builder<T> dimension(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder<T> position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public OptionWidget<T> build() {
            return new OptionWidget<>(title, configKey, x, y, width, height);
        }

    }
}
