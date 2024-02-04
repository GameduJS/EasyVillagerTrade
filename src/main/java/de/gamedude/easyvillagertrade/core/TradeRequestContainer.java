package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.config.Config;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import net.minecraft.enchantment.Enchantment;

import java.util.*;

public class TradeRequestContainer implements ConfigDependent {

    private final Set<TradeRequest> tradeRequestSet;

    public TradeRequestContainer() {
        this.tradeRequestSet = new LinkedHashSet<>();
    }

    public void addTradeRequest(TradeRequest tradeRequest) {
        if(tradeRequest != null)
            this.tradeRequestSet.add(tradeRequest);
    }

    public void removeTradeRequestByEnchantment(Enchantment enchantment) {
        this.tradeRequestSet.removeIf(request -> request.enchantment().getTranslationKey().equals(enchantment.getTranslationKey()));
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

    @Override
    public void loadConfig(Config config) {

    }

    @Override
    public void reloadConfig(Config config) {

    }
}
