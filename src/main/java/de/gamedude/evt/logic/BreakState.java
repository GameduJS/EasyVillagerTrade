package de.gamedude.evt.logic;

import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BreakState extends State{

    private final BlockPos lecternPos;

    public BreakState(TradeWorkflow tradeWorkflow) {
        super(tradeWorkflow);
        this.lecternPos = tradeWorkflow.getHandler(SelectionInterface.class).getLecternPos();
    }

    @Override
    public int run() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        World world = player.getWorld();

        if(lecternPos == null)
            return 2;

        if(world.getBlockState(lecternPos).getBlock() == Blocks.LECTERN) {
            MinecraftClient.getInstance().interactionManager.updateBlockBreakingProgress(lecternPos, Direction.UP);
            player.swingHand(Hand.MAIN_HAND, false);
            player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            return 0;
        } else {
            return 1;
        }
    }
}
