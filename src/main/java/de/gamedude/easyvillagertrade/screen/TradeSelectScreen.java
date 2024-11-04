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

import java.awt.*;
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

            int result = modBase.getTradeRequestInputHandler().handleInputUI(enchantmentInputWidget.getText(), levelTextFieldWidget.getText(), priceTextFieldWidget.getText(), tradeRequest -> {
                if(!modBase.getTradeRequestContainer().getTradeRequests().contains(tradeRequest)) {
                    tradeRequestListWidget.addEntry(tradeRequest);
                    modBase.getTradeRequestContainer().addTradeRequest(tradeRequest);
                }
            });

            switch (result) {
                case 0 -> clearTextFieldWidgets(enchantmentInputWidget, levelTextFieldWidget, priceTextFieldWidget);
                case 1 -> enchantmentInputWidget.setEditableColor(Color.RED.getRGB());
                case 2 -> priceTextFieldWidget.setEditableColor(Color.RED.getRGB());
                case 3 -> levelTextFieldWidget.setEditableColor(Color.RED.getRGB());
            }

        }).position(x + 9, px + 15 + 20 + 5).size(50, 20).build();

        ButtonWidget removeButton = ButtonWidget.builder(Text.of("Remove"), button -> {
            RegistryEntry<Enchantment> enchantment = modBase.getTradeRequestInputHandler().getEnchantment(enchantmentInputWidget.getText());
            if (enchantment == null) {
                enchantmentInputWidget.setEditableColor(ColorHelper.getArgb(255, 255, 0, 0));
                return;
            }

            for (Iterator<TradeRequestListWidget.TradeRequestEntry> it = tradeRequestListWidget.children().iterator(); it.hasNext(); ) {
                TradeRequestListWidget.TradeRequestEntry entry = it.next();
                if (TradeRequest.equalEnchantment(enchantment, entry.tradeRequest.enchantment())) {
                    it.remove();
                    modBase.getTradeRequestContainer().removeTradeRequest(entry.tradeRequest);
                }
            }

            clearTextFieldWidgets(enchantmentInputWidget, levelTextFieldWidget, priceTextFieldWidget);

        }).position(x + 70, px + 40).size(50, 20).build();

        this.addDrawableChild(addButton);
        this.addDrawableChild(removeButton);
        this.addDrawableChild(tradeRequestListWidget);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        children().forEach(element -> {
            if(element instanceof TextFieldWidget textFieldWidget)
                textFieldWidget.setEditableColor(0xE0E0E0);
        });
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        children().forEach(element -> {
            if(element instanceof TextFieldWidget textFieldWidget)
                textFieldWidget.setEditableColor(0xE0E0E0);
        });
        return super.charTyped(chr, modifiers);
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
        context.fill(x, px, this.width - px, this.height - px, ColorHelper.getArgb(150, 7, 7, 7));
    }

    public void clearTextFieldWidgets(TextFieldWidget... textFieldWidgets){
        Arrays.stream(textFieldWidgets).forEach(textFieldWidget -> textFieldWidget.setText(""));
    }

}
