package de.gamedude.easyvillagertrade.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        ENTRIES_PER_PAGE = (int) Math.ceil((height - y + 5) / (ENTRY_HEIGHT + 5f) - 1);
        if (ENTRIES_PER_PAGE == 0)
            return;

        this.renderBackground(matrices);

        for (int index = 0; index < Math.min(getEntryCount(), ENTRIES_PER_PAGE); ++index) {
            getEntry(index + getOffset()).render(matrices, index, x, y + 1, width);
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

    private void renderBackground(MatrixStack matrices) {
        fill(matrices, x - 1, y, x + width + 1, y + 1, -1); // horizontal
        fill(matrices, x - 2, height, x + width + 2, height + 1, -1);
        fill(matrices, x - 2, y, x - 1, height, -1); // vertical
        fill(matrices, x + width + 1, y, x + width + 2, height, -1);
    }

    public void addEntry(TradeRequestEntry entry) {
        entry.setRemoveConsumer(tradeRequestEntry -> {
            modBase.getTradeRequestContainer().removeTradeRequest(tradeRequestEntry.tradeRequest);
            children.remove(tradeRequestEntry);
        });
        children.add(entry);
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

        private Consumer<TradeRequestEntry> removeConsumer;

        public TradeRequestEntry(TradeRequest request) {
            this.tradeRequest = request;
        }

        public void setRemoveConsumer(Consumer<TradeRequestEntry> removeConsumer) {
            this.removeConsumer = removeConsumer;
        }

        private void render(MatrixStack matrices, int index, int x, int y, int entryWidth) {
            this.x = x;
            this.y1 = y + (index * ENTRY_HEIGHT) + (5 * index);
            this.x2 = x + entryWidth;
            this.y2 = y + ENTRY_HEIGHT * (index + 1) + (5 * index);

            DrawableHelper.fill(matrices, x, y1, x2, y2, ColorHelper.Argb.getArgb(240, 7, 7, 7));

            RenderSystem.setShaderTexture(0, ENCHANTED_BOOK_TEXTURE);
            DrawableHelper.drawTexture(matrices, x, y1, 0.0f, 0.0f, 16, 16, 16, 16);

            RenderSystem.setShaderTexture(0, EMERALD_TEXTURE);
            DrawableHelper.drawTexture(matrices, x, y1 + 16, 24, 0f, 0f, 16, 16, 16, 16);

            textRenderer.draw(matrices, tradeRequest.enchantment().getName(tradeRequest.level()), x + 20, y1 + 4, 0);
            textRenderer.draw(matrices, Text.of("§e" + tradeRequest.maxPrice()), x + 20, y1 + 20, 0);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean clicked = isMouseOver(mouseX, mouseY);

            if(clicked)
                removeConsumer.accept(this);
            return clicked;
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return x <= mouseX && mouseX <= x2 && y1 <= mouseY && mouseY <= y2;
        }
    }
}
