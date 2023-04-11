package de.gamedude.easyvillagertrade.scripting.core.script.actions;

import de.gamedude.easyvillagertrade.scripting.core.script.actions.base.Action;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.Vec3d;

public class WalkAction extends Action {

    private final KeyBinding directionKeyBinding;
    private final double blocksFinal;
    private double blocks;

    public WalkAction(String direction, String blocks) {
        this.directionKeyBinding = getDirectionKeyBinding(direction, MinecraftClient.getInstance().options);
        this.blocks = this.parseNumberOrThrow(blocks, () -> new IllegalArgumentException("'" + blocks + "' is not an integer!")).doubleValue() + 0.1;
        this.blocksFinal = this.blocks;
    }

    @Override
    public void performAction() {
        directionKeyBinding.setPressed(true);
        blocks -= BLOCKS_PER_TICK;

        finished = blocks < 0;
        if(finished)
            directionKeyBinding.setPressed(false);
    }

    @Override
    public void reset() {
        this.blocks = blocksFinal;
        this.finished = false;
    }

    private KeyBinding getDirectionKeyBinding(String input, GameOptions gameOptions) {
        return switch (input) {
            case "LEFT": yield gameOptions.leftKey;
            case "RIGHT": yield gameOptions.rightKey;
            case "FORWARD": yield gameOptions.forwardKey;
            case "BACKWARD": yield gameOptions.backKey;
            default: throw new IllegalArgumentException("No such direction as " + input);
        };
    }

    /**
     * <p> Based on measurements
     * <p>For future better approximations see velocity calculation from {@link net.minecraft.entity.LivingEntity#travel(Vec3d)}
     */
    public static final double BLOCKS_PER_TICK = 0.2158590684;
}
