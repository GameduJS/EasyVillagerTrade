package de.gamedude.evt.mixin;

import de.gamedude.evt.EasyVillagerTrade;
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
public class ClientPlayerMixin implements ActionInterface {

    @Unique
    public AutoAction autoAction;
    @Unique
    private final AutoWalkEngine autoWalkEngine = EasyVillagerTrade.getAutoWalkEngine();

    @Inject(method = "tickNewAi", at = @At("TAIL"))
    private void doTick(CallbackInfo ci) {
        autoWalkEngine.tickMovement();
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
