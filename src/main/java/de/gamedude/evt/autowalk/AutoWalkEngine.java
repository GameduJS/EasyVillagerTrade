package de.gamedude.evt.autowalk;

import de.gamedude.evt.EasyVillagerTrade;
import de.gamedude.evt.utils.ActionInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.function.Supplier;

public class AutoWalkEngine {

    private final Supplier<ClientPlayerEntity> player = () -> MinecraftClient.getInstance().player;

    //TODO: LATER maybe add vertical movement
    public void move(Vec2f relativeDestination) {
        Vec3d toMoveVec = new Vec3d(relativeDestination.x, player.get().getY(), relativeDestination.y);
        WalkAction walkAction = new WalkAction(toMoveVec);
        ((ActionInterface) player.get()).easyVillagerTrade$setWalkAction(walkAction);
    }

    public void look(float yaw) {
        ViewAction viewAction = new ViewAction(0, yaw);
        ((ActionInterface) player.get()).easyVillagerTrade$setWalkAction(viewAction);
    }

    private void walkInternal(WalkAction walkAction) {
        Vec3d walkVec = walkAction.getWalkVec();

        double length = walkVec.horizontalLengthSquared();
        if (Math.abs(length) >= 0.1 * 0.1 || length < 0) { // check for length < 0, when player was moving a little bit too much last tick -> will be correct during next repetitions
            // adjust mapping InitialLengthSquared -> 0 <---> 1 -> 0.3
            walkAction.getPlayer().forwardSpeed = MathHelper.clampedMap((float) length, 9, 0, 1, 0.3f); // 0.5f would be more realistic looking imo
        } else {
            walkAction.getPlayer().forwardSpeed = 0f;
            finishAction();
        }
    }

    private void lookInternal(ViewAction viewAction) {
        System.out.println("[DEBUG] AutoWalkEngine.lookInternal: " + "hiha");
        PlayerEntity player = viewAction.getPlayer();

        float currentYaw = player.getYaw();
        float angleDiff = MathHelper.wrapDegrees(viewAction.finalYaw - currentYaw);
        float rotationAmount = 4f * Math.min(1.0f, Math.abs(angleDiff) / 10f);
        float newYaw = MathHelper.wrapDegrees(currentYaw + Math.signum(angleDiff) * rotationAmount);

        player.setYaw(newYaw);

        if (Math.abs(angleDiff) <= 0.05) {
            player.setYaw(viewAction.finalYaw);
            finishAction();
        }
    }

    public void tickMovement() {
        if(!EasyVillagerTrade.getTradeWorkflow().isEnabled())
            return;
        AutoAction autoAction = ((ActionInterface) player.get()).easyVillagerTrade$getWalkaction();
        if(autoAction == null)
            return;

        System.out.println("[DEBUG] AutoWalkEngine.tickMovement: " + "moving");

        if(autoAction instanceof ViewAction viewAction)
            lookInternal(viewAction);
        else if(autoAction instanceof WalkAction walkAction)
            walkInternal(walkAction);
    }

    private void finishAction() {
        ((ActionInterface) player.get()).easyVillagerTrade$setWalkAction(null);
    }

}
