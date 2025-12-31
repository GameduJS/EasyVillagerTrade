package de.gamedude.evt.automation.states;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.ParsingContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;

/**
 *
 * Represents the "LOOK" command. <br>
 * Usage: LOOK [VIEW/POS]  [ yaw pitch / dx dy (pitch) ] <br>
 * Smoothly changes the player yaw and pitch based on the given arguments. <br>
 *
 * <h>TODO: smooth linear (or smth else) transition; Current implementation:  Independently moving along each axis </h>
 *
 *
 */
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
        if ( this.pitch == -999F ) {
            this.pitch = playerSupplier.get().getPitch();
        }
    }

    public static State parse(String[] args, ParsingContext ctx) throws Exception {
        if (args.length < 1) {
            throw new Exception("LOOK needs arguments: <yaw> <pitch> or ~");
        }

        if (args[0].equals("~")) {
            String nextLine = ctx.next();
            if (!nextLine.toUpperCase().startsWith("WALK")) {
                throw new Exception("LOOK ~ only works if there is a WALK <> following");
            }


            String[] parts = nextLine.split(" ");
            try {
                double dx = Double.parseDouble(parts[1]);
                double dz = Double.parseDouble(parts[2]);
                float pitch = -999;

                if ( parts.length > 3 )
                    pitch =  Float.parseFloat(parts[3]);

                return new LookState( dx, dz, pitch );

            } catch (Exception e) {
                throw new Exception("Could not parse values of WALK command in next line");
            }
        }

        // Fall 2: Normale Koordinaten
        if (args.length != 2) {
            throw new Exception("LOOK needs yaw and pitch.");
        }

        try {
            float y = Float.parseFloat(args[0]);
            float p = Float.parseFloat(args[1]);
            return new LookState(y, p);
        } catch (NumberFormatException e) {
            throw new Exception("Yaw/Pitch have to be floating point numbers");
        }
    }
}
