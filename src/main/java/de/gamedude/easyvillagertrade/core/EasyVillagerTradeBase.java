package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import de.gamedude.easyvillagertrade.utils.TradingState;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class EasyVillagerTradeBase {
    private TradingState state;

    private final TradeRequestContainer tradeRequestContainer;
    private final SelectionInterface selectionInterface;
    private final TradeRequestInputHandler tradeRequestInputHandler;
    private final TradeInterface tradeInterface;

    private final Minecraft minecraftClient;

    public EasyVillagerTradeBase() {
        this.minecraftClient = Minecraft.getInstance();
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
                if (selectionInterface.getVillager().getVillagerData().profession().getKey().orElse(VillagerProfession.NITWIT).equals(VillagerProfession.NONE))
                    setState(TradingState.PLACE_WORKSTATION);
            }
        }
    }

    private void handlePlacement() {
        ClientPlayerEntity player = minecraftClient.player;
        BlockPos lecternPos = selectionInterface.getLecternPos();

        if (player.getOffHandStack().equals(ItemStack.EMPTY)) {
            player.sendMessage(Text.translatable("evt.logic.lectern_non"), false);
            setState(TradingState.INACTIVE);
            return;
        }

        // Place block
        BlockHitResult hitResult = new BlockHitResult(lecternPos.toBottomCenterPos().add(0, 1,0), Direction.UP, lecternPos, false);

        minecraftClient.interactionManager.interactBlock(player, Hand.OFF_HAND, hitResult);
        player.swingHand(Hand.OFF_HAND);

        setState(TradingState.WAIT_PROFESSION);
    }

    private void handleBreak() {
        World world = minecraftClient.world;
        ClientPlayerEntity player = minecraftClient.player;
        BlockPos blockPos = getSelectionInterface().getLecternPos();

        if (world == null || player == null)
            return;

        int preventionValue = EasyVillagerTrade.CONFIG.getProperty("preventAxeBreakingValue").getAsInt();
        ItemStack tool = player.getMainHandStack();
        if(preventionValue != -1) {
            if (tool.getMaxDamage() - tool.getDamage() <= preventionValue) {
                player.sendMessage(Text.translatable("evt.logic.axe_durability"), false);
                setState(TradingState.INACTIVE);
                return;
            }
        }

        if (blockPos == null) {
            player.sendMessage(Text.translatable("evt.logic.pos_not_set"), false);
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

    public void checkVillagerOffers(MerchantOffers tradeOffers) {
        MerchantOffer bookOffer = null;
        for (MerchantOffer offers : tradeOffers)
            if (offers.getResult().getItem() == net.minecraft.world.item.Items.ENCHANTED_BOOK) {
                bookOffer = offers;
                break;
            }

        if (bookOffer == null) {
            setState(TradingState.BREAK_WORKSTATION);
            return;
        }


        Holder<Enchantment>  enchantmentHolder = bookOffer.getResult().get(DataComponents.ENCHANTMENTS).keySet().iterator().next();
        int level = bookOffer.getResult().get(DataComponents.ENCHANTMENTS).getLevel(enchantmentHolder)

        TradeRequest offer = new TradeRequest(enchantmentHolder.value(), level, bookOffer.getCostA().getCount());

        if(EasyVillagerTrade.CONFIG.getProperty("debugEnchantments").getAsBoolean()) {
            minecraftClient.player.sendMessage(Text.translatable("evt.logic.trade.debug", "§a" + offer.maxPrice(), "§e" + Enchantment.getName(bookEnchantment, level).getString()), false);
        }

        if (tradeRequestContainer.matchesAny(offer)) {
            minecraftClient.player.sendMessage(Text.translatable("evt.logic.trade_found", "§e" + Enchantment.getName(bookEnchantment, level).getString(), "§a" + offer.maxPrice()), false);
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
