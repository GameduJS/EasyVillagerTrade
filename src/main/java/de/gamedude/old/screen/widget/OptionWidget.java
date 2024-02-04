package de.gamedude.old.screen.widget;

import com.google.gson.JsonPrimitive;
import de.gamedude.old.EasyVillagerTrade;
import de.gamedude.old.config.Config;
import de.gamedude.old.render.widget.CustomParentWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OptionWidget<T> extends CustomParentWidget {

    private final Config config = EasyVillagerTrade.getTradeWorkFlowHandler().getConfig();
    private final String configKey, title;
    private final List<Element> widgets;
    private WidgetBase<T> widget;

    public OptionWidget(String title, String configKey, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.widgets = new ArrayList<>();
        this.title = title;
        this.configKey = configKey;
        this.init();
    }

    private void init() {
        widgets.add(createWidget());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(x, y, x + width, y + height, Color.GRAY.getRGB());
        context.drawText(MinecraftClient.getInstance().textRenderer, title, x, y, Color.WHITE.getRGB(), false);
        context.drawText(MinecraftClient.getInstance().textRenderer, config.getProperty(configKey).getAsString(), x + width / 2, y + height / 2, Color.WHITE.getRGB(), false);
        children().stream().filter(element -> element instanceof Drawable).map(Drawable.class::cast).forEach(drawable -> drawable.render(context, mouseX, mouseY, delta));
    }

    public void setValue(T t) {
        config.addProperty(configKey, t);
    }

    public T getValue() {
        return widget.getValue();
    }

    @Override
    public List<? extends Element> children() {
        return widgets;
    }



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return hoveredElement(mouseX, mouseY).isPresent() && hoveredElement(mouseX, mouseY).get().mouseClicked(mouseX, mouseY, button);
    }

    private Element createWidget() {
        JsonPrimitive jsonPrimitive = config.getProperty(configKey).getAsJsonPrimitive();
        if (jsonPrimitive.isBoolean()) {
            return new BooleanWidget(x + width / 2 - 40, y + height / 2 - 10, 80, 20);
        } else if (jsonPrimitive.isNumber()) {
            return new NumberWidget(x + width / 2 - 40, y + height / 2 - 10, 80, 20);
        } else {
            return new StringWidget(x + width / 2 - 40, y + height / 2 - 10, 80, 20);
        }
    }

    private interface WidgetBase<T> extends Drawable {
        T getValue();
    }

    private static class BooleanWidget extends TextFieldWidget implements WidgetBase<Boolean> {
        public BooleanWidget(int x, int y, int width, int height) {
            super(MinecraftClient.getInstance().textRenderer, x, y, width, height, Text.of(""));
        }

        @Override
        public Boolean getValue() {
            return Boolean.valueOf(getText());
        }
    }

    private static class NumberWidget extends TextFieldWidget implements WidgetBase<Integer> {
        public NumberWidget(int x, int y, int width, int height) {
            super(MinecraftClient.getInstance().textRenderer, x, y, width, height, Text.of(""));
        }

        @Override
        public Integer getValue() {
            return Integer.valueOf(getText());
        }
    }

    private static class StringWidget extends TextFieldWidget implements WidgetBase<String> {
        public StringWidget(int x, int y, int width, int height) {
            super(MinecraftClient.getInstance().textRenderer, x, y, width, height, Text.of(""));
        }

        @Override
        public String getValue() {
            return  getText();
        }
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
