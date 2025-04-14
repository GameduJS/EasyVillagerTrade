package de.gamedude.evt.mixin;

import de.gamedude.evt.autowalk.AutoAction;
import de.gamedude.evt.autowalk.AutoWalkEngine;
import de.gamedude.evt.utils.ActionInterface;
import net.minecraft.client.network.ClientPlayerEntity;
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
