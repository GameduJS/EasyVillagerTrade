package de.gamedude.old.screen;

import de.gamedude.old.core.TradeRequestContainer;
import de.gamedude.old.core.TradeRequestInputHandler;
import de.gamedude.old.core.TradeWorkflowHandler;
import de.gamedude.old.render.widget.VillagerRenderWidget;
import de.gamedude.old.screen.widget.EnchantmentInputWidget;
import de.gamedude.old.screen.widget.TradeRequestListWidget;
import de.gamedude.old.utils.TradeRequest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

import java.util.Arrays;
import java.util.Iterator;

public class TradeSelectScreen extends Screen {

    private final TradeWorkflowHandler tradeWorkflowHandler;

    private final int enchantmentWidth;
    private final int levelWidth;
    private final int priceWidth;

    public static int widgetWidth;

    public TradeSelectScreen(TradeWorkflowHandler tradeWorkflowHandler) {
        super(Text.empty());
        this.tradeWorkflowHandler = tradeWorkflowHandler;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        this.enchantmentWidth = textRenderer.getWidth("Enchantment");
        this.levelWidth = textRenderer.getWidth("Level");
        this.priceWidth = textRenderer.getWidth("Price");
        widgetWidth = priceWidth + levelWidth + enchantmentWidth + 50;
    }

    @Override
    protected void init() {
        int px = (int) (this.width / 50f);
        int x = this.width - widgetWidth - px;

        TradeRequestContainer tradeRequestContainer = tradeWorkflowHandler.getHandler(TradeRequestContainer.class);
        TradeRequestInputHandler tradeRequestInputHandler = tradeWorkflowHandler.getHandler(TradeRequestInputHandler.class);

        EnchantmentInputWidget enchantmentInputWidget = new EnchantmentInputWidget(x + 10, px + 15, enchantmentWidth, 20);
        TextFieldWidget levelTextFieldWidget = new TextFieldWidget(textRenderer, x + 20 + enchantmentWidth, px + 15, levelWidth, 20, Text.of("Level"));
        TextFieldWidget priceTextFieldWidget = new TextFieldWidget(textRenderer, x + 30 + enchantmentWidth + levelWidth, px + 15, priceWidth, 20, Text.of("Price"));

        TradeRequestListWidget tradeRequestListWidget = new TradeRequestListWidget(x + 10, px + 80, width - x - px - 20, this.height - px - 50);
        tradeRequestContainer.getTradeRequests().forEach(tradeRequestListWidget::addEntry);

        this.addDrawableChild(tradeRequestListWidget);
        this.addDrawableChild(enchantmentInputWidget);
        this.addDrawableChild(levelTextFieldWidget);
        this.addDrawableChild(priceTextFieldWidget);
        this.addDrawableChild(ButtonWidget.builder(Text.of("Add"), button -> {
            TradeRequest request = tradeRequestInputHandler.handleGUIInput(enchantmentInputWidget.getText(), levelTextFieldWidget.getText(), priceTextFieldWidget.getText());

            // TradeRequest could be incomplete
            if (request == null) {
                enchantmentInputWidget.setEditableColor(ColorHelper.Argb.getArgb(255, 255, 0, 0));
                return;
            }

            // TradeRequest could already exists
            if (!tradeRequestContainer.getTradeRequests().contains(request)) {
                tradeRequestListWidget.addEntry(request);
                tradeRequestContainer.addTradeRequest(request);
                clearTextFieldWidgets(enchantmentInputWidget, levelTextFieldWidget, priceTextFieldWidget);
            }

        }).position(x + 9, px + 15 + 20 + 5).size(50, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Remove"), button -> {
            Enchantment enchantment = tradeRequestInputHandler.getEnchantment(enchantmentInputWidget.getText());

            // Check if TradeRequests exists with given enchantment
            if (enchantment == null) {
                enchantmentInputWidget.setEditableColor(ColorHelper.Argb.getArgb(255, 255, 0, 0));
                return;
            }

            // Removal of TradeRequest
            for (Iterator<TradeRequestListWidget.TradeRequestEntry> it = tradeRequestListWidget.children().iterator(); it.hasNext(); ) {
                TradeRequestListWidget.TradeRequestEntry entry = it.next();
                if (entry.tradeRequest.enchantment().getTranslationKey().equals(enchantment.getTranslationKey())) {
                    tradeRequestContainer.removeTradeRequest(entry.tradeRequest);
                    it.remove();
                    clearTextFieldWidgets(enchantmentInputWidget, levelTextFieldWidget, priceTextFieldWidget);
                }
            }

        }).position(x + 70, px + 40).size(50, 20).build());

        this.addDrawableChild(new TexturedButtonWidget(x + 131, px + 40, 20, 18, 0, 0, 19, new Identifier("textures/gui/recipe_button.png"), button -> {
            MinecraftClient.getInstance().setScreen(new ConfigScreen(tradeWorkflowHandler));
        }));

        VillagerRenderWidget villagerRenderWidget = new VillagerRenderWidget(width / 2, height / 2, 300);
        villagerRenderWidget.enableRenderingBox();
        VillagerData villagerData = new VillagerData(VillagerType.SWAMP, VillagerProfession.CLERIC, 0);
        NbtCompound nbtCompound = new NbtCompound();
        VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, villagerData).result().ifPresent(nbtElement -> nbtCompound.put("VillagerData", nbtElement));
        villagerRenderWidget.setNbtCompoundSupplier(nbtCompound);

        this.addDrawableChild(villagerRenderWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        int px = (int) (this.width / 50f);
        int x = this.width - px - widgetWidth;

        context.drawText(textRenderer, "Enchantment", x + 10, px + 6, 0xE0E0E0, false);
        context.drawText(textRenderer, "Level", x + 20 + enchantmentWidth, px + 6, 0xE0E0E0, false);
        context.drawText(textRenderer, "Price", x + 30 + enchantmentWidth + levelWidth, px + 6, 0xE0E0E0, false);
    }

    @Override
    public void renderBackground(DrawContext context) {
        int px = (int) (this.width / 50f);
        int x = this.width - px - widgetWidth;
        context.fill(x, px, this.width - px, this.height - px, ColorHelper.Argb.getArgb(150, 7, 7, 7));
    }

    private void clearTextFieldWidgets(TextFieldWidget... textFieldWidgets) {
        Arrays.stream(textFieldWidgets).forEach(textFieldWidget -> textFieldWidget.setText(""));
    }
}
