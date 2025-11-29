package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PlaceState extends State {

    private final SelectionInterface selectionInterface;

    public PlaceState() {
        super(TradeWorkflow.INSTANCE);
        this.selectionInterface = getTradeWorkflow().getHandler(SelectionInterface.class);
    }

    @Override
    public int run() {
        ClientPlayerEntity player = playerSupplier.get();
        BlockPos targetPos = selectionInterface.getLecternPos().get();

        if ( targetPos == null ) {
            player.sendMessage(Text.of("TODO: PlaceState failed - missing target position"));
            return 1;
        }

        if ( player.getWorld().getBlockState(targetPos).getBlock() != Blocks.AIR ) {
            player.sendMessage(Text.of("TODO: PlaceState failed - block occupied"));
            return 1;
        }

        if ( player.getOffHandStack().getItem() != Items.LECTERN ) {
            player.sendMessage(Text.of("TODO: PlaceState failed - no lecterns"));
            return 1;
        }

        BlockHitResult hitResult = new BlockHitResult(
                targetPos.toCenterPos(), Direction.UP, targetPos, false
        );
        client.interactionManager.interactBlock(player, Hand.OFF_HAND, hitResult);
        player.swingHand(Hand.OFF_HAND);
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
