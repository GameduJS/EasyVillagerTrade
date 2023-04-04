package de.gamedude.easyvillagertrade.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class TradeRequestListWidget extends EntryListWidget<TradeRequestListWidget.TradeRequestEntry> {

    private static final int entryHeight = 32;

    public TradeRequestListWidget(int x, int y, int width, int height) {
        super(MinecraftClient.getInstance(), width, height, x, y, 20);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int x = this.top;
        int y = this.bottom;
        int width = this.width;
        int height = this.height;

        fill(matrices, x - 1, y, x + width + 1, y + 1, -1); // horizontal
        fill(matrices, x - 2, height, x + width + 2, height + 1, -1);
        fill(matrices, x - 2, y, x - 1, height, -1); // vertical
        fill(matrices, x + width + 1, y, x + width + 2, height, -1);

        int entrySize = this.getEntryCount();
        int entryPerPage = (int) Math.ceil((height - y + 5) / (entryHeight + 5f) - 1);

        for (int index = 0; index < entrySize && (index + 1) * (entryHeight + 5) - 5 < height - y; ++index) {
            this.renderEntry(matrices, mouseX, mouseY, delta, index, x, y + 1, width, height);
        }
    }

    @Override
    public int addEntry(TradeRequestEntry entry) {
        return super.addEntry(entry);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    public static class TradeRequestEntry extends EntryListWidget.Entry<TradeRequestEntry> {

        private static final Identifier EMERALD_TEXTURE = new Identifier("textures/item/emerald.png");
        private static final Identifier ENCHANTED_BOOK_TEXTURE = new Identifier("textures/item/enchanted_book.png");
        public final TradeRequest tradeRequest;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        public TradeRequestEntry(TradeRequest request) {
            this.tradeRequest = request;
        }

        public void renderEntry(MatrixStack matrices, int index, int x, int y, int widgetWidth) {
            int entryWidth = widgetWidth;

            int y1 = y + (index * entryHeight) + (5 * index);
            int x2 = x + entryWidth;
            int y2 = y + entryHeight * (index + 1) + (5 * index);

            DrawableHelper.fill(matrices, x, y1, x2, y2, ColorHelper.Argb.getArgb(240, 7, 7, 7));

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

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
}
