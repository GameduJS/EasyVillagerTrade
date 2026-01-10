package de.gamedude.evt.handler;

import de.gamedude.evt.utils.TradeRequest;
import net.minecraft.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

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
