package de.gamedude.evt.automation;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.Vec2f;

public class AutomationInput extends Input {

    public AutomationInput() {
        // somehow access "Statemanager"
    }

    public void tick(boolean slowDown, float slowDownFactor) {
        // this.pressingForward = statemanager.shouldMoveFwd();
        //this.pressingBack = statemanager.shouldMoveBwd();
        //this.pressingLeft = this.settings.leftKey.isPressed();
       // this.pressingRight = this.settings.rightKey.isPressed();
        //this.movementForward = getMovementMultiplier(this.pressingForward, this.pressingBack);
        //this.movementSideways = getMovementMultiplier(this.pressingLeft, this.pressingRight);
        //this.jumping = this.settings.jumpKey.isPressed();
       // this.sneaking = this.settings.sneakKey.isPressed();
        if (slowDown) {
            this.movementSideways *= slowDownFactor;
            this.movementForward *= slowDownFactor;
        }

    }
}
