package de.gamedude.evt.handler;

import de.gamedude.evt.utils.TradeRequest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TradeRequestContainer implements Handler {

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
        this.tradeRequests.removeIf(tradeRequest -> tradeRequest.enchantment().getTranslationKey().equals(enchantment.getTranslationKey()) );
    }

    public List<TradeRequest> getRequests() {
        return tradeRequests;
    }

    public boolean matchesAny(TradeRequest tradeRequest) {
        return this.tradeRequests.stream().anyMatch(tradeRequest::matchRequest);
    }

}
