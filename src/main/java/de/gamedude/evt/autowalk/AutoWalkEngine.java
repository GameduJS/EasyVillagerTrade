package de.gamedude.evt.autowalk;

import de.gamedude.evt.utils.ActionInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Supplier;

public class AutoWalkEngine extends AutomationEngine {

    private final Supplier<ClientPlayerEntity> player = () -> MinecraftClient.getInstance().player;

    @Override
    public void move() {
        Vec2f offset = getOffset();
        WalkAction walkAction = new WalkAction(new Vec3d(offset.x, player.get().getY(), offset.y), player.get());
        ((ActionInterface) player.get()).easyVillagerTrade$setWalkAction(walkAction);
    }

    @Override
    public void cancel() {
        ((ActionInterface) player.get()).easyVillagerTrade$setWalkAction(null);
    }

    @Override
    public void look(float yaw) {
        WalkAction walkAction = new WalkAction(yaw, player.get());
        ((ActionInterface) player.get()).easyVillagerTrade$setWalkAction(walkAction);
    }
    private void walkInternal() {
        WalkAction walkAction = ((ActionInterface) player.get()).easyVillagerTrade$getWalkaction();
        Vec3d walkVec = walkAction.getWalkVec();

        double length = walkVec.horizontalLengthSquared();
        if (Math.abs(length) >= 0.1 * 0.1 || length < 0) { // check for length < 0, when player was moving a little bit too much last tick -> will be correct during next repetitions
            // adjust mapping InitialLengthSquared -> 0 <---> 1 -> 0.3
            walkAction.getPlayer().forwardSpeed = MathHelper.clampedMap((float) length, 9, 0, 1, 0.3f); // 0.5f would be more realistic looking imo
        } else {
            walkAction.getPlayer().forwardSpeed = 0f;
            toggleEngine();
        }
    }

    private void lookInternal() {
        WalkAction walkAction = ((ActionInterface) player.get()).easyVillagerTrade$getWalkaction();
        Vec3d walkVec = walkAction.getWalkVec();

        if (walkVec == Vec3d.ZERO)
            return;
        PlayerEntity player = walkAction.getPlayer();

        float currentYaw = player.getYaw();
        float angleDiff = MathHelper.wrapDegrees(walkAction.getFinalYaw() - currentYaw);
        float rotationAmount = 4f * Math.min(1.0f, Math.abs(angleDiff) / 10f);
        float newYaw = MathHelper.wrapDegrees(currentYaw + Math.signum(angleDiff) * rotationAmount);

        player.setYaw(newYaw);

        if (Math.abs(angleDiff) <= 0.05) {
            player.setYaw(walkAction.getFinalYaw());
            toggleEngine();
        }
    }

    public void toggleEngine() {
        ((ActionInterface) player.get()).easyVillagerTrade$setWalkAction(null);
    }

    public boolean isToggled() {
        return ((ActionInterface) player.get()).easyVillagerTrade$getWalkaction() != null;
    }

}
