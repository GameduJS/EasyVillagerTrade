package de.gamedude.old.render.widget;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;

public abstract class AbstractWidget implements Drawable, Element, Selectable {

    protected int x, y, width, height;
    protected boolean focused;

    public AbstractWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void updateXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return x <= mouseX && mouseX <= x + width && y <= mouseY && mouseY <= y + height;
    }

    @Override
    public void setFocused(boolean focused) { this.focused = focused; }
    @Override
    public boolean isFocused() { return focused; }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
