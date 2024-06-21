package de.gamedude.evt.logic;

import de.gamedude.evt.handler.SelectionInterface;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerProfession;

import java.util.function.Supplier;

public class WaitProfessionState extends State {

    Supplier<VillagerEntity> villager;
    VillagerProfession profession;

    public WaitProfessionState(String profession) {
        this.villager = tradeWorkflow.getHandler(SelectionInterface.class).getVillager();
        this.profession = (profession.equals("NONE")) ? VillagerProfession.NONE : VillagerProfession.LIBRARIAN;
    }

    @Override
    public int run() {
        if (this.villager.get() == null) {
            player.get().sendMessage(Text.of("No villager has been selected!"));
            return 2;
        }
      return villager.get().getVillagerData().getProfession() == profession ? 1 : 0;
    }
}
