package de.gamedude.evt.handler;

import de.gamedude.evt.utils.TradeRequest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TradeRequestContainer {

    private final List<TradeRequest> tradeRequests;

    public TradeRequestContainer() {
        this.tradeRequests = new ArrayList<>();
    }

    public void addRequest(TradeRequest tradeRequest) {
        this.tradeRequests.add(tradeRequest);
    }

    public void removeRequest(TradeRequest tradeRequest) {
        this.tradeRequests.remove(tradeRequest);
    }
    
    public void removeRequestByEnchantment(Enchantment enchantment) {
        this.tradeRequests.removeIf( tradeRequest -> enchantment.getTranslationKey().equals(enchantment.getTranslationKey()) );
    }

    public List<TradeRequest> getRequests() {
        return tradeRequests;
    }

    public boolean checkTradeRequest(TradeOfferList tradeOfferList) {
        Optional<TradeOffer> optionalTradeOffer = tradeOfferList.stream().filter(tradeOffer -> tradeOffer.getSellItem().getItem() == Items.ENCHANTED_BOOK).findFirst();
        if (optionalTradeOffer.isEmpty()) {
            // setState(TradingState.BREAK_WORKSTATION);
            return false;
        }

        TradeOffer tradeOffer = optionalTradeOffer.get();
        Enchantment enchantment = EnchantmentHelper.get(tradeOffer.getSellItem()).keySet().iterator().next();
        int level = EnchantmentHelper.getLevel(enchantment, tradeOffer.getSellItem());
        int cost = tradeOffer.getAdjustedFirstBuyItem().getCount();

        TradeRequest tradeRequest = new TradeRequest(enchantment, level, cost);

        return tradeRequests.stream().anyMatch(request -> request.matchRequest(tradeRequest));
    }

}
