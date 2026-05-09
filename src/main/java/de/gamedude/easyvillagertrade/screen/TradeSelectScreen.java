package de.gamedude.easyvillagertrade.screen;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
        super(Component.empty());

        this.enchantmentWidth = getFont().width("Enchantment");
        this.levelWidth = getFont().width("Level");
        this.priceWidth = getFont().width("Price");
        this.widgetWidth = priceWidth + levelWidth + enchantmentWidth + 50;
    }

    @Override
    protected void init() {
        int px = (int) (this.width / 50f);
        int x = this.width - widgetWidth - px;

        EnchantmentInputWidget enchantmentInputWidget = new EnchantmentInputWidget(x + 10, px + 15, enchantmentWidth, 20);
        EditBox levelTextFieldWidget = new EditBox(font, x + 20 + enchantmentWidth, px + 15, levelWidth, 20, Component.literal("Level") );
        EditBox priceTextFieldWidget = new EditBox(font, x + 30 + enchantmentWidth + levelWidth, px + 15, priceWidth, 20, Component.literal("Price") );

        int listHeight = this.height - px - 50;
        TradeRequestListWidget tradeRequestListWidget = new TradeRequestListWidget(x + 10, px + 80, widgetWidth - 20, listHeight);
        modBase.getTradeRequestContainer().getTradeRequests().forEach(tradeRequestListWidget::addEntry);

        this.addRenderableWidget(enchantmentInputWidget);
        this.addRenderableWidget(levelTextFieldWidget);
        this.addRenderableWidget(priceTextFieldWidget);

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
                enchantmentInputWidget.setEditableColor(ColorHelper.getArgb(    255, 255, 0, 0));
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

        int buttonY = this.height - px - 25;
        int buttonX = x + (widgetWidth - 160) / 2;

        ButtonWidget selectLecternButton = ButtonWidget.builder(Text.of("Select"), button -> {
            if(this.client != null && this.client.player != null)
                this.client.player.networkHandler.sendChatCommand("evt select close");
        }).position(buttonX, buttonY).size(50, 20).build();

        ButtonWidget startButton = ButtonWidget.builder(Text.of("Start"), button -> {
            if(this.client != null && this.client.player != null) {
                this.close();
                this.client.player.networkHandler.sendChatCommand("evt execute");
            }
        }).position(buttonX + 55, buttonY).size(50, 20).build();

        ButtonWidget stopButton = ButtonWidget.builder(Text.of("Stop"), button -> {
            if(this.client != null && this.client.player != null)
                this.client.player.networkHandler.sendChatCommand("evt stop");
        }).position(buttonX + 110, buttonY).size(50, 20).build();


        this.addDrawableChild(selectLecternButton);
        this.addDrawableChild(startButton);
        this.addDrawableChild(stopButton);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        children().forEach(element -> {
            if(element instanceof TextFieldWidget textFieldWidget) {
                textFieldWidget.setEditableColor(-2039584);
            }
        });
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        children().forEach(element -> {
            if(element instanceof TextFieldWidget textFieldWidget)
                textFieldWidget.setEditableColor(-2039584);
        });
        return super.charTyped(charInput);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        int px = (int) (this.width / 50f);
        int x = this.width - px - widgetWidth;
        context.drawText(textRenderer, "Enchantment", x + 10, px + 6, -2039584, false);
        context.drawText(textRenderer, "Level", x + 20 + enchantmentWidth, px + 6, -2039584, false);
        context.drawText(textRenderer, "Price", x + 30 + enchantmentWidth + priceWidth, px + 6, -2039584, false);
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
