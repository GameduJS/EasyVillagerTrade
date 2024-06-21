package de.gamedude.evt.logic;

import de.gamedude.evt.EasyVillagerTrade;
import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeRequestContainer;
import de.gamedude.evt.handler.TradeWithVillagerHandler;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.Script;
import de.gamedude.evt.script.ScriptManager;
import de.gamedude.evt.utils.TradeRequest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.Optional;

public class CheckTradeState  extends State{

    @Override
    public int run() {
        VillagerEntity villagerEntity = tradeWorkflow.getHandler(SelectionInterface.class).getVillager().get();
        TradeOfferList tradeOffers = villagerEntity.getOffers();

        TradeRequestContainer tradeRequestContainer = tradeWorkflow.getHandler(TradeRequestContainer.class);
        Optional<Pair<TradeOffer, TradeRequest>> optionalTradeRequest = tradeRequestContainer.getValidTradeRequest(tradeOffers);

        optionalTradeRequest.ifPresentOrElse(pair -> {
            TradeRequest tradeRequest = pair.getRight();
            TradeOffer offer = pair.getLeft();

            tradeRequestContainer.removeRequestByEnchantment(tradeRequest.enchantment());
            tradeWorkflow.getHandler(TradeWithVillagerHandler.class).setTradeIndex(tradeOffers.indexOf(offer));
            tradeWorkflow.getHandler(ScriptManager.class).getScript().tickScriptType(Script.ScriptType.FOUND);

            player.get().sendMessage(Text.translatable("evt.logic.trade_found", "§e" + tradeRequest.enchantment().getName(tradeRequest.level()).getString(), "§a" + tradeRequest.cost()));

            if(!EasyVillagerTrade.CONFIG.getProperty("shouldPlaySound").getAsBoolean())
                return;
            SoundEvent soundEvent = Registries.SOUND_EVENT.get(new Identifier(EasyVillagerTrade.CONFIG.getProperty("soundPlayed").getAsString()));
            PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(soundEvent, SoundCategory.MASTER, 2f, 1f, Random.create(), player.get().getBlockPos());
            client.getSoundManager().play(positionedSoundInstance);
        }, () -> {
            if(player.get().currentScreenHandler instanceof MerchantScreenHandler) {
                // obsolete since closing in mixin
                player.get().closeHandledScreen();
            }
            tradeWorkflow.getHandler(ScriptManager.class).getScript().tickScriptType(Script.ScriptType.REPEAT);
        });

        return 1;
    }
}
