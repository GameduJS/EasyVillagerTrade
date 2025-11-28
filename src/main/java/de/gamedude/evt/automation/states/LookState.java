package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.TradeWorkflow;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class LookState extends State {

    private final float yaw;
    private float pitch;

    public LookState(float yaw, float pitch) {
        super(TradeWorkflow.INSTANCE);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public LookState(double dx, double dz, float pitch) {
        this(  MathHelper.wrapDegrees((float) (MathHelper.atan2(dz, dx) * 57.2957763671875) - 90.0F), pitch );
    }

    public LookState(double dx, double dz) {
        this(dx, dz, -999F);
    }

    @Override
    public int run() {
        ClientPlayerEntity player = playerSupplier.get();

        float yaw0 = player.getYaw();
        float pitch0 = player.getPitch();
        float delYaw = MathHelper.wrapDegrees(this.yaw - yaw0);
        float delPitch = MathHelper.wrapDegrees(this.pitch - pitch0);
        float  gradientYaw = 4f * Math.min(1.0f, Math.abs(delYaw) / 10f);
        float  gradientPitch = 4f * Math.min(1.0f, Math.abs(delPitch) / 10f);

        player.setYaw( MathHelper.wrapDegrees(yaw0 + Math.signum(delYaw) * gradientYaw) ) ;
        player.prevYaw = yaw0;
        player.setPitch( MathHelper.wrapDegrees(pitch0 + Math.signum(delPitch) * gradientPitch) ) ;
        player.prevPitch = pitch0;

        // Precision; not visible by human eye could be for cheat checks
        if ( Math.abs(gradientYaw) <= 0.05 )
            player.setYaw( yaw );
        if ( Math.abs(gradientPitch) <= 0.05 )
            player.setPitch( pitch );

        return 0;
    }

    @Override
    public boolean isDone() {
        ClientPlayerEntity player = playerSupplier.get();
        return player.getYaw() == yaw && player.getPitch() == pitch;
    }

    @Override
    public void initState() {
        System.out.println("[DEBUG] LookState.initState");
        if ( this.pitch == -999F ) {
            this.pitch = playerSupplier.get().getPitch();
        }
    }
}
