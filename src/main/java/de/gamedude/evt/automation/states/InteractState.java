package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class InteractState extends State {

    public InteractState() {
        super(TradeWorkflow.INSTANCE);
    }

    @Override
    public int run() {
        ClientPlayerEntity player = playerSupplier.get();
        VillagerEntity villagerEntity = getTradeWorkflow().getHandler(SelectionInterface.class).getVillager()
                .get();
        if ( villagerEntity == null ) {
            player.sendMessage(Text.of("TODO: That was not supposed to happen, please add SELECT-State infront of INTERACT-State"));
            return 1;
        }

        player.swingHand(Hand.MAIN_HAND);
        ActionResult actionResult = client.interactionManager.interactEntity(player, villagerEntity, Hand.MAIN_HAND);
        player.sendMessage(Text.of("TODO: Executed InteractState with status - " + actionResult.isAccepted()));
        return 0;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public void initState() {

    }
}
