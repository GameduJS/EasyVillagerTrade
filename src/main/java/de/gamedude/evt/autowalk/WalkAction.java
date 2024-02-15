package de.gamedude.evt.autowalk;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class WalkAction {

    private final Vec3d destination;
    private final ClientPlayerEntity player;
    private final Vec3d startPosition;
    private final float startYaw;
    private final float finalYaw;

    public WalkAction(Vec3d offset, ClientPlayerEntity player) {
        this.player = player;
        this.startPosition = player.getPos();
        this.destination = startPosition.add(offset);
        this.startYaw = player.getYaw();

        Vec3d walkVec = getWalkVec();
        this.finalYaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(walkVec.z, walkVec.x) * 57.2957763671875) - 90.0F);
    }

    public WalkAction(float finalYaw, ClientPlayerEntity player) {
        this.player = player;
        this.startPosition = player.getPos();
        this.destination = startPosition;
        this.startYaw = finalYaw;
        this.finalYaw = finalYaw;
    }

    public float getStartYaw() {
        return startYaw;
    }

    public float getFinalYaw() {
        return finalYaw;
    }

    public ClientPlayerEntity getPlayer() {
        return player;
    }

    public Vec3d getWalkVec() {
        return this.destination.subtract(player.getPos());
    }
}
