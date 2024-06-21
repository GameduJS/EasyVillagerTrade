package de.gamedude.easyvillagertrade.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.enchantment.Enchantment;
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
    private final EasyVillagerTradeBase modBase;

    public TradeRequestListWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.children = new ArrayList<>();
        this.modBase = EasyVillagerTrade.getModBase();
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
            getEntry(index + getOffset()).render(context, index, x, y + 1, width);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        this.scrollAmount = MathHelper.clamp(
                scrollAmount - (vertical * (ENTRY_HEIGHT + 5)),
                0.0,
                this.getMaxScroll());
        return true;
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

    public void addEntry(TradeRequest entry) {
        children.add(new TradeRequestEntry(entry));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = super.mouseClicked(mouseX, mouseY, button);
        Optional<Element> element = this.hoveredElement(mouseX, mouseY);
        if(element.isEmpty())
            return bl;
        TradeRequestEntry tradeRequestEntry = (TradeRequestEntry) element.get();
        children.remove(tradeRequestEntry);
        modBase.getTradeRequestContainer().removeTradeRequest(tradeRequestEntry.tradeRequest);
        return bl;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) { }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    public static class TradeRequestEntry implements Element {

        private static final Identifier EMERALD_TEXTURE = Identifier.of("textures/item/emerald.png");
        private static final Identifier ENCHANTED_BOOK_TEXTURE = Identifier.of("textures/item/enchanted_book.png");
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        public final TradeRequest tradeRequest;
        private int x,y1,x2,y2;

        public TradeRequestEntry(TradeRequest request) {
            this.tradeRequest = request;
        }

        private void render(DrawContext context, int index, int x, int y, int entryWidth) {
            this.x = x;
            this.y1 = y + (index * ENTRY_HEIGHT) + (5 * index);
            this.x2 = x + entryWidth;
            this.y2 = y + ENTRY_HEIGHT * (index + 1) + (5 * index);

            context.fill(x, y1, x2, y2, ColorHelper.Argb.getArgb(240, 7, 7, 7));

            context.drawTexture(ENCHANTED_BOOK_TEXTURE, x, y1, 0, 0, 16, 16, 16, 16);

            RenderSystem.setShaderTexture(0, EMERALD_TEXTURE);
            context.drawTexture(EMERALD_TEXTURE, x, y1 + 16,0, 0, 16, 16, 16, 16);

            context.drawText(textRenderer, Enchantment.getName(tradeRequest.enchantment(), tradeRequest.level()), x + 20, y1 + 4, 0, false);
            context.drawText(textRenderer, Text.of("§e" + tradeRequest.maxPrice()), x + 20, y1 + 20, 0, false);
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
    }
}
