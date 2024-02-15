package de.gamedude.evt.screen.widget;

import de.gamedude.evt.utils.TradeRequest;
import de.gamedude.old.EasyVillagerTrade;
import de.gamedude.old.core.TradeWorkflowHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TradeRequestListWidget extends AbstractParentElement implements Drawable, Selectable {

    private static final int ENTRY_HEIGHT = 32;
    private static int ENTRIES_PER_PAGE;

    private double scrollAmount;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private final List<TradeRequestEntry> children;

    public TradeRequestListWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.children = new ArrayList<>();
    }

    public int getEntryCount() {
        return children.size();
    }

    public TradeRequestEntry getEntry(int index) {
        return this.children.get(index);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ENTRIES_PER_PAGE = (int) Math.ceil((height - y + 5) / (ENTRY_HEIGHT + 5f) - 1);
        if (ENTRIES_PER_PAGE == 0)
            return;

        this.renderBackground(context);

        for (int index = 0; index < Math.min(getEntryCount(), ENTRIES_PER_PAGE); ++index) {
            getEntry(index + getOffset()).render(context, index, x, y + 1, width, mouseX, mouseY, delta);
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
    public List<TradeRequestEntry> children() {
        return children;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.setScrollAmount(scrollAmount - (amount * (ENTRY_HEIGHT + 5)));
        return true;
    }

    public void setScrollAmount(double amount) {
        this.scrollAmount = MathHelper.clamp(amount, 0.0, this.getMaxScroll());
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return x <= mouseX && mouseX <= (x + width) && y <= mouseY && mouseY <= height;
    }

    private void renderBackground(DrawContext context) {
        context.fill(x - 1, y, x + width + 1, y + 1, -1); // horizontal
        context.fill(x - 2, height, x + width + 2, height + 1, -1);
        context.fill(x - 2, y, x - 1, height, -1); // vertical
        context.fill(x + width + 1, y, x + width + 2, height, -1);
    }

    public void addEntry(TradeRequest tradeRequest) {
        this.children.add(new TradeRequestEntry(tradeRequest));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = super.mouseClicked(mouseX, mouseY, button);
        Optional<Element> element = this.hoveredElement(mouseX, mouseY);
        if(element.isEmpty())
            return bl;
        TradeRequestEntry tradeRequestEntry = (TradeRequestEntry) element.get();
        return bl;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) { }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    public static class TradeRequestEntry implements Element {

        private static final Identifier EMERALD_TEXTURE = new Identifier("textures/item/emerald.png");
        private static final Identifier ENCHANTED_BOOK_TEXTURE = new Identifier("textures/item/enchanted_book.png");
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        public final TradeRequest tradeRequest;
        private int x,y1,x2,y2;

        private ButtonWidget widget;

        public TradeRequestEntry(TradeRequest request) {
            this.tradeRequest = request;
        }

        private void render(DrawContext context, int index, int x, int y, int entryWidth, int mouseX, int mouseY, float delta) {
            this.x = x;
            this.y1 = y + (index * ENTRY_HEIGHT) + (5 * index);
            this.x2 = x + entryWidth;
            this.y2 = y + ENTRY_HEIGHT * (index + 1) + (5 * index);

            context.fill(x, y1, x2, y2, ColorHelper.Argb.getArgb(240, 7, 7, 7));

            context.drawTexture(ENCHANTED_BOOK_TEXTURE, x, y1, 0.0f, 0.0f, 16, 16, 16, 16);
            context.drawTexture(EMERALD_TEXTURE, x, y1 + 16, 24, 0f, 0f, 16, 16, 16, 16);

            context.drawText(textRenderer, tradeRequest.enchantment().getName(tradeRequest.level()), x + 20, y1 + 4, -1, false);
            context.drawText(textRenderer, "§e" + tradeRequest.cost(), x + 20, y1 + 20, -1, false);

            if(widget == null) {
                widget = ButtonWidget.builder(Text.of("test"), button -> System.out.println("[DEBUG] TradeRequestEntry.render: " + "test")).dimensions(x, y1, 50, 20).tooltip(Tooltip.of(Text.of("Test"))).build();
            }
            widget.render(context, mouseX, mouseY, delta);
        }


        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return x <= mouseX && mouseX <= x2 && y1 <= mouseY && mouseY <= y2;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return widget.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void setFocused(boolean focused) { }

        @Override
        public boolean isFocused() {
            return false;
        }
    }
}
