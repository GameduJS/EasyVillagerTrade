package de.gamedude.easyvillagertrade.scripting.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;

import java.util.*;

public class InputHandler {

    private static final InputType[] INPUT_VALUES = InputType.values();
    private final Set<InputType> forcedInput;

    public InputHandler() {
        this.forcedInput = new HashSet<>();
    }

    public boolean isInputForcedDown(InputType inputType) {
        return forcedInput.contains(inputType);
    }

    public void forceInput(InputType inputType, boolean active) {
        if(active)
            this.forcedInput.add(inputType);
        else
            this.forcedInput.remove(inputType);
    }

    public boolean isScriptControlled() {
        return Arrays.stream(INPUT_VALUES).anyMatch(forcedInput::contains);
    }

    public void onTick(MinecraftClient minecraftClient) {
        if(minecraftClient.player == null)
            return;
        if(isScriptControlled() && !(minecraftClient.player.input instanceof ScriptMovementInput)) {
            minecraftClient.player.input = new ScriptMovementInput(this);
        } else if(!(minecraftClient.player.input instanceof KeyboardInput)) {
            minecraftClient.player.input = new KeyboardInput(minecraftClient.options);
        }
    }

}
