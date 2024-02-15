package de.gamedude.evt.mixin;

import de.gamedude.evt.EasyVillagerTrade;
import de.gamedude.evt.autowalk.AutoWalkEngine;
import de.gamedude.evt.autowalk.WalkAction;
import de.gamedude.evt.utils.ActionInterface;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerMixin implements ActionInterface {

    @Unique
    public WalkAction walkAction;
    @Unique
    private final AutoWalkEngine autoWalkEngine = EasyVillagerTrade.getAutoWalkEngine();

    @Inject(method = "tickNewAi", at = @At("TAIL"))
    private void doTick(CallbackInfo ci) {
        if(!autoWalkEngine.isToggled())
            return;
        autoWalkEngine.tickMovement();
    }

    @Override
    public void easyVillagerTrade$setWalkAction(WalkAction walkAction) {
        this.walkAction = walkAction;
        autoWalkEngine.toggleEngine();
    }

    @Override
    public WalkAction easyVillagerTrade$getWalkaction() {
        return walkAction;
    }
}
