package de.gamedude.easyvillagertrade.screen;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.screen.widget.EnchantmentInputWidget;
import de.gamedude.easyvillagertrade.screen.widget.TradeRequestListWidget;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.Arrays;
import java.util.Iterator;

public class TradeSelectScreen extends Screen {

    private final EasyVillagerTradeBase modBase = EasyVillagerTrade.getModBase();

    private final int enchantmentWidth;
    private final int levelWidth;
    private final int priceWidth;

    public final int widgetWidth;

    public TradeSelectScreen() {
        super(Text.empty());

        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        this.enchantmentWidth = textRenderer.getWidth("Enchantment");
        this.levelWidth = textRenderer.getWidth("Level");
        this.priceWidth = textRenderer.getWidth("Price");
        this.widgetWidth = priceWidth + levelWidth + enchantmentWidth + 50;
    }

    @Override
    protected void init() {
        int px = (int) (this.width / 50f);
        int x = this.width - widgetWidth - px;

        EnchantmentInputWidget enchantmentInputWidget = new EnchantmentInputWidget(x + 10, px + 15, enchantmentWidth, 20);

        TextFieldWidget levelTextFieldWidget = new TextFieldWidget(textRenderer, x + 20 + enchantmentWidth, px + 15, levelWidth, 20, Text.of("Level"));
        TextFieldWidget priceTextFieldWidget = new TextFieldWidget(textRenderer, x + 30 + enchantmentWidth + levelWidth, px + 15, priceWidth, 20, Text.of("Price"));

        TradeRequestListWidget tradeRequestListWidget = new TradeRequestListWidget(x + 10, px + 80, width - x - px - 20, this.height - px - 50);
        modBase.getTradeRequestContainer().getTradeRequests().forEach(tradeRequestListWidget::addEntry);

        this.addDrawableChild(enchantmentInputWidget);
        this.addDrawableChild(levelTextFieldWidget);
        this.addDrawableChild(priceTextFieldWidget);

        ButtonWidget addButton = ButtonWidget.builder(Text.of("Add"), button -> {
            TradeRequest request = modBase.getTradeRequestInputHandler().handleGUIInput(enchantmentInputWidget.getText(), levelTextFieldWidget.getText(), priceTextFieldWidget.getText());
            if (request != null) {
                if (!modBase.getTradeRequestContainer().getTradeRequests().contains(request)) {
                    tradeRequestListWidget.addEntry(request);
                    modBase.getTradeRequestContainer().addTradeRequest(request);
                    clearTextFieldWidgets(enchantmentInputWidget, levelTextFieldWidget, priceTextFieldWidget);
                }
            } else {
                enchantmentInputWidget.setEditableColor(ColorHelper.Argb.getArgb(255, 255, 0, 0));
            }
        }).position(x + 9, px + 15 + 20 + 5).size(50, 20).build();

        ButtonWidget removeButton = ButtonWidget.builder(Text.of("Remove"), button -> {
            RegistryEntry<Enchantment> enchantment = modBase.getTradeRequestInputHandler().getEnchantment(enchantmentInputWidget.getText());
            if (enchantment == null) {
                enchantmentInputWidget.setEditableColor(ColorHelper.Argb.getArgb(255, 255, 0, 0));
                return;
            }
            for (Iterator<TradeRequestListWidget.TradeRequestEntry> it = tradeRequestListWidget.children().iterator(); it.hasNext(); ) {
                TradeRequestListWidget.TradeRequestEntry entry = it.next();
                if (TradeRequest.equalEnchantment(enchantment, entry.tradeRequest.enchantment())) {
                    modBase.getTradeRequestContainer().removeTradeRequest(entry.tradeRequest);
                    it.remove();
                    clearTextFieldWidgets(enchantmentInputWidget, levelTextFieldWidget, priceTextFieldWidget);
                }
            }
        }).position(x + 70, px + 40).size(50, 20).build();

        this.addDrawableChild(addButton);
        this.addDrawableChild(removeButton);
        this.addDrawableChild(tradeRequestListWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        int px = (int) (this.width / 50f);
        int x = this.width - px - widgetWidth;

        context.drawText(textRenderer, "Enchantment", x + 10, px + 6, 0xE0E0E0, false);
        context.drawText(textRenderer, "Level", x + 20 + enchantmentWidth, px + 6, 0xE0E0E0, false);
        context.drawText(textRenderer, "Price", x + 30 + enchantmentWidth + priceWidth, px + 6, 0xE0E0E0, false);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        int px = (int) (this.width / 50f);
        int x = this.width - px - widgetWidth;
        context.fill(x, px, this.width - px, this.height - px, ColorHelper.Argb.getArgb(150, 7, 7, 7));
    }

    public void clearTextFieldWidgets(TextFieldWidget... textFieldWidgets){
        Arrays.stream(textFieldWidgets).forEach(textFieldWidget -> textFieldWidget.setText(""));
    }

}
