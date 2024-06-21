package de.gamedude.evt.autowalk;

import net.minecraft.util.math.Vec3d;

public class WalkAction extends AutoAction {

    private final Vec3d destination;
    private final Vec3d startPosition;

    public WalkAction(Vec3d offset) {
        this.startPosition = player().get().getPos();
        this.destination = startPosition.add(offset);
    }

    public Vec3d getWalkVec() {
        return this.destination.subtract(player().get().getPos());
    }
}
