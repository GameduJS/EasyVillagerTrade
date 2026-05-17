package de.gamedude.easyvillagertrade.screen.widget;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.util.List;
import java.util.function.Predicate;


public class TradeRequestListWidget extends AbstractSelectionList<TradeRequestListWidget.TradeRequestEntry> implements Renderable, GuiEventListener {

    private static final int ENTRY_HEIGHT = 32;
    private static int ENTRIES_PER_PAGE;

    private double scrollAmount;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private final EasyVillagerTradeBase modBase;

    public TradeRequestListWidget(int x, int y, int width, int height) {
        super(Minecraft.getInstance(), width, height, y, 5);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.modBase = EasyVillagerTrade.getModBase();
    }

    public int getEntryCount() {
        return children().size();
    }

    public TradeRequestEntry getEntry(int index) {
        return this.children().get(index);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        ENTRIES_PER_PAGE = (int) Math.ceil((height - y + 5) / (ENTRY_HEIGHT + 5f) - 1);
        if (ENTRIES_PER_PAGE == 0)
            return;
        this.renderBackground(graphics);

        for (int index = 0; index < Math.min(getEntryCount(), ENTRIES_PER_PAGE); ++index) {
            getEntry(index + getOffset()).render(graphics, index, x, y + 1, width);
        }
    }


    private int getOffset() {
        int maxScroll = getMaxScroll();
        int currentScroll = (int) Math.abs(this.scrollAmount);
        return Math.min((maxScroll > 0) ? (int) Math.ceil(maxScroll / (ENTRY_HEIGHT + 5f)) : 0, (int) Math.ceil(currentScroll / (ENTRY_HEIGHT + 5f)));
    }

    protected int getMaxPosition() {
        return getEntryCount() * (ENTRY_HEIGHT + 5) - 5;
    }

    public int getMaxScroll() {
        return getMaxPosition() - (ENTRIES_PER_PAGE * (ENTRY_HEIGHT + 5));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        this.scrollAmount = Math.clamp(
                scrollAmount - (vertical * (ENTRY_HEIGHT + 5)),
                0.0,
                this.getMaxScroll());
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return x <= mouseX && mouseX <= (x + width) && y <= mouseY && mouseY <= height;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) { }

    private void renderBackground(GuiGraphicsExtractor context) {
        context.fill(x - 1, y, x + width + 1, y + 1, -1); // horizontal
        context.fill(x - 2, height, x + width + 2, height + 1, -1);
        context.fill(x - 2, y, x - 1, height, -1); // vertical
        context.fill(x + width + 1, y, x + width + 2, height, -1);
    }

    public void addEntry(TradeRequest entry) {
        this.addEntry(new TradeRequestEntry(entry));
    }

    public List<TradeRequest> removeEntry(Predicate<TradeRequest> predicate) {
        return children().stream()
                .filter(tradeRequestEntry -> predicate.test(tradeRequestEntry.tradeRequest))
                .peek(this::removeEntry)
                .map(tradeRequestEntry -> tradeRequestEntry.tradeRequest)
                .toList();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean bl = super.mouseClicked(event, doubleClick);
        TradeRequestEntry element = this.getHovered();
        if ( element == null )
            return bl;

        // #HACK, since the "scrolled" entries "stack on top of each other" => the first would be removed each time
        int entriesSkipped = Math.ceilDiv( (int) scrollAmount, ENTRY_HEIGHT + 5 );
        if ( children().indexOf(element) == 0 && children().size() > ENTRIES_PER_PAGE ) {
            scrollAmount-=(ENTRY_HEIGHT + 5);
            element = children().get(entriesSkipped);
        }

        children().remove(element);
        modBase.getTradeRequestContainer().removeTradeRequest(element.tradeRequest);
        return bl;
    }

    public static class TradeRequestEntry extends Entry<TradeRequestEntry> implements GuiEventListener {

        private static final Identifier EMERALD_TEXTURE = Identifier.parse("textures/item/emerald.png");
        private static final Identifier ENCHANTED_BOOK_TEXTURE = Identifier.withDefaultNamespace("textures/item/enchanted_book.png");
        private final Font textRenderer = Minecraft.getInstance().font;
        public final TradeRequest tradeRequest;
        private int x,y1,x2,y2;

        public TradeRequestEntry(TradeRequest request) {
            this.tradeRequest = request;
        }

        private void render(GuiGraphicsExtractor context, int index, int x, int y, int entryWidth) {
            this.x = x;
            this.y1 = y + (index * ENTRY_HEIGHT) + (5 * index);
            this.x2 = x + entryWidth;
            this.y2 = y + ENTRY_HEIGHT * (index + 1) + (5 * index);

            context.fill(x, y1, x2, y2, ARGB.color(240, 7, 7, 7));

            context.blit(RenderPipelines.GUI_TEXTURED, ENCHANTED_BOOK_TEXTURE, x, y1, 0, 0, 16, 16, 16, 16);
            context.blit(RenderPipelines.GUI_TEXTURED, EMERALD_TEXTURE, x, y1 + 16,0, 0, 16, 16, 16, 16);

            context.text(textRenderer, tradeRequest.getNameEnchantment(), x + 20, y1 + 4, -2039584, false);
            context.text(textRenderer, Component.literal("§e" + tradeRequest.maxPrice()), x + 20, y1 + 20, -2039584, false);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return x <= mouseX && mouseX <= x2 && y1 <= mouseY && mouseY <= y2;
        }

        @Override
        public void setFocused(boolean focused) { }

        @Override
        public boolean isFocused() {
            return false;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        }
    }
}
