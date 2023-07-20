package de.gamedude.easyvillagertrade.scripting.movement;

import net.minecraft.client.input.Input;

public class ScriptMovementInput extends Input {

    private final InputHandler inputHandler;

    public ScriptMovementInput(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    private float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }

    @Override
    public void tick(boolean slowDown, float f) {
        this.movementSideways = 0.0F;
        this.movementForward = 0.0F;

        this.jumping = inputHandler.isInputForcedDown(InputType.JUMP); // oppa gangnam style
        this.pressingForward = inputHandler.isInputForcedDown(InputType.FORWARD);
        this.pressingBack = inputHandler.isInputForcedDown(InputType.BACKWARD);
        this.pressingLeft = inputHandler.isInputForcedDown(InputType.LEFT);
        this.pressingRight = inputHandler.isInputForcedDown(InputType.RIGHT);

        this.movementForward = getMovementMultiplier(this.pressingForward, this.pressingBack);
        this.movementSideways = getMovementMultiplier(this.pressingLeft, this.pressingRight);

        this.sneaking = inputHandler.isInputForcedDown(InputType.SNEAK);

        if (this.sneaking) {
            this.movementSideways *= 0.3D;
            this.movementForward *= 0.3D;
        }
    }
}
