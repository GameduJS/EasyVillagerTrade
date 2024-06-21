package de.gamedude.evt.logic;

import de.gamedude.evt.handler.SelectionInterface;
import net.minecraft.text.Text;

public class SelectState extends State{

    @Override
    public int run() {
        return switch (tradeWorkflow.getHandler(SelectionInterface.class).selectClosestToPlayer(player.get())) {
            case 0 -> 1;
            case 1 -> {
                player.get().sendMessage(Text.of("§cNo suitable lectern found in front of you"));
                yield  2;
            }
            case 2 -> {
                player.get().sendMessage(Text.of("§cNo villager has been found"));
                yield 2;
            }
            default -> 0;
        };
    }
}
