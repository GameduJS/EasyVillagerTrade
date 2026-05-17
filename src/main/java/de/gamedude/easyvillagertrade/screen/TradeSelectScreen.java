package de.gamedude.easyvillagertrade.screen;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.screen.widget.EnchantmentInputWidget;
import de.gamedude.easyvillagertrade.screen.widget.TradeRequestListWidget;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.enchantment.Enchantment;

import java.awt.*;
import java.util.Arrays;

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

        Button addButton = Button.builder(Component.literal("Add"), button -> {

            int result = modBase.getTradeRequestInputHandler().handleInputUI(
                    enchantmentInputWidget.getValue(), levelTextFieldWidget.getValue(), priceTextFieldWidget.getValue(), tradeRequest -> {
                if(!modBase.getTradeRequestContainer().getTradeRequests().contains(tradeRequest)) {
                    tradeRequestListWidget.addEntry(tradeRequest);
                    modBase.getTradeRequestContainer().addTradeRequest(tradeRequest);
                }
            });

            switch (result) {
                case 0 -> clearTextFieldWidgets(enchantmentInputWidget, levelTextFieldWidget, priceTextFieldWidget);
                case 1 -> enchantmentInputWidget.setTextColor(Color.RED.getRGB());
                case 2 -> priceTextFieldWidget.setTextColor(Color.RED.getRGB());
                case 3 -> levelTextFieldWidget.setTextColor(Color.RED.getRGB());
            }

        }).pos(x + 9, px + 15 + 20 + 5).size(50, 20).build();

        Button removeButton = Button.builder(Component.literal("Remove"), button -> {
            Holder<Enchantment> enchHolder = modBase.getTradeRequestInputHandler().getEnchantment(enchantmentInputWidget.getValue());
            if (enchHolder == null) {
                enchantmentInputWidget.setTextColor(ARGB.color(255, 255, 0, 0));
                return;
            }

            tradeRequestListWidget.removeEntry(request ->
                    TradeRequest.equalEnchantment(request.enchantmentHolder(), enchHolder))
                    .forEach(request ->
                            modBase.getTradeRequestContainer().removeTradeRequestByEnchantment(request.enchantmentHolder()));

            clearTextFieldWidgets(enchantmentInputWidget, levelTextFieldWidget, priceTextFieldWidget);

        }).pos(x + 70, px + 40).size(50, 20).build();

        this.addRenderableWidget(addButton);
        this.addRenderableWidget(removeButton);
        this.addRenderableWidget(tradeRequestListWidget);

        int buttonY = this.height - px - 25;
        int buttonX = x + (widgetWidth - 160) / 2;

        Button selectLecternButton = Button.builder(Component.literal("Select"), button -> {
            if(this.minecraft.player != null)
                this.minecraft.player.connection.sendCommand("evt select close");
        }).pos(buttonX, buttonY).size(50, 20).build();

        Button startButton = Button.builder(Component.literal("Start"), button -> {
            if(this.minecraft.player != null) {
                this.onClose();
                this.minecraft.player.connection.sendCommand("evt execute");
            }
        }).pos(buttonX + 55, buttonY).size(50, 20).build();

        Button stopButton = Button.builder(Component.literal("Stop"), button -> {
            if(this.minecraft.player != null)
                this.minecraft.player.connection.sendCommand("evt stop");
        }).pos(buttonX + 110, buttonY).size(50, 20).build();


        this.addRenderableWidget(selectLecternButton);
        this.addRenderableWidget(startButton);
        this.addRenderableWidget(stopButton);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        children().forEach(element -> {
            if(element instanceof EditBox textFieldWidget) {
                textFieldWidget.setTextColor(-2039584);
            }
        });
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        children().forEach(element -> {
            if(element instanceof EditBox textFieldWidget)
                textFieldWidget.setTextColor(-2039584);
        });
        return super.charTyped(event);
    }


    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.extractBackground(graphics, mouseX, mouseY, a);
        super.extractRenderState(graphics, mouseX, mouseY, a);
        int px = (int) (this.width / 50f);
        int x = this.width - px - widgetWidth;
        graphics.text(this.font, "Enchantment", x + 10, px + 6, -2039584, false);
        graphics.text(this.font, "Level", x + 20 + enchantmentWidth, px + 6, -2039584, false);
        graphics.text(this.font, "Price", x + 30 + enchantmentWidth + priceWidth, px + 6, -2039584, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int px = (int) (this.width / 50f);
        int x = this.width - px - widgetWidth;
        graphics.fill(x, px, this.width - px, this.height - px, ARGB.color(150, 7, 7, 7));
    }


    public void clearTextFieldWidgets(EditBox... textFieldWidgets){
        Arrays.stream(textFieldWidgets).forEach(textFieldWidget -> textFieldWidget.setValue(""));
    }

}
