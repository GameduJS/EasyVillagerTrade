package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.ParsingContext;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BreakState extends State {

    private final SelectionInterface selectionInterface;

    public BreakState() {
        super(TradeWorkflow.INSTANCE);
        this.selectionInterface = getTradeWorkflow().getHandler(SelectionInterface.class);
    }

    @Override
    public int run() {
        PlayerEntity player = playerSupplier.get();
        ItemStack axe = player.getMainHandStack();
        BlockPos lecternPos = this.selectionInterface.getLecternPos().get();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if ( lecternPos == null ) {
            System.out.println("[DEBUG] BREAK - LecternPos is null");
            return 1;
        }

        if ( !axe.isIn(ItemTags.AXES) ) {
            System.out.println("[DEBUG] BREAK - Item is not axe");
            return 1;
        }

        if ( axe.getDamage() >= axe.getMaxDamage() - 10) {
            System.out.println("[DEBUG] BREAK - Axe too low");
            return 1;
        }

        if ( player.getWorld().getBlockState(lecternPos).getBlock() == Blocks.LECTERN ) {
            interactionManager.updateBlockBreakingProgress(lecternPos, Direction.UP);
            player.swingHand(Hand.MAIN_HAND, false);
        }

        return 0;
    }

    @Override
    public boolean isDone()  {
        BlockPos lecternPos = selectionInterface.getLecternPos().get();

        if(lecternPos == null) {
            System.out.println("[DEBUG] BREAK - LecternPos is null");
            return false;
        }

        return playerSupplier.get()
                .getWorld()
                .getBlockState(lecternPos)
                .isOf(Blocks.AIR);
    }

    @Override
    public void initState() { }

    public static State parse(String[] args, ParsingContext ctx) throws Exception {
        return new BreakState();
    }
}
