package de.gamedude.easyvillagertrade.mixin;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import de.gamedude.easyvillagertrade.scripting.movement.InputHandler;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    private final MinecraftClient minecraftClient = (MinecraftClient) (Object) this;

    /**
     * Let {@link InputHandler#onTick(MinecraftClient)} tick
     */
    @Inject(
            method = "tick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 4,
                    shift = At.Shift.BY, by = -3)
    )
    public void onTick(CallbackInfo callbackInfo) {
        EasyVillagerTradeBase base = EasyVillagerTrade.getModBase();
        if (base.isActive())
            base.getScriptCache().getInputHandler().onTick(minecraftClient);
    }
}
