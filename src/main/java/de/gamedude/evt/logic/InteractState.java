package de.gamedude.evt.logic;

import de.gamedude.evt.handler.SelectionInterface;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.function.Supplier;

public class InteractState  extends State{

    private final Supplier<VillagerEntity> villagerEntity;

    public InteractState() {
        this.villagerEntity = tradeWorkflow.getHandler(SelectionInterface.class).getVillager();
    }

    @Override
    public int run() {
        if(villagerEntity.get() == null) {
            player.get().sendMessage(Text.of("No villager has been selected!"));
            return 2;
        }
        client.interactionManager.interactEntity(client.player, villagerEntity.get(), Hand.MAIN_HAND);
        return 1;
    }
}
