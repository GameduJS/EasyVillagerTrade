package de.gamedude.evt.autowalk;

public class ViewAction extends AutoAction {

    public final float startYaw;
    public final float finalYaw;
    public final float startPitch;
    public final float finalPitch;

    public ViewAction(float pitch, float yaw) {
        this.startPitch = player().get().getPitch();
        this.finalPitch = pitch;
        this.startYaw = player().get().getYaw();
        this.finalYaw = yaw;
        // this.finalYaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(walkVec.z, walkVec.x) * 57.2957763671875) - 90.0F);
    }

}
