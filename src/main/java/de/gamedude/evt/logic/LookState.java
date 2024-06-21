package de.gamedude.evt.logic;

import de.gamedude.evt.autowalk.ViewAction;
import de.gamedude.evt.utils.ActionInterface;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Supplier;

public class LookState  extends State {

    private final Supplier<Float> yaw, pitch;
    public LookState(boolean toFormerRotation) {
        this.yaw = () -> 0f;
        this.pitch = () ->0f;
        throw new NotImplementedException();
    }

    public LookState(float yaw, float pitch) {
        this.yaw = () -> yaw;
        this.pitch = () -> pitch;
    }

    @Override
    public void initState() {
        ((ActionInterface) player.get()).easyVillagerTrade$setWalkAction(new ViewAction(pitch.get(), yaw.get()));
        super.initState();
    }

    @Override
    public int run() {
        return ((ActionInterface) player.get()).easyVillagerTrade$getWalkaction() == null ? 1 : 0;
    }
}
