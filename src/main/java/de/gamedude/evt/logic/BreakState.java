package de.gamedude.evt.logic;

import de.gamedude.evt.handler.SelectionInterface;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class BreakState extends State{

    private final Supplier<BlockPos> lecternPos;

    public BreakState() {
        this.lecternPos = tradeWorkflow.getHandler(SelectionInterface.class).getLecternPos();
    }

    public BreakState(BlockPos lecternPos) {
        this.lecternPos = () -> lecternPos;
    }

    @Override
    public int run() {
        ClientPlayerEntity playerEntity = player.get();
        World world = playerEntity.getWorld();

        if(lecternPos.get() == null)
            return 2;

        if(world.getBlockState(lecternPos.get()).getBlock() == Blocks.LECTERN) {
            client.interactionManager.updateBlockBreakingProgress(lecternPos.get(), Direction.UP);
            playerEntity.swingHand(Hand.MAIN_HAND, false);
            playerEntity.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            return 0;
        } else {
            return 1;
        }
    }
}
