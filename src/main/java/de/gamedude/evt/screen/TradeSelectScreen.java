package de.gamedude.evt.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.gamedude.evt.EasyVillagerTrade;
import de.gamedude.evt.handler.TradeRequestContainer;
import de.gamedude.evt.handler.TradeRequestParser;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.screen.widget.EnchantmentInputWidget;
import de.gamedude.evt.screen.widget.TradeRequestListWidget;
import de.gamedude.evt.utils.TradeRequest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class TradeSelectScreen extends Screen {

    private static final TradeWorkflow tradeWorkflow = TradeWorkflow.INSTANCE;
    private final int enchantmentWidth;
    private final int levelWidth;
    private final int priceWidth;

    private final int widgetWidth;

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

        TradeRequestContainer tradeRequestContainer = tradeWorkflow.getHandler(TradeRequestContainer.class);
        TradeRequestParser tradeRequestInputHandler = tradeWorkflow.getHandler(TradeRequestParser.class);

        EnchantmentInputWidget enchantmentInputWidget = new EnchantmentInputWidget(x + 10, px + 15, enchantmentWidth, 20);
        TextFieldWidget levelTextFieldWidget = new TextFieldWidget(textRenderer, x + 20 + enchantmentWidth, px + 15, levelWidth, 20, Text.of("Level"));
        TextFieldWidget priceTextFieldWidget = new TextFieldWidget(textRenderer, x + 30 + enchantmentWidth + levelWidth, px + 15, priceWidth, 20, Text.of("Price"));

        TradeRequestListWidget tradeRequestListWidget = new TradeRequestListWidget(x + 10, px + 80, width - x - px - 20, this.height - px - 50);
        tradeRequestContainer.getRequests().forEach(tradeRequestListWidget::addEntry);

        this.addDrawableChild(tradeRequestListWidget);
        this.addDrawableChild(enchantmentInputWidget);
        this.addDrawableChild(levelTextFieldWidget);
        this.addDrawableChild(priceTextFieldWidget);
        this.addDrawableChild(ButtonWidget.builder(Text.of("Add"), button -> {
            TradeRequest request = tradeRequestInputHandler.parseUiInput(enchantmentInputWidget.getText(), levelTextFieldWidget.getText(), priceTextFieldWidget.getText());

            if (request == null) {
                enchantmentInputWidget.setEditableColor(ColorHelper.Argb.getArgb(255, 255, 0, 0));
                return;
            }

            if (!tradeRequestContainer.getRequests().contains(request)) {
                tradeRequestListWidget.addEntry(request);
                tradeRequestContainer.addRequest(request);
                clearTextFieldWidgets(enchantmentInputWidget, levelTextFieldWidget, priceTextFieldWidget);
            }

        }).position(x + 9, px + 15 + 20 + 5).size(50, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Remove"), button -> {
            Enchantment enchantment = tradeRequestInputHandler.getEnchantment(enchantmentInputWidget.getText());
            if (enchantment == null) {
                enchantmentInputWidget.setEditableColor(ColorHelper.Argb.getArgb(255, 255, 0, 0));
                return;
            }

            tradeRequestContainer.removeRequestByEnchantment(enchantment);
            tradeRequestListWidget.removeChildByEnchantment(enchantment);
            clearTextFieldWidgets(enchantmentInputWidget, levelTextFieldWidget, priceTextFieldWidget);
        }).position(x + 70, px + 40).size(50, 20).build());

        this.addDrawableChild(new TexturedButtonWidget(x + 131, px + 40, 20, 18, 0, 0, 19, new Identifier("textures/gui/recipe_button.png"), button -> MinecraftClient.getInstance().setScreen(new OptionScreen(widgetWidth))));
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
