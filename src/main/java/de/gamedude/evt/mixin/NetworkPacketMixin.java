package de.gamedude.evt.mixin;

import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import io.netty.channel.ChannelHandlerContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
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
            VillagerEntity selectingVillager = tradeWorkFlowHandler.getHandler(SelectionInterface.class).getVillager().get();
            if ( selectingVillager == null )
                return;
            selectingVillager.setOffers( setTradeOffers.getOffers() );
            System.out.println("Setting villager offer!");
        } else if (packet instanceof OpenScreenS2CPacket openScreenS2CPacket) {
        }
    }
}
