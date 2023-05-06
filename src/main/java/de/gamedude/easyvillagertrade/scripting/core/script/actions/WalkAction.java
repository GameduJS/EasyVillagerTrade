package de.gamedude.easyvillagertrade.scripting.core.script.actions;

import de.gamedude.easyvillagertrade.scripting.core.script.actions.base.Action;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static java.lang.Math.*;

public class WalkAction extends Action {

    private final KeyBinding directionKeyBinding;
    private double distance;
    private BlockPos destination;


    public WalkAction(String direction, String distanceString) {
        this.directionKeyBinding = getDirectionKeyBinding(direction, MinecraftClient.getInstance().options);
        this.distance = this.parseNumberOrThrow(distanceString, () -> new IllegalArgumentException("'" + distanceString + "' is not an integer!")).doubleValue() + 0.1;
    }

    @Override
    public void performAction() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (destination == null) {
            float yaw = player.getYaw();
            this.destination = player.getBlockPos();
            destination = destination.add(sin(toRadians(yaw)) * distance, 0, cos(toRadians(yaw)) * distance); // RIGHT, LEFT
            destination = destination.add(cos(toRadians(yaw)) * distance, 0, sin(toRadians(yaw)) * distance); // FORWARD, BACKWARD
        }

        /*directionKeyBinding.setPressed(true);
        blocks -= BLOCKS_PER_TICK;

        finished = blocks < 0;
        if(finished)
            directionKeyBinding.setPressed(false);*/

        double dx = destination.getX() - player.getX();
        double dz = destination.getZ() - player.getZ();

        if (dx * dx + dz * dz < 0.05) {
            finished = true;
            return;
        }

        PlayerMoveC2SPacket.PositionAndOnGround packet = new PlayerMoveC2SPacket.PositionAndOnGround(0, 0, 0, true);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }

    @Override
    public void reset() {
        this.finished = false;
    }

    private KeyBinding getDirectionKeyBinding(String input, GameOptions gameOptions) {
        return switch (input) {
            case "LEFT":
                yield gameOptions.leftKey;
            case "RIGHT":
                yield gameOptions.rightKey;
            case "FORWARD":
                yield gameOptions.forwardKey;
            case "BACKWARD":
                yield gameOptions.backKey;
            default:
                throw new IllegalArgumentException("No such direction as " + input);
        };
    }

    /**
     * <p> Based on measurements
     * <p>For future better approximations see velocity calculation from {@link net.minecraft.entity.LivingEntity#travel(Vec3d)}
     */
    public static final double BLOCKS_PER_TICK = 0.2158590684;
}
