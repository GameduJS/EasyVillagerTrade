package de.gamedude.evt.script;

import de.gamedude.evt.EasyVillagerTrade;
import de.gamedude.evt.autowalk.AutomationEngine;

public abstract class Script {


    private final AutomationEngine automationEngine = EasyVillagerTrade.getAutoWalkEngine();

    /**
     * Returns the counts of cycles done
     */
    protected int count = automationEngine.getRepetitionCount(); // TODO: AutoWalkEngine#count

    /**
     * Executes the command to move
     * @param dx: Distance walked in x-Direction
     * @param dz: Distance walked in z-Direction
     */
    protected final void move(int dx, int dz) {
        automationEngine.setOffset(dx, dz);
        automationEngine.move();
        // TODO: AutoWalkEngine
    }


    /**
     * If executed the script will turn itself off
     */
    protected final void cancel() {
        automationEngine.cancel();
    }


    /**
     * Executes the command to look around given a yaw
     * @param yaw: Destination yaw where the player should look
     * @param variance: Whether there should be a tiny variance in the yaw
     */
    protected final void look(float yaw, boolean variance) {
        if(variance) {
            yaw+= ((float) Math.random() - 0.5f) * 2;
        }
      automationEngine.look(yaw);
    }

    /**
     * Executed whenever the script gets initialized
     */
    public abstract void init();

    /**
     * Executed whenever a cycle is finished
     */
    public abstract void tick();


}
