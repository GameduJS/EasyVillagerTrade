package de.gamedude.evt.logic;

import de.gamedude.evt.handler.SelectionInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.function.Supplier;

public class PlaceState extends State{

    private final Supplier<BlockPos> blockPosSupplier;

    public PlaceState() {
        this.blockPosSupplier = tradeWorkflow.getHandler(SelectionInterface.class).getLecternPos();
    }

    public PlaceState(BlockPos blockPos) {
        this.blockPosSupplier = () -> blockPos;
    }

    @Override
    public int run() {

        if(player.get().getOffHandStack().equals(ItemStack.EMPTY)) {
            player.get().sendMessage(Text.translatable("evt.logic.lectern_non"));
            return 2;
        }

        BlockPos lecternPos = blockPosSupplier.get();
        // Place block
        BlockHitResult hitResult = new BlockHitResult(lecternPos.toCenterPos(), Direction.UP, lecternPos, false);
        client.interactionManager.interactBlock(player.get(), Hand.OFF_HAND, hitResult);
        client.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.OFF_HAND));
        return 1;
    }
}
