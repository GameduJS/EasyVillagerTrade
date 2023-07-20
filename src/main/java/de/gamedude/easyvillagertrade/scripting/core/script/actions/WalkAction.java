package de.gamedude.easyvillagertrade.scripting.core.script.actions;

import de.gamedude.easyvillagertrade.scripting.core.script.actions.base.Action;
import de.gamedude.easyvillagertrade.scripting.movement.InputType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static java.lang.Math.*;

public class WalkAction extends Action {

    private final Direction direction;
    private double distance;
    private Vec3d oldPlayerPos = Vec3d.ZERO;

    private boolean isRotated;

    public WalkAction(String direction, String distanceString) {
        this.direction = getDirectionKeyBinding(direction);
        this.distance = this.parseNumberOrThrow(distanceString, () -> new IllegalArgumentException("'" + distanceString + "' is not an integer!")).doubleValue() + 0.1;
    }

    @Override
    public void performAction() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (!isRotated) {
            isRotated = true;
            player.setYaw(direction.asRotation());
            this.oldPlayerPos = player.getPos();
        }

        inputHandler.forceInput(InputType.FORWARD, true);

        finished = (oldPlayerPos.squaredDistanceTo(player.getPos()) >= (distance*distance - player.getMovementSpeed()));
    }

    private Direction getDirectionKeyBinding(String input) {
        Direction tempDirection;
        if ((tempDirection = Direction.byName(input)) == null)
            throw new IllegalArgumentException("'" + input + "' is not a valid direction");
        return tempDirection;
    }

    /**
     * <p> Based on measurements
     * <p>For future better approximations see velocity calculation from {@link net.minecraft.entity.LivingEntity#travel(Vec3d)}
     */
    public static final double BLOCKS_PER_TICK = 0.2158590684;
}
