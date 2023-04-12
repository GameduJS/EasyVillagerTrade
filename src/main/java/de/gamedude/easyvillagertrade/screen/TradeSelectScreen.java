package de.gamedude.easyvillagertrade.screen;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.screen.widget.TradeRequestListWidget;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.registry.Registry;

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

        TextFieldWidget enchantmentTextFieldWidget = new TextFieldWidget(textRenderer, x + 10, px + 15, enchantmentWidth, 20, Text.of("Enchantment"));
        enchantmentTextFieldWidget.setChangedListener(input -> {
            if (Registry.ENCHANTMENT.stream().map(Enchantment::getTranslationKey).map(Text::translatable).map(Text::getString).anyMatch(input::equalsIgnoreCase))
                enchantmentTextFieldWidget.setEditableColor(ColorHelper.Argb.getArgb(255, 255, 255, 0));
            else
                enchantmentTextFieldWidget.setEditableColor(0xE0E0E0);
        });
        TextFieldWidget levelTextFieldWidget = new TextFieldWidget(textRenderer, x + 20 + enchantmentWidth, px + 15, levelWidth, 20, Text.of("Level"));
        TextFieldWidget priceTextFieldWidget = new TextFieldWidget(textRenderer, x + 30 + enchantmentWidth + levelWidth, px + 15, priceWidth, 20, Text.of("Price"));

        TradeRequestListWidget tradeRequestListWidget = new TradeRequestListWidget(x + 10, px + 80, width - x - px - 20, this.height - px - 50);
        modBase.getTradeRequestContainer().getTradeRequests().forEach(tradeRequest -> tradeRequestListWidget.addEntry(new TradeRequestListWidget.TradeRequestEntry(tradeRequest)));

        this.addDrawableChild(enchantmentTextFieldWidget);
        this.addDrawableChild(levelTextFieldWidget);
        this.addDrawableChild(priceTextFieldWidget);
        this.addDrawableChild(new ButtonWidget(x + 9, px + 15 + 20 + 5, 50, 20, Text.of("Add"), button -> {
            TradeRequest request = modBase.getTradeRequestInputHandler().handleGUIInput(enchantmentTextFieldWidget.getText(), levelTextFieldWidget.getText(), priceTextFieldWidget.getText());
            if (request != null) {
                if (!modBase.getTradeRequestContainer().getTradeRequests().contains(request)) {
                    tradeRequestListWidget.addEntry(new TradeRequestListWidget.TradeRequestEntry(request));
                    modBase.getTradeRequestContainer().addTradeRequest(request);
                    clearTextFieldWidgets(enchantmentTextFieldWidget, levelTextFieldWidget, priceTextFieldWidget);
                }
            } else {
                enchantmentTextFieldWidget.setEditableColor(ColorHelper.Argb.getArgb(255, 255, 0, 0));
            }
        }));

        this.addDrawableChild(new ButtonWidget(x + 70, px + 40, 50, 20, Text.of("Remove"), button -> {
            Enchantment enchantment = modBase.getTradeRequestInputHandler().getEnchantment(enchantmentTextFieldWidget.getText());
            if (enchantment == null) {
                enchantmentTextFieldWidget.setEditableColor(ColorHelper.Argb.getArgb(255, 255, 0, 0));
                return;
            }
            for (Iterator<TradeRequestListWidget.TradeRequestEntry> it = tradeRequestListWidget.children().iterator(); it.hasNext(); ) {
                TradeRequestListWidget.TradeRequestEntry entry = it.next();
                    if (entry.tradeRequest.enchantment().getTranslationKey().equals(enchantment.getTranslationKey())) {
                        modBase.getTradeRequestContainer().removeTradeRequest(entry.tradeRequest);
                        it.remove();
                        clearTextFieldWidgets(enchantmentTextFieldWidget, levelTextFieldWidget, priceTextFieldWidget);
                    }
            }
        }));
        this.addDrawableChild(tradeRequestListWidget);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        int px = (int) (this.width / 50f);
        int x = this.width - px - widgetWidth;

        this.textRenderer.draw(matrices, Text.of("Enchantment"), x + 10, px + 6, 0xE0E0E0);
        this.textRenderer.draw(matrices, Text.of("Level"), x + 20 + enchantmentWidth, px + 6, 0xE0E0E0);
        this.textRenderer.draw(matrices, Text.of("Price"), x + 30 + enchantmentWidth + levelWidth, px + 6, 0xE0E0E0);
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        int px = (int) (this.width / 50f);
        int x = this.width - px - widgetWidth;
        DrawableHelper.fill(matrices, x, px, this.width - px, this.height - px, ColorHelper.Argb.getArgb(150, 7, 7, 7));
    }

    public void clearTextFieldWidgets(TextFieldWidget... textFieldWidgets){
        Arrays.stream(textFieldWidgets).forEach(textFieldWidget -> textFieldWidget.setText(""));
    }

}
