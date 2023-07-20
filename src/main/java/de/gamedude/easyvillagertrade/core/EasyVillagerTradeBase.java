package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.scripting.core.ScriptCache;
import de.gamedude.easyvillagertrade.scripting.core.ScriptFactory;
import de.gamedude.easyvillagertrade.scripting.core.script.Script;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import de.gamedude.easyvillagertrade.utils.TradingState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;

import java.util.Map;
import java.util.function.Consumer;

public class EasyVillagerTradeBase {
    private TradingState state = TradingState.INACTIVE;

    private TradeRequestContainer tradeRequestContainer;
    private SelectionInterface selectionInterface;
    private TradeRequestInputHandler tradeRequestInputHandler;
    private ScriptCache scriptCache;

    private boolean isRegistered = false;

    public EasyVillagerTradeBase() { }

    public void register() {
        this.tradeRequestContainer = new TradeRequestContainer();
        this.selectionInterface = new SelectionInterface(this);
        this.tradeRequestInputHandler = new TradeRequestInputHandler();
        this.scriptCache = new ScriptCache(new ScriptFactory());
        this.isRegistered = true;
    }

    public TradeRequestInputHandler getTradeRequestInputHandler() {
        return tradeRequestInputHandler;
    }

    public SelectionInterface getSelectionInterface() {
        return this.selectionInterface;
    }

    public TradeRequestContainer getTradeRequestContainer() {
        return this.tradeRequestContainer;
    }

    public ScriptCache getScriptCache() {
        return scriptCache;
    }

    public void setState(TradingState state) {
        this.state = state;
    }

    public TradingState getState() {
        return state;
    }

    private final Consumer<Script> scriptConsumer = Script::tickScript;

    public void handle() {
        this.scriptCache.getActiveScript(scriptConsumer);

        if (state == TradingState.INACTIVE)
            return;
        switch (state) {
            case BREAK_WORKSTATION -> handleBreak();
            case PLACE_WORKSTATION -> handlePlacement();
        }
    }

    private void handlePlacement() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        BlockPos lecternPos = selectionInterface.getLecternPos();

        if(player.getOffHandStack().equals(ItemStack.EMPTY)) {
            player.sendMessage(Text.of("§8| §7The query has been stopped due to the lack of lecterns."));
            setState(TradingState.INACTIVE);
            return;
        }

        // Place block
        BlockHitResult hitResult = new BlockHitResult(new Vec3d(lecternPos.getX(), lecternPos.getY(), lecternPos.getZ()), Direction.UP, lecternPos, false);
        MinecraftClient.getInstance().interactionManager.interactBlock(player, Hand.OFF_HAND, hitResult);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.OFF_HAND));

        setState(TradingState.WAIT_PROFESSION);
    }

    private void handleBreak() {
        World world = MinecraftClient.getInstance().world;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (world == null || player == null)
            return;
        ItemStack axe = player.getMainHandStack();
        if(axe.getMaxDamage() - axe.getDamage() < 20) {
            player.sendMessage(Text.of("§8| §7The query has been stopped due to the lack of durability of the axe"));
            setState(TradingState.INACTIVE);
            return;
        }
        if (world.getBlockState(getSelectionInterface().getLecternPos()).getBlock() == Blocks.LECTERN) {
            MinecraftClient.getInstance().interactionManager.updateBlockBreakingProgress(getSelectionInterface().getLecternPos(), Direction.UP);
            player.swingHand(Hand.MAIN_HAND, true);
            player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        } else
            state = TradingState.PLACE_WORKSTATION;
    }

    public void checkVillagerOffers(TradeOfferList tradeOffers) {
        TradeOffer bookOffer = null;
        for (TradeOffer offers : tradeOffers)
            if (offers.getSellItem().getItem() == Items.ENCHANTED_BOOK)
                bookOffer = offers;
        if (bookOffer == null) {
            setState(TradingState.BREAK_WORKSTATION);
            return;
        }
        Map<Enchantment, Integer> enchantmentMap = EnchantmentHelper.get(bookOffer.getSellItem());
        Enchantment bookEnchantment = enchantmentMap.keySet().iterator().next();
        int level = enchantmentMap.values().iterator().next();

        TradeRequest offer = new TradeRequest(bookEnchantment, level, bookOffer.getAdjustedFirstBuyItem().getCount());

        if (tradeRequestContainer.matchesAny(offer)) {
            MinecraftClient.getInstance().player.sendMessage(Text.of("§8| §7The enchantment §e" + bookEnchantment.getName(level).getString() + "§7 has been found for §a" + offer.maxPrice() + " Emeralds"));
            setState(TradingState.INACTIVE);
            MinecraftClient.getInstance().getSoundManager().play(new PositionedSoundInstance(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, SoundCategory.MASTER, 2f, 1f, new LocalRandom(0), MinecraftClient.getInstance().player.getBlockPos()));
            tradeRequestContainer.removeTradeRequestByEnchantment(bookEnchantment);

            this.scriptCache.getActiveScript(script -> {
                if(!script.setTriggered(true))
                    this.scriptCache.setActiveScript(null);
                System.out.println("hello!! test triggered");
            });

        } else {
            setState(TradingState.BREAK_WORKSTATION);
        }
    }

    public void handleInteractionWithVillager() {
        MinecraftClient.getInstance().interactionManager.interactEntity(MinecraftClient.getInstance().player, selectionInterface.getVillager(), Hand.MAIN_HAND);
    }

    public boolean isActive() {
        return isRegistered;
    }
}
