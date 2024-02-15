package de.gamedude.evt.autowalk;

import net.minecraft.util.math.Vec2f;

public abstract class AutomationEngine {

    private Vec2f offset;
    protected int count;

    public int getRepetitionCount() {
        return this.count;
    }

    public abstract void move();

    public abstract void cancel();

    public abstract void look(float yaw);

    public void setOffset(float dx, float dz) {
        this.offset = new Vec2f(dx, dz);
    }

    public Vec2f getOffset() {
        return offset;
    }
}
