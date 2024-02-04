package de.gamedude.old.core.autowalk;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class WalkAction {

    private final Vec3d destination;
    private final ClientPlayerEntity player;
    private final float startYaw;
    private final float finalYaw;

    public WalkAction(Vec3d destination, ClientPlayerEntity player) {
        this.destination = destination;
        this.player = player;
        this.startYaw = player.getYaw();

        Vec3d walkVec = destination.subtract(player.getPos());
        this.finalYaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(walkVec.z, walkVec.x) * 57.2957763671875) - 90.0F);
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

    public Vec3d getDestination() {
        return destination;
    }
}
