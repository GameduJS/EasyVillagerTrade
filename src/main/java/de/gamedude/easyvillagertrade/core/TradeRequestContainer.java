package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.enchantment.Enchantment;

import java.util.HashSet;
import java.util.Set;

public class TradeRequestContainer {

    private final Set<TradeRequest> tradeRequestSet;

    public TradeRequestContainer() {
        this.tradeRequestSet = new HashSet<>();
    }

    public void addTradeRequest(TradeRequest tradeRequest) {
        this.tradeRequestSet.add(tradeRequest);
    }

    public void removeTradeRequestByEnchantment(Enchantment enchantment) {
        this.tradeRequestSet.removeIf(request -> request.enchantment().getTranslationKey().equals(enchantment.getTranslationKey()));
    }

    public Set<TradeRequest> getTradeRequests() {
        return tradeRequestSet;
    }

    public boolean matchesAny(TradeRequest request) {
        return tradeRequestSet.stream().anyMatch(request::matches);
    }

}
