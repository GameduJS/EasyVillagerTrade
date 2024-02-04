package de.gamedude.old.screen;

import de.gamedude.old.config.Config;
import de.gamedude.old.core.TradeWorkflowHandler;
import de.gamedude.old.screen.widget.OptionWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.awt.*;


public class ConfigScreen extends Screen {

    private final Identifier BACKGROUND_TEXTURE = new Identifier("easyvillagertrade", "textures/gui/config_background.png");

    private final Config config;

    protected ConfigScreen(TradeWorkflowHandler tradeWorkflowHandler) {
        super(Text.of("Config Screen"));
        this.config = tradeWorkflowHandler.getConfig();
    }

    @Override
    protected void init() {
        int y = (int) (this.width / 50f);
        int x = this.width - y - TradeSelectScreen.widgetWidth + 10;
        y+=10;

        TextFieldWidget textFieldWidget = new TextFieldWidget(textRenderer, x, y += 40, 40, 30, Text.of(config.getProperty("soundPlayed").getAsString()));
        this.addDrawableChild(textFieldWidget);
        this.addDrawableChild(new TextWidget(x, y + 40, textRenderer.getWidth("Option1"), y + 80, Text.of("§7Option 1"), textRenderer));


        this.addDrawableChild(OptionWidget.Builder.<Integer>builder("§cX §d§lChange", "distanceX").position(x, y + 80).dimension(40, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int px = (int) (this.width / 50f);
        int x = this.width - px - TradeSelectScreen.widgetWidth;
        int margin = 10;

        context.fill(x, px, this.width - px, this.height - px, ColorHelper.Argb.getArgb(150, 7, 7, 7));

        context.getMatrices().push();
        context.getMatrices().scale(4, 4, 1f);
        context.drawText(this.textRenderer, "CONFIG", (x + margin) / 4, (px + margin) / 4, Color.GRAY.getRGB(), false);
        context.getMatrices().pop();
    }
}
