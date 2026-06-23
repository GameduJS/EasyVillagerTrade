package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.interventions.NoJobIntervention;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import de.gamedude.easyvillagertrade.utils.TradingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class EasyVillagerTradeBase {
    private TradingState state;

    private final TradeRequestContainer tradeRequestContainer;
    private final SelectionInterface selectionInterface;
    private final TradeRequestInputHandler tradeRequestInputHandler;
    private final TradeInterface tradeInterface;

    private final Minecraft minecraftClient;

    private final NoJobIntervention noJobIntervention;

    public EasyVillagerTradeBase() {
        this.minecraftClient = Minecraft.getInstance();
        this.tradeRequestContainer = new TradeRequestContainer();
        this.selectionInterface = new SelectionInterface(this);
        this.tradeRequestInputHandler = new TradeRequestInputHandler();
        this.tradeInterface = new TradeInterface(this);

        this.noJobIntervention = new NoJobIntervention(this);

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
                if (selectionInterface.getVillager().getVillagerData().profession().is(VillagerProfession.NONE))
                    setState(TradingState.PLACE_WORKSTATION);
            }
        }
        this.noJobIntervention.checkSystem(state);
    }

    private void handlePlacement() {
        LocalPlayer player = minecraftClient.player;
        BlockPos lecternPos = selectionInterface.getLecternPos();

        if (player.getOffhandItem().equals(ItemStack.EMPTY)) {
            player.sendSystemMessage(Component.translatable("evt.logic.lectern_non"));
            setState(TradingState.INACTIVE);
            return;
        }

        BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(lecternPos.below()).add(0, 0.5, 0), Direction.UP, lecternPos.below(), false);
        minecraftClient.gameMode.useItemOn(player, InteractionHand.OFF_HAND, hitResult);
        player.swing(InteractionHand.OFF_HAND);

        setState(TradingState.WAIT_PROFESSION);
    }

    private void handleBreak() {
        Level world = minecraftClient.level;
        Player player = minecraftClient.player;
        BlockPos blockPos = getSelectionInterface().getLecternPos();

        if (world == null || player == null)
            return;

        int preventionValue = EasyVillagerTrade.CONFIG.getProperty("preventAxeBreakingValue").getAsInt();
        ItemStack tool = player.getItemInHand(InteractionHand.MAIN_HAND);
        if(preventionValue != -1) {
            if (tool.getMaxDamage() - tool.getDamageValue() <= preventionValue) {
                player.sendSystemMessage(Component.translatable("evt.logic.axe_durability"));
                setState(TradingState.INACTIVE);
                return;
            }
        }

        if (blockPos == null) {
            player.sendSystemMessage(Component.translatable("evt.logic.pos_not_set"));
            setState(TradingState.INACTIVE);
            return;
        }

        if (world.getBlockState(getSelectionInterface().getLecternPos()).getBlock() == Blocks.LECTERN) {
            minecraftClient.gameMode.continueDestroyBlock(getSelectionInterface().getLecternPos(), Direction.UP);
            player.swing(InteractionHand.MAIN_HAND, true);
            minecraftClient.getConnection().send(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
        } else {
            state = TradingState.WAIT_JOB_LOSS;
        }
    }

    public void checkVillagerOffers(MerchantOffers tradeOffers) {
        MerchantOffer bookOffer = null;
        for (MerchantOffer offers : tradeOffers)
            if (offers.getResult().getItem() == Items.ENCHANTED_BOOK) {
                bookOffer = offers;
                break;
            }

        if (bookOffer == null) {
            setState(TradingState.BREAK_WORKSTATION);
            return;
        }

        Holder<Enchantment>  enchantmentHolder = bookOffer.getResult().get(DataComponents.STORED_ENCHANTMENTS).keySet().iterator().next();
        int level = bookOffer.getResult().get(DataComponents.STORED_ENCHANTMENTS).getLevel(enchantmentHolder);

        TradeRequest offer = new TradeRequest(enchantmentHolder, level, bookOffer.getCostA().getCount());

        if(EasyVillagerTrade.CONFIG.getProperty("debugEnchantments").getAsBoolean()) {
            minecraftClient.player.sendSystemMessage(Component.translatable("evt.logic.trade.debug", "§a" + offer.maxPrice(), "§e" + offer.getNameEnchantment().getString()));
        }

        if (tradeRequestContainer.matchesAny(offer)) {
            minecraftClient.player.sendSystemMessage(Component.translatable("evt.logic.trade_found", "§e" + offer.getNameEnchantment().getString(), "§a" + offer.maxPrice()));
            minecraftClient.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.AMETHYST_CLUSTER_BREAK, 1f));

            tradeRequestContainer.removeTradeRequestByEnchantment(offer.enchantmentHolder());
            tradeInterface.setTradeSlotID(tradeOffers.indexOf(bookOffer));
            setState(TradingState.SELECT_TRADE);
        } else {
            setState(TradingState.BREAK_WORKSTATION);
        }
    }

    public void handleInteractionWithVillager() {
        InteractionResult result = Minecraft.getInstance().gameMode.interact(minecraftClient.player,
                getSelectionInterface().getVillager(),
                new EntityHitResult(getSelectionInterface().getVillager()),
                InteractionHand.MAIN_HAND);
    }

}
