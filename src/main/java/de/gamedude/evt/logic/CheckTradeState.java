package de.gamedude.evt.logic;

import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeRequestContainer;
import de.gamedude.evt.handler.TradeWithVillagerHandler;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.Script;
import de.gamedude.evt.script.ScriptManager;
import de.gamedude.evt.utils.TradeRequest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

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
            // tradeWorkflow.getHandler(ScriptManager.class).getScript().tickScriptType(Script.ScriptType.REPEAT);
            return 1;
        }

        TradeRequestContainer tradeRequestContainer = tradeWorkflow.getHandler(TradeRequestContainer.class);
        Optional<Pair<TradeOffer, TradeRequest>> optionalTradeRequest = tradeRequestContainer.getValidTradeRequest(tradeOffers); // TODO:

        optionalTradeRequest.ifPresentOrElse(pair -> {
            TradeRequest tradeRequest = pair.getRight();
            TradeOffer offer = pair.getLeft();

            client.player.sendMessage(Text.translatable("evt.logic.trade_found", "§e" + tradeRequest.enchantment().getName(tradeRequest.level()).getString(), "§a" + tradeRequest.cost()));
            client.getSoundManager().play(new PositionedSoundInstance(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, SoundCategory.MASTER, 2f, 1f, new LocalRandom(0), MinecraftClient.getInstance().player.getBlockPos()));

            tradeRequestContainer.removeRequestByEnchantment(tradeRequest.enchantment());
            tradeWorkflow.getHandler(TradeWithVillagerHandler.class).setTradeIndex(tradeOffers.indexOf(offer));
            //tradeWorkflow.getHandler(ScriptManager.class).getScript().tickScriptType(Script.ScriptType.FOUND);

            System.out.println("[DEBUG] CheckTradeState.run: " + tradeOffers.indexOf(offer));
        }, () -> {
            //tradeWorkflow.getHandler(ScriptManager.class).getScript().tickScriptType(Script.ScriptType.REPEAT)
        });

        return 1;
    }
}
