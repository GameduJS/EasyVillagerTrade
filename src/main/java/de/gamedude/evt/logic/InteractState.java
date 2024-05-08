package de.gamedude.evt.logic;

import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Hand;

public class InteractState  extends State{

    private final VillagerEntity villagerEntity;

    public InteractState(TradeWorkflow tradeWorkflow) {
        super(tradeWorkflow);
        this.villagerEntity = tradeWorkflow.getHandler(SelectionInterface.class).getVillager();
    }

    @Override
    public int run() {
        if(villagerEntity == null) {
            // TODO: MESSAGE
            return 2;
        }
        minecraftClient.interactionManager.interactEntity(minecraftClient.player, villagerEntity, Hand.MAIN_HAND);
        return 1;
    }
}
