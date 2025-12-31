package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeRequestContainer;
import de.gamedude.evt.handler.TradeWithVillagerHandler;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.ParsingContext;
import de.gamedude.evt.utils.TradeRequest;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.Map;
import java.util.Optional;

public class CheckState extends State {

    private final TradeRequestContainer tradeRequestContainer;
    private final TradeWithVillagerHandler tradeWithVillagerHandler;

    public CheckState() {
        super(TradeWorkflow.INSTANCE);
        this.tradeRequestContainer = getTradeWorkflow().getHandler(TradeRequestContainer.class);
        this.tradeWithVillagerHandler = getTradeWorkflow().getHandler(TradeWithVillagerHandler.class);
    }

    @Override
    public int run() {
        ClientPlayerEntity player = playerSupplier.get();
        VillagerEntity villagerEntity = getTradeWorkflow().getHandler(SelectionInterface.class).getVillager().get();
        if ( villagerEntity == null ) {
            player.sendMessage(Text.of("TODO: CheckState failed, villager null!"));
            return 1;
        }

        int status = checkForOffer(villagerEntity);
        player.sendMessage(Text.of("TODO: Executed CheckState with status - " + status));
        if ( status == 1 ) {
            // TODO ANIMATE SCRIPT TO REPEAT
            return 1;
        }
        // TODO ANIMATE SCRIPT TO BUY @onFound
        return 0;
    }

    private int checkForOffer(VillagerEntity villagerEntity) {
        TradeOfferList tradeOfferList = villagerEntity.getOffers();
        Optional<TradeOffer> optionalTradeOffer = tradeOfferList.stream().filter(tradeOffer -> tradeOffer.getSellItem().getItem() == Items.ENCHANTED_BOOK).findFirst();
        if (optionalTradeOffer.isEmpty())
            return 1;

        TradeOffer tradeOffer = optionalTradeOffer.get();

        Map.Entry<Enchantment, Integer> entry0 = EnchantmentHelper.get(tradeOffer.getSellItem()).entrySet().iterator().next();
        Enchantment enchantment = entry0.getKey();
        int level = entry0.getValue();
        int cost = tradeOffer.getAdjustedFirstBuyItem().getCount();

        TradeRequest tradeRequest = new TradeRequest(enchantment, level, cost);

        if(tradeRequestContainer.matchesAny(tradeRequest)) {
            int slotId = tradeOfferList.indexOf(tradeOffer);
            this.tradeWithVillagerHandler.setTradeIndex(slotId);
            System.out.println("TODO: Found suitable TRADE REQUEST slot id: " + slotId);
            return 0;
        }
        return 1;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public void initState() {

    }

    public static State parse(String[] args, ParsingContext ctx) throws Exception {
        return new CheckState();
    }
}
