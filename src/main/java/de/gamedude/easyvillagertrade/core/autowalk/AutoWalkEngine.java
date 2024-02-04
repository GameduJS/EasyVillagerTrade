package de.gamedude.easyvillagertrade.core.autowalk;

import de.gamedude.easyvillagertrade.utils.ActionInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class AutoWalkEngine {

    private WalkAction walkAction;
    private Vec3d walkVec = Vec3d.ZERO;
    private int state = 0;

    public void toggleAutoWalk(PlayerEntity player) {
        this.walkAction = ((ActionInterface) player).easyVillagerTrade$getWalkaction();
        if(walkAction == null)
            return;
        this.state = 0;
        updateVector();
    }

    public boolean isToggled() {
        return this.walkAction != null;
    }

    private void lerpYaw(float finalYaw) {
        if (walkVec == Vec3d.ZERO)
            return;
        PlayerEntity player = walkAction.getPlayer();

        float currentYaw = player.getYaw();
        float angleDiff = MathHelper.wrapDegrees(finalYaw - currentYaw);
        float rotationAmount = 4f * Math.min(1.0f, Math.abs(angleDiff) / 10f);
        float newYaw = MathHelper.wrapDegrees(currentYaw + Math.signum(angleDiff) * rotationAmount);

        player.setYaw(newYaw);

        if (Math.abs(angleDiff) <= 0.05) {
            player.setYaw(finalYaw);
            state += 1;
        }
    }

    private void walk() {
        updateVector();
        double length = walkVec.horizontalLengthSquared();
        if (Math.abs(length) >= 0.1 * 0.1 || length < 0) { // check for length < 0, when player was moving a little bit too much last tick -> will be correct during next repetitions
            // adjust mapping InitialLengthSquared -> 0 <---> 1 -> 0.3
            walkAction.getPlayer().forwardSpeed = MathHelper.clampedMap((float) length, 9, 0, 1, 0.3f); // 0.5f would be more realistic looking imo
        } else {
            walkAction.getPlayer().forwardSpeed = 0f;
            state +=1;
        }
    }

    protected abstract void onFinish(PlayerEntity player);

    public void tickPlayerMovement(PlayerEntity player) {
        switch (state) {
            case 0 -> lerpYaw(walkAction.getFinalYaw());
            case 1 -> walk();
            case 2 -> lerpYaw(walkAction.getStartYaw());
            case 3 -> {
                onFinish(player);
                toggleAutoWalk(player);
            }
        }
    }

    private void updateVector() {
        this.walkVec = walkAction.getDestination().subtract(walkAction.getPlayer().getPos());
    }

}
