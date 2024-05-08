package de.gamedude.evt.logic;

import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PlaceState extends State{

    private final BlockPos lecternPos;

    public PlaceState(TradeWorkflow tradeWorkflow) {
        super(tradeWorkflow);
        this.lecternPos = tradeWorkflow.getHandler(SelectionInterface.class).getLecternPos();
    }

    @Override
    public int run() {
        ClientPlayerEntity player = minecraftClient.player;

        if(player.getOffHandStack().equals(ItemStack.EMPTY)) {
            player.sendMessage(Text.translatable("evt.logic.lectern_non"));
            return 2;
        }

        // Place block
        BlockHitResult hitResult = new BlockHitResult(new Vec3d(lecternPos.getX(), lecternPos.getY(), lecternPos.getZ()), Direction.UP, lecternPos, false);
        minecraftClient.interactionManager.interactBlock(player, Hand.OFF_HAND, hitResult);
        minecraftClient.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.OFF_HAND));

        return 1;
    }
}
