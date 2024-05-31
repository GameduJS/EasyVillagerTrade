package de.gamedude.evt.screen;

import de.gamedude.evt.config.Config;
import de.gamedude.evt.screen.widget.OptionListWidget;
import de.gamedude.evt.screen.widget.OptionWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class OptionScreen extends Screen {

    private final int widgetWidth;
    public static final Config CONFIG = new Config("easyvillagertrade");

    public OptionScreen(int widgetWidth) {
        super(Text.empty());
        this.widgetWidth = widgetWidth;
    }

    @Override
    protected void init() {
        int px = (int) (this.width / 50f);
        int x = this.width - widgetWidth - px;
        int y = px + 20;

        OptionListWidget optionListWidget = new OptionListWidget(widgetWidth  - 10, height - 4 * px, 2 * px, this.height - 2 * px, 30);
        optionListWidget.setLeftPos(x + 5);

        this.addDrawableChild(optionListWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int px = (int) (this.width / 50f);
        int x = this.width - widgetWidth - px;

        this.renderBackground(context);

        float scale = (float) Math.max(9, px) / 9;
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1f);
        context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, "CONFIG", (int) ((x + widgetWidth / 2) / scale), (int) ((px + scale) / scale), -1);
        context.getMatrices().pop();

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context) {
        int px = (int) (this.width / 50f);
        int x = this.width - px - widgetWidth;
        context.fill(x, px, this.width - px, this.height - px, ColorHelper.Argb.getArgb(150, 7, 7, 7));
    }

    @Override
    public void close() {
        CONFIG.savePropertiesToFile();
        super.close();
    }
}
