package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.*;

public class TradeRequestContainer {

    private final Set<TradeRequest> tradeRequestSet;

    public TradeRequestContainer() {
        this.tradeRequestSet = new LinkedHashSet<>();
    }

    public void addTradeRequest(TradeRequest tradeRequest) {
        if(tradeRequest != null)
            this.tradeRequestSet.add(tradeRequest);
    }

    public void removeTradeRequestByEnchantment(RegistryEntry<Enchantment> enchantment) {
        this.tradeRequestSet.removeIf(request -> request.enchantment().getIdAsString().equalsIgnoreCase(enchantment.getIdAsString()));
    }

    public void removeTradeRequest(TradeRequest request) {
        this.tradeRequestSet.remove(request);
    }

    public Set<TradeRequest> getTradeRequests() {
        return tradeRequestSet;
    }

    public boolean matchesAny(TradeRequest request) {
        return tradeRequestSet.stream().anyMatch(request::matches);
    }

}
