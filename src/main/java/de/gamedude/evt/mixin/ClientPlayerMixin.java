package de.gamedude.evt.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.gamedude.evt.autowalk.AutoAction;
import de.gamedude.evt.autowalk.AutoWalkEngine;
import de.gamedude.evt.handler.Handler;
import de.gamedude.evt.utils.ActionInterface;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.SleepTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerMixin implements ActionInterface {


    @Inject(method = "init", at = @At("TAIL"))
    private void clinit(CallbackInfo ci) {
    }

    @Unique
    public AutoAction autoAction;
    @Unique
    private final AutoWalkEngine autoWalkEngine = AutoWalkEngine.INSTANCE;

    @Inject(method = "tickNewAi", at = @At("TAIL"))
    private void doTick(CallbackInfo ci) {
        autoWalkEngine.tickMovement();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
    }

    @Override
    public void easyVillagerTrade$setWalkAction(AutoAction walkAction) {
        this.autoAction = walkAction;
    }

    @Override
    public AutoAction easyVillagerTrade$getWalkaction() {
        return autoAction;
    }

}
