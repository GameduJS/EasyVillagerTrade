package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.utils.TradeRequest;
import de.gamedude.easyvillagertrade.utils.TradingState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;

public class EasyVillagerTradeBase {
    private TradingState state;

    private final TradeRequestContainer tradeRequestContainer;
    private final SelectionInterface selectionInterface;
    private final TradeRequestInputHandler tradeRequestInputHandler;
    private final TradeInterface tradeInterface;

    private final MinecraftClient minecraftClient;

    public EasyVillagerTradeBase() {
        this.minecraftClient = MinecraftClient.getInstance();
        this.tradeRequestContainer = new TradeRequestContainer();
        this.selectionInterface = new SelectionInterface(this);
        this.tradeRequestInputHandler = new TradeRequestInputHandler();
        this.tradeInterface = new TradeInterface(this);

        this.state = TradingState.INACTIVE;
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

    public void setState(TradingState state) {
        this.state = state;
    }

    public TradingState getState() {
        return state;
    }

    public void handle() {
        if (state == TradingState.INACTIVE)
            return;
        switch (state) {
            case BREAK_WORKSTATION -> handleBreak();
            case PLACE_WORKSTATION -> handlePlacement();
            case SELECT_TRADE -> tradeInterface.selectTrade();
            case APPLY_TRADE -> tradeInterface.applyTrade();
            case PICKUP_TRADE -> tradeInterface.pickupBook();
            case WAIT_JOB_LOSS -> {
                if (selectionInterface.getVillager().getVillagerData().getProfession() == VillagerProfession.NONE)
                    setState(TradingState.PLACE_WORKSTATION);
            }
        }
    }

    private void handlePlacement() {
        ClientPlayerEntity player = minecraftClient.player;
        BlockPos lecternPos = selectionInterface.getLecternPos();

        if (player.getOffHandStack().equals(ItemStack.EMPTY)) {
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
        BlockPos blockPos = getSelectionInterface().getLecternPos();

        if (world == null || player == null)
            return;
        ItemStack axe = player.getMainHandStack();
        if (axe.getMaxDamage() - axe.getDamage() < 20) {
            player.sendMessage(Text.translatable("evt.logic.axe_durability"));
            setState(TradingState.INACTIVE);
            return;
        }

        if (blockPos == null) {
            player.sendMessage(Text.translatable("evt.logic.pos_not_set"));
            setState(TradingState.INACTIVE);
            return;
        }

        if (world.getBlockState(getSelectionInterface().getLecternPos()).getBlock() == Blocks.LECTERN) {
            minecraftClient.interactionManager.updateBlockBreakingProgress(getSelectionInterface().getLecternPos(), Direction.UP);
            player.swingHand(Hand.MAIN_HAND, true);
            player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        } else {
            state = TradingState.WAIT_JOB_LOSS;
        }
    }

    public void checkVillagerOffers(TradeOfferList tradeOffers) {
        TradeOffer bookOffer = null;
        for (TradeOffer offers : tradeOffers)
            if (offers.getSellItem().getItem() == Items.ENCHANTED_BOOK) {
                bookOffer = offers;
                break;
            }

        if (bookOffer == null) {
            setState(TradingState.BREAK_WORKSTATION);
            return;
        }


        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(bookOffer.getSellItem());
        Enchantment bookEnchantment = enchantments.getEnchantments().iterator().next().value();
        int level = enchantments.getLevel(bookEnchantment);

        TradeRequest offer = new TradeRequest(bookEnchantment, level, bookOffer.getDisplayedFirstBuyItem().getCount());

        if (tradeRequestContainer.matchesAny(offer)) {
            minecraftClient.player.sendMessage(Text.translatable("evt.logic.trade_found", "§e" + bookEnchantment.getName(level).getString(), "§a" + offer.maxPrice()));
            minecraftClient.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, 1f));

            tradeRequestContainer.removeTradeRequestByEnchantment(bookEnchantment);
            tradeInterface.setTradeSlotID(tradeOffers.indexOf(bookOffer));
            setState(TradingState.SELECT_TRADE);
        } else {
            setState(TradingState.BREAK_WORKSTATION);
        }
    }

    public void handleInteractionWithVillager() {
        minecraftClient.interactionManager.interactEntity(MinecraftClient.getInstance().player, selectionInterface.getVillager(), Hand.MAIN_HAND);
    }

}
