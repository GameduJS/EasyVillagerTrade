package de.gamedude.easyvillagertrade.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.scoreboard.Scoreboard;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class DebugMixin {

    @Mixin(ChatHud.class)
    public static class MixinChatHud {
        @Inject(method = "logChatMessage", at = @At("HEAD"), cancellable = true)
        private void logChatMessage(ChatHudLine message, CallbackInfo ci) {
            ci.cancel();
        }
    }


    @Mixin(ClientPlayNetworkHandler.class)
    public static class MixinClientPlayNetworkHandler {
        @Redirect(
                method = "onPlayerList",
                at = @At(
                        value = "INVOKE",
                        target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"
                ),
                remap = false
        )
        private void onPlayerList(Logger instance, String s, Object o, Object o1) {
            // ignore 'Ignoring player info update for unknown player {} ({})'
        }
    }

    @Mixin(Scoreboard.class)
    public static class MixinScoreboard {
        @Redirect(
                method = "addTeam",
                at = @At(
                        value = "INVOKE",
                        target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"
                ),
                remap = false
        )
        private void onPlayerList(Logger instance, String s, Object o) {
            // ignore 'Requested creation of existing team'
        }
    }

}
