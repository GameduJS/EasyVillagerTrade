package de.gamedude.old.mixin;

import de.gamedude.old.EasyVillagerTrade;
import de.gamedude.old.core.autowalk.VillagerHubEngine;
import de.gamedude.old.core.autowalk.WalkAction;
import de.gamedude.old.utils.ActionInterface;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
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
    private final VillagerHubEngine villagerHubEngine = EasyVillagerTrade.getTradeWorkFlowHandler().getHandler(VillagerHubEngine.class);

    @Inject(method = "tickNewAi", at = @At("TAIL"))
    private void doTick(CallbackInfo ci) {
        if(!villagerHubEngine.isToggled())
            return;
        villagerHubEngine.tickPlayerMovement((PlayerEntity) (Object) this);
    }

    @Override
    public void easyVillagerTrade$setWalkAction(WalkAction walkAction) {
        this.walkAction = walkAction;
        villagerHubEngine.toggleAutoWalk((PlayerEntity) (Object) this);
    }

    @Override
    public WalkAction easyVillagerTrade$getWalkaction() {
        return walkAction;
    }
}
