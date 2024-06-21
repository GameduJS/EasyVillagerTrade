package de.gamedude.evt.logic;

import de.gamedude.evt.autowalk.WalkAction;
import de.gamedude.evt.utils.ActionInterface;
import net.minecraft.util.math.Vec3d;

public class WalkState  extends State{

    private final int dx, dz;
    public WalkState(int dx, int dz) {
        this.dx = dx;
        this.dz = dz;
    }

    @Override
    public void initState() {
        ((ActionInterface) player.get()).easyVillagerTrade$setWalkAction(new WalkAction(new Vec3d(dx, 0, dz)));
        super.initState();
    }

    @Override
    public int run() {
        return ((ActionInterface) player.get()).easyVillagerTrade$getWalkaction() == null ? 1 : 0;
    }
}
