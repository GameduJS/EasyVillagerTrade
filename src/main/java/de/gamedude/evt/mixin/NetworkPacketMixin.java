package de.gamedude.evt.mixin;

import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.Script;
import io.netty.channel.ChannelHandlerContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientConnection.class)
public abstract class NetworkPacketMixin {

    @Unique
    private final TradeWorkflow tradeWorkFlowHandler = TradeWorkflow.INSTANCE;

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void channelRead0(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof SetTradeOffersS2CPacket setTradeOffers) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if ( player == null )
                return;
            if ( player.getWorld() == null )
                return;
            if ( !tradeWorkFlowHandler.isEnabled() )
                return;
            VillagerEntity selectingVillager = tradeWorkFlowHandler.getHandler(SelectionInterface.class).getVillager().get();
            if ( selectingVillager == null )
                return;
            selectingVillager.setOffers( setTradeOffers.getOffers() );
        } else if (packet instanceof OpenScreenS2CPacket openScreenS2CPacket) {
            if ( openScreenS2CPacket.getScreenHandlerType() != ScreenHandlerType.MERCHANT )
                return;
            if ( !tradeWorkFlowHandler.isEnabled() )
                return;
            if ( tradeWorkFlowHandler.getScriptPhase() == Script.ScriptPhase.FOUND )
                return;
            MinecraftClient.getInstance().execute( () -> MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(openScreenS2CPacket.getSyncId() + 1)));
            ci.cancel();
        }
    }
}
