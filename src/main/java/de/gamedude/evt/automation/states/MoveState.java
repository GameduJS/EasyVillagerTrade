package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.TradeWorkflow;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MoveState extends State {

    private final double x,z;
    private Vec3d destinationVec;

    public MoveState(double x, double z) {
        super(TradeWorkflow.INSTANCE);
        this.x = x;
        this.z= z;
    }

    // TODO: This is a point of proof, increase precision;
    // Not necessary: account for slipperiness of blocks / velocity factor - USER should be walking on "normal" blocks
    @Override
    public int run() {
        ClientPlayerEntity player = playerSupplier.get();
        Vec3d moveVec = destinationVec.subtract(player.getPos());
        double defaultVelocity = 4.317 / 20; // Default sprinting speed in meters/tick = blocks/tick

        double length = moveVec.lengthSquared();
        if ( !isDone() ) {
            double velocity = MathHelper.clampedMap( (float) length, 3*3, 0, defaultVelocity, 0.1f ); // Slow down when length^2 = 3^2 -> slow down the last three blocks
            player.setVelocity( moveVec.normalize().multiply( velocity ) ); // At the end of the tick / beginning of the new tick velocity is overwritten to 0 (if no input is given)
        }
        return 0;
    }

    @Override
    public boolean isDone() {
        ClientPlayerEntity player = playerSupplier.get();
        double length = destinationVec.subtract( player.getPos() ).lengthSquared();
        return length <= 0.1*0.1; // Decrease value for more precision, be careful to not set it too low since walking isn't continuous
    }

    @Override
    public void initState() {
        System.out.println("[DEBUG] MoveState.initState");
        this.destinationVec = playerSupplier.get().getPos().add( x, 0, z );
    }
}
