package de.gamedude.easyvillagertrade.mixin;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.TradeWorkflowHandler;
import de.gamedude.easyvillagertrade.core.autowalk.VillagerHubEngine;
import de.gamedude.easyvillagertrade.core.autowalk.WalkAction;
import de.gamedude.easyvillagertrade.utils.ActionInterface;
import net.minecraft.block.Blocks;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
