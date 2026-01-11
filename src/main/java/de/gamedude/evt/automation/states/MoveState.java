package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.ParsingContext;
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

    // Not necessary: account for slipperiness of blocks / velocity factor - USER should be walking on "normal" blocks
    @Override
    public int run() {
        ClientPlayerEntity player = playerSupplier.get();
        Vec3d moveVec = destinationVec.subtract(player.getPos());
        double defaultVelocity = 4.317 / 20; // Default walking speed in meters/tick -> blocks/tick

        double length = moveVec.lengthSquared();
        if ( !isDone() ) {
            double velocity = MathHelper.clampedMap( (float) length, 1, 0, defaultVelocity,  defaultVelocity / 4); // Slow down when length^2 = 3^2 -> slow down the last three blocks
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
        this.destinationVec = playerSupplier.get().getPos().add( x, 0, z );
    }

    public static State parse(String[] args, ParsingContext ctx) throws Exception {
        if (args.length != 2) {
            throw new Exception("MOVE has arguments: dx (double), dz(double) (example: 2.0 1.4)");
        }
        try {
            double dx = Double.parseDouble(args[0]);
            double dz = Double.parseDouble(args[1]);
            return new MoveState(dx, dz);
        } catch (NumberFormatException e) {
            throw new Exception("dx, dy should be floating point number; Integer should be written as e.g. '2.0'");
        }
    }

    /*
     *  public void travel(Vec3d movementInput) {
*             // NORMALE BEWEGUNG (LAND)
*                 BlockPos groundPos = this.getVelocityAffectingPos();
*                 float slipperiness = this.getWorld().getBlockState(groundPos).getBlock().getSlipperiness();
*
*                 // Reibung auf dem Boden (Slipperiness) vs. Luftwiderstand (0.91)
*                 friction = this.isOnGround() ? slipperiness * 0.91F : 0.91F;
*
*                 Vec3d appliedInput = this.applyMovementInput(movementInput, slipperiness);
*                 double velocityY = appliedInput.y;
*
*                 // Levitation-Effekt
*                 if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
*                     velocityY += (0.05 * (double)(this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) - appliedInput.y) * 0.2;
*                 }
*                 // Fallschutz bei nicht geladenen Chunks
*                 else if (this.getWorld().isClient && !this.getWorld().isChunkLoaded(groundPos)) {
*                     velocityY = (this.getY() > (double)this.getWorld().getBottomY()) ? -0.1 : 0.0;
*                 }
*                 // Schwerkraft anwenden
*                 else if (!this.hasNoGravity()) {
*                     velocityY -= gravity;
*                 }
*
*                 // Finale Geschwindigkeitsberechnung (Horizontaler Drag + Vertikaler Drag 0.98)
*                 if (this.hasNoDrag()) {
*                     this.setVelocity(appliedInput.x, velocityY, appliedInput.z);
*                 } else {
*                     this.setVelocity(appliedInput.x * (double)friction, velocityY * 0.9800000190734863, appliedInput.z * (double)friction);
*                 }
*         }
     */

}
