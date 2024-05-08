package de.gamedude.evt.logic;

import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeRequestContainer;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.utils.TradeRequest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.Map;
import java.util.Optional;

public class CheckTradeState  extends State{

    public CheckTradeState(TradeWorkflow tradeWorkflow) {
        super(tradeWorkflow);
    }

    @Override
    public int run() {
        VillagerEntity villagerEntity = tradeWorkflow.getHandler(SelectionInterface.class).getVillager();
        TradeOfferList tradeOffers = villagerEntity.getOffers();
        Optional<TradeOffer> optionalTradeOffer = tradeOffers.stream().filter(tradeOffer -> tradeOffer.getSellItem().getItem() == Items.ENCHANTED_BOOK).findFirst();
        if (optionalTradeOffer.isEmpty()) {
             // new repeat cycle / script
            return 1;
        }

        TradeRequestContainer tradeRequestContainer = tradeWorkflow.getHandler(TradeRequestContainer.class);
        Optional<TradeRequest> optionalTradeRequest = tradeRequestContainer.getValidTradeRequest(tradeOffers);

        optionalTradeRequest.ifPresentOrElse(tradeRequest -> {
            minecraftClient.player.sendMessage(Text.translatable("evt.logic.trade_found", "§e" + tradeRequest.enchantment().getName(tradeRequest.level()).getString(), "§a" + tradeRequest.cost()));
            minecraftClient.getSoundManager().play(new PositionedSoundInstance(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, SoundCategory.MASTER, 2f, 1f, new LocalRandom(0), MinecraftClient.getInstance().player.getBlockPos()));

            tradeRequestContainer.removeRequestByEnchantment(tradeRequest.enchantment());
            // TODO : getHandler(TradeAutomationProcessor.class).setTradeSlotID(tradeOffers.indexOf(bookOffer));
            // TODO: 'found' script
        }, () -> {
            // TODO: new 'repeat' script
        });

        return 1;
    }
}
