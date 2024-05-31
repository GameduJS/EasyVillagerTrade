package de.gamedude.evt.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class OptionListWidget extends AlwaysSelectedEntryListWidget<OptionWidget> {

    public OptionListWidget(int width, int height, int top, int bottom, int itemHeight) {
        super(MinecraftClient.getInstance(), width, height, top, bottom, itemHeight);
        this.setRenderBackground(false);
        this.setRenderSelection(false);
        this.setRenderHorizontalShadows(false);
        this.addEntry(new OptionWidget(Boolean.class, "Play Sound", "shouldPlaySound"));
        this.addEntry(new OptionWidget(Integer.class, "Offset X", "distanceX"));
        this.addEntry(new OptionWidget(Float.class, "Offset Z", "distanceZ"));
        this.addEntry(new OptionWidget(SoundEvent.class, "Sound", "soundPlayed"));
    }

    @Override
    public int getRowLeft() {
        return this.left;
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int getScrollbarPositionX() { // TODO: CHEK!!!!
        return this.right + 5;
    }

    @Nullable
    @Override
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        if(navigation instanceof GuiNavigation.Tab && isFocused()) {
            OptionWidget entry = getNeighboringEntry(navigation.getDirection(), Objects::nonNull);
            if(entry == null)
                return null;
            if(children().indexOf(entry) >= children().size())
                return null;
            return GuiNavigationPath.of(this, GuiNavigationPath.of(entry));
        }
        return super.getNavigationPath(navigation);
    }
}
