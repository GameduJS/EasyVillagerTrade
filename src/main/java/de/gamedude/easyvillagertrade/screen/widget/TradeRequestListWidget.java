package de.gamedude.easyvillagertrade.screen.widget;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Predicate;

public class TradeRequestListWidget extends AbstractSelectionList<TradeRequestListWidget.TradeRequestEntry> {

    private static final int ENTRY_HEIGHT = 36;
    private static final int ENTRY_PADDING = 4;
    private static final int BORDER_THICKNESS = 1;
    private static final int COLOR_BORDER = 0xFFAAAAAA;
    private static final int COLOR_BG = ARGB.color(220, 10, 10, 10);
    private static final int COLOR_BG_HOVER = ARGB.color(240, 35, 35, 35);
    private static final int COLOR_SEPARATOR = ARGB.color(80, 200, 200, 200);

    private final EasyVillagerTradeBase modBase;

    public TradeRequestListWidget(int x, int y, int width, int height) {
        super(Minecraft.getInstance(), width, height, y, ENTRY_HEIGHT + ENTRY_PADDING);
        this.modBase = EasyVillagerTrade.getModBase();
        this.setX(x);
    }

    @Override
    public int getRowLeft() {
        return getX() + ENTRY_PADDING;
    }

    @Override
    public int getRowWidth() {
        return getWidth() - 12;
    }

    @Override
    protected int scrollBarX() {
        return getX() + getWidth() - 6;
    }


    @Override
    protected void extractListSeparators(@NonNull GuiGraphicsExtractor graphics) {
    }

    @Override
    public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        renderBorder(graphics);
        super.extractWidgetRenderState(graphics, mouseX, mouseY, delta);
    }

    private void renderBorder(GuiGraphicsExtractor graphics) {
        int x = getX();
        int y = getY();
        int x2 = x + getWidth();
        int y2 = y + getHeight();
        int t = BORDER_THICKNESS;

        graphics.fill(x - t, y - t, x2 + t, y, COLOR_BORDER); // top
        graphics.fill(x - t, y2, x2 + t, y2 + t, COLOR_BORDER); // bottom
        graphics.fill(x - t, y, x, y2, COLOR_BORDER); // left
        graphics.fill(x2, y, x2 + t, y2, COLOR_BORDER); // right
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        TradeRequestEntry clicked = getEntryAtPosition(event.x(), event.y());
        if (clicked != null) {
            removeEntry(clicked);
            modBase.getTradeRequestContainer().removeTradeRequest(clicked.tradeRequest);
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth()
                && mouseY >= getY() && mouseY <= getY() + getHeight();
    }

    public void addEntry(TradeRequest request) {
        super.addEntry(new TradeRequestEntry(request));
    }

    public List<TradeRequest> removeEntry(Predicate<TradeRequest> predicate) {
        List<TradeRequestEntry> toRemove = children().stream()
                .filter(e -> predicate.test(e.tradeRequest))
                .toList();

        toRemove.forEach(super::removeEntry);
        return toRemove.stream().map(e -> e.tradeRequest).toList();
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) { }


    public static class TradeRequestEntry extends AbstractSelectionList.Entry<TradeRequestEntry> {

        private static final Identifier EMERALD_TEXTURE = Identifier.withDefaultNamespace("textures/item/emerald.png");
        private static final Identifier ENCHANTED_BOOK_TEXTURE = Identifier.withDefaultNamespace("textures/item/enchanted_book.png");

        private static final int ICON_SIZE = 16;
        private static final int ICON_PADDING = 4;
        private static final int COLOR_TEXT = 0xFFE0E0E0;
        private static final int COLOR_PRICE = 0xFFFFD700;

        private final Font font = Minecraft.getInstance().font;
        public final TradeRequest tradeRequest;

        public TradeRequestEntry(TradeRequest tradeRequest) {
            this.tradeRequest = tradeRequest;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float delta) {
            int x = getX();
            int y = getY();
            int right = x + getWidth();
            int bottom = y + getHeight();

            graphics.fill(x, y, right, bottom, hovered ? COLOR_BG_HOVER : COLOR_BG);
            graphics.fill(x + 2, bottom - 1, right - 2, bottom, COLOR_SEPARATOR);

            int iconX = x + ICON_PADDING;
            graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTED_BOOK_TEXTURE,
                    iconX, y + 2, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
            graphics.blit(RenderPipelines.GUI_TEXTURED, EMERALD_TEXTURE,
                    iconX, y + 2 + ICON_SIZE + 2, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

            int textX = iconX + ICON_SIZE + ICON_PADDING;

            graphics.text(font, tradeRequest.getNameEnchantment(), textX, y + 5, COLOR_TEXT, false);
            graphics.text(font, Component.literal(tradeRequest.maxPrice() + " Emeralds"), textX, y + ICON_SIZE + 7, COLOR_PRICE, false);
        }
    }
}