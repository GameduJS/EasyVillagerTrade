package de.gamedude.easyvillagertrade.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class TradeRequestListWidget extends AlwaysSelectedEntryListWidget<TradeRequestListWidget.Entry> {

    private static final int ENTRY_HEIGHT = 32;
    private static int ENTRIES_PER_PAGE;

    public TradeRequestListWidget(int x, int y, int width, int height) {
        super(MinecraftClient.getInstance(), width, height, x, y, ENTRY_HEIGHT + 5);
        setRenderBackground(false);
        setRenderHorizontalShadows(false);
        setRenderSelection(false);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int x = this.top;
        int y = this.bottom;
        int width = this.width;
        int height = this.height;

        ENTRIES_PER_PAGE = (int) Math.ceil((height - y + 5) / (ENTRY_HEIGHT + 5f) - 1);
        if (ENTRIES_PER_PAGE == 0) // Unable to render any entry
            return;

        this.renderBackground(matrices);

        for (int index = 0; index < Math.min(getEntryCount(), ENTRIES_PER_PAGE); ++index) {
            ((TradeRequestEntry) getEntry(index + getOffset())).renderEntry(matrices, index, x, y + 1, width);
        }
    }

    private int getOffset() {
        int maxScroll = getMaxScroll();
        int currentScroll = (int) Math.abs(getScrollAmount());
        return Math.min((maxScroll > 0) ? (int) Math.ceil(maxScroll / (ENTRY_HEIGHT + 5f)) : 0, (int) Math.ceil(currentScroll / (ENTRY_HEIGHT + 5f)));
    }

    @Override
    protected int getMaxPosition() {
        return getEntryCount() * (ENTRY_HEIGHT + 5) - 5;
    }

    @Override
    public int getMaxScroll() {
        return getMaxPosition() - (ENTRIES_PER_PAGE * (ENTRY_HEIGHT + 5));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.setScrollAmount(getScrollAmount() - (amount * (ENTRY_HEIGHT + 5)));
        return true;
    }


    @Override // bypass normal EntryListWidget logic
    public boolean isMouseOver(double mouseX, double mouseY) {
        // top <= mouse <= (top + width) & bottom <= mouseY <= height
        return top <= mouseX && mouseX <= (top + width) && bottom <= mouseY && mouseY <= height;
    }

    @Override
    protected void renderBackground(MatrixStack matrices) {
        int x = this.top;
        int y = this.bottom;
        int width = this.width;
        int height = this.height;

        fill(matrices, x - 1, y, x + width + 1, y + 1, -1); // horizontal
        fill(matrices, x - 2, height, x + width + 2, height + 1, -1);
        fill(matrices, x - 2, y, x - 1, height, -1); // vertical
        fill(matrices, x + width + 1, y, x + width + 2, height, -1);
    }

    public int addEntry(Entry entry) {
        return super.addEntry(entry);
    }

    public static class TradeRequestEntry extends Entry {

        private static final Identifier EMERALD_TEXTURE = new Identifier("textures/item/emerald.png");
        private static final Identifier ENCHANTED_BOOK_TEXTURE = new Identifier("textures/item/enchanted_book.png");
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        public final TradeRequest tradeRequest;

        public TradeRequestEntry(TradeRequest request) {
            this.tradeRequest = request;
        }

        public void renderEntry(MatrixStack matrices, int index, int x, int y, int entryWidth) {
            int y1 = y + (index * ENTRY_HEIGHT) + (5 * index);
            int x2 = x + entryWidth;
            int y2 = y + ENTRY_HEIGHT * (index + 1) + (5 * index);

            DrawableHelper.fill(matrices, x, y1, x2, y2, ColorHelper.Argb.getArgb(240, 7, 7, 7));

            RenderSystem.setShaderTexture(0, ENCHANTED_BOOK_TEXTURE);
            DrawableHelper.drawTexture(matrices, x, y1, 0.0f, 0.0f, 16, 16, 16, 16);

            RenderSystem.setShaderTexture(0, EMERALD_TEXTURE);
            DrawableHelper.drawTexture(matrices, x, y1 + 16, 24, 0f, 0f, 16, 16, 16, 16);

            textRenderer.draw(matrices, tradeRequest.enchantment().getName(tradeRequest.level()), x + 20, y1 + 4, 0);
            textRenderer.draw(matrices, Text.of("§e" + tradeRequest.maxPrice()), x + 20, y1 + 20, 0);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            renderEntry(matrices, index, x, y, entryWidth);
        }
    }


    @Environment(value = EnvType.CLIENT)
    public static abstract class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        @Override
        public Text getNarration() {
            return Text.empty();
        }
    }
}
