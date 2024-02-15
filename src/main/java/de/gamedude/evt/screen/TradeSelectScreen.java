package de.gamedude.evt.screen;

import de.gamedude.evt.screen.widget.OptionWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class TradeSelectScreen extends Screen {

    private final int widgetWidth = 100;

    public TradeSelectScreen() {
        super(Text.empty());
    }

    @Override
    protected void init() {
        int px = (int) (this.width / 50f);
        int x = this.width - widgetWidth - px;
        this.addDrawableChild(OptionWidget.Builder.<Integer>builder("Distance X", "distanceX").dimension(50, 30).position(x, px).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context) {
        int px = (int) (this.width / 50f);
        int x = this.width - px - widgetWidth;
        context.fill(x, px, this.width - px, this.height - px, ColorHelper.Argb.getArgb(150, 7, 7, 7));
    }

}
