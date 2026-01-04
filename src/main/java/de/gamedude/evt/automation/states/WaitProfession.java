package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.ParsingContext;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;

public class WaitProfession extends State {


    private final SelectionInterface selectionInterface;
    private final VillagerProfession profession;

    public WaitProfession(VillagerProfession profession) {
        super(TradeWorkflow.INSTANCE);
        this.selectionInterface = getTradeWorkflow().getHandler(SelectionInterface.class);
        this.profession = profession;
    }

    public static State parse(String[] args, ParsingContext ctx) throws Exception {
        if (args.length != 1) {
            throw new Exception("Usage: WAIT_PROFESSION <profession>");
        }
        String possibleProfession =  args[0].toLowerCase();
        try {
            VillagerProfession prof =
                    Registries.VILLAGER_PROFESSION.get(Identifier.tryParse(possibleProfession));
            return new WaitProfession(prof);
        } catch (Exception e) {
            throw new Exception("Invalid profession: " + possibleProfession);
        }
    }

    @Override
    public int run() {
        return 0;
    }

    @Override
    public boolean isDone() {
        VillagerEntity villager = selectionInterface.getVillager().get();
        if ( villager == null ) {
            System.out.println("[DEBUG] WAIT_PROFESSION Villager is null");
            return false;
        }
        return villager.getVillagerData().getProfession().equals(profession);
    }

    @Override
    public void initState() { }
}
