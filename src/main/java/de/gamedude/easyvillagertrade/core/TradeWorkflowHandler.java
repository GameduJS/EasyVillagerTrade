package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.config.Config;
import de.gamedude.easyvillagertrade.core.autowalk.VillagerHubEngine;
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
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TradeWorkflowHandler {
    public TradingState state;

    private final Map<Class<? extends ConfigDependent>, ConfigDependent> handlersMap;
    private final Config config;
    private final MinecraftClient minecraftClient;

    public TradeWorkflowHandler() {
        this.minecraftClient = MinecraftClient.getInstance();
        this.handlersMap = new HashMap<>();

        this.config = new Config("easyvillagertrade");
        this.handlersMap.put(VillagerHubEngine.class, new VillagerHubEngine(this));
        this.handlersMap.put(TradeRequestContainer.class, new TradeRequestContainer());
        this.handlersMap.put(TradeRequestInputHandler.class, new TradeRequestInputHandler());
        this.handlersMap.put(SelectionInterface.class, new SelectionInterface(this));
        this.handlersMap.put(TradeAutomationProcessor.class, new TradeAutomationProcessor(this));
        //Load config at the beginning
        this.handlersMap.values().forEach(configDependent -> configDependent.loadConfig(config));

        this.state = TradingState.INACTIVE;
    }

    public Config getConfig() {
        return config;
    }

    @SuppressWarnings("unchecked")
    public <T extends ConfigDependent> T getHandler(Class<T> clazz) {
        return (T) this.handlersMap.get(clazz);
    }

    public void setState(TradingState state) {
        this.state = state;
    }

    public void tick() {
        if (state == TradingState.INACTIVE)
            return;
        SelectionInterface selectionInterface = getHandler(SelectionInterface.class);
        TradeAutomationProcessor tradeAutomationProcessor = getHandler(TradeAutomationProcessor.class);
        switch (state) {
            case BREAK_WORKSTATION -> handleBreak();
            case PLACE_WORKSTATION -> handlePlacement();
            case WAIT_JOB_LOSS -> {
                if(selectionInterface.getVillager().getVillagerData().getProfession() == VillagerProfession.NONE)
                    setState(TradingState.PLACE_WORKSTATION);
            }

            case SELECT_TRADE -> tradeAutomationProcessor.selectTrade();
            case APPLY_TRADE -> tradeAutomationProcessor.applyTrade();
            case PICKUP_TRADE -> tradeAutomationProcessor.pickupBook();
        }
    }

    private void handlePlacement() {
        ClientPlayerEntity player = minecraftClient.player;
        BlockPos lecternPos = getHandler(SelectionInterface.class).getLecternPos();

        if(player.getOffHandStack().equals(ItemStack.EMPTY)) {
            player.sendMessage(Text.translatable("evt.logic.lectern_non"));
            setState(TradingState.INACTIVE);
            return;
        }

        // Place block
        BlockHitResult hitResult = new BlockHitResult(new Vec3d(lecternPos.getX(), lecternPos.getY(), lecternPos.getZ()), Direction.UP, lecternPos, false);
        minecraftClient.interactionManager.interactBlock(player, Hand.OFF_HAND, hitResult);
        minecraftClient.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.OFF_HAND));

        setState(TradingState.WAIT_PROFESSION);
    }

    private void handleBreak() {
        World world = minecraftClient.world;
        ClientPlayerEntity player = minecraftClient.player;
        BlockPos blockPos = getHandler(SelectionInterface.class).getLecternPos();

        if (world == null || player == null)
            return;
        ItemStack axe = player.getMainHandStack();
        if(axe.getMaxDamage() - axe.getDamage() < 20) {
            player.sendMessage(Text.translatable("evt.logic.axe_durability"));
            setState(TradingState.INACTIVE);
            return;
        }

        if(blockPos == null) {
            player.sendMessage(Text.translatable("evt.logic.pos_not_set"));
            setState(TradingState.INACTIVE);
            return;
        }

        if (world.getBlockState(getHandler(SelectionInterface.class).getLecternPos()).getBlock() == Blocks.LECTERN) {
            minecraftClient.interactionManager.updateBlockBreakingProgress(getHandler(SelectionInterface.class).getLecternPos(), Direction.UP);
            player.swingHand(Hand.MAIN_HAND, true);
            player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        } else {
            state = TradingState.WAIT_JOB_LOSS;
        }
    }

    public void checkVillagerOffers(TradeOfferList tradeOffers) {
        Optional<TradeOffer> optionalTradeOffer = tradeOffers.stream().filter(tradeOffer -> tradeOffer.getSellItem().getItem() == Items.ENCHANTED_BOOK).findFirst();
        if (optionalTradeOffer.isEmpty()) {
            setState(TradingState.BREAK_WORKSTATION);
            return;
        }
        TradeOffer bookOffer = optionalTradeOffer.get();

        Map<Enchantment, Integer> enchantmentMap = EnchantmentHelper.get(bookOffer.getSellItem());
        Enchantment bookEnchantment = enchantmentMap.keySet().iterator().next();
        int level = enchantmentMap.values().iterator().next();

        TradeRequest offer = new TradeRequest(bookEnchantment, level, bookOffer.getAdjustedFirstBuyItem().getCount());
        TradeRequestContainer tradeRequestContainer = getHandler(TradeRequestContainer.class);

        if (tradeRequestContainer.matchesAny(offer)) {
            minecraftClient.player.sendMessage(Text.translatable("evt.logic.trade_found", "§e" + bookEnchantment.getName(level).getString(), "§a" + offer.maxPrice()));
            minecraftClient.getSoundManager().play(new PositionedSoundInstance(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, SoundCategory.MASTER, 2f, 1f, new LocalRandom(0), MinecraftClient.getInstance().player.getBlockPos()));

            tradeRequestContainer.removeTradeRequestByEnchantment(bookEnchantment);
            getHandler(TradeAutomationProcessor.class).setTradeSlotID(tradeOffers.indexOf(bookOffer));
            setState(TradingState.SELECT_TRADE);
        } else {
            setState(TradingState.BREAK_WORKSTATION);
        }
    }

    public void handleInteractionWithVillager() {
        minecraftClient.interactionManager.interactEntity(MinecraftClient.getInstance().player, getHandler(SelectionInterface.class).getVillager(), Hand.MAIN_HAND);
    }

}
