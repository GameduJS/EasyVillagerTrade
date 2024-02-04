package de.gamedude.old.render.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import org.jetbrains.annotations.Nullable;

public abstract class CustomParentWidget extends AbstractWidget implements ParentElement {

    public CustomParentWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public boolean isFocused() {
        return super.isFocused();
    }

    @Override
    public void setFocused(@Nullable Element focused) {

    }

    @Override
    public void setDragging(boolean dragging) {

    }

    @Override
    public Element getFocused() {
        return null;
    }

}
