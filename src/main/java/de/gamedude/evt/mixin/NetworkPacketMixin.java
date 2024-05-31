package de.gamedude.evt.mixin;

import de.gamedude.evt.EasyVillagerTrade;
import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.old.core.TradeWorkflowHandler;
import de.gamedude.old.utils.TradingState;
import io.netty.channel.ChannelHandlerContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientConnection.class)
public abstract class NetworkPacketMixin {

    @Unique
    private final TradeWorkflow tradeWorkFlowHandler = EasyVillagerTrade.getTradeWorkflow();

    //@Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    /*private void channelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if(packet instanceof EntityStatusS2CPacket statusPacket) {
            World world = MinecraftClient.getInstance().world;
            if(world == null)
                return;
            if(tradeWorkFlowHandler.state != TradingState.WAIT_PROFESSION)
                return;
            if(!(statusPacket.getEntity(world) instanceof VillagerEntity villager))
                return;
            if(!villager.equals(tradeWorkFlowHandler.getHandler(SelectionInterface.class).getVillager()))
                return;
            tradeWorkFlowHandler.setState(TradingState.CHECK_OFFERS);
            tradeWorkFlowHandler.handleInteractionWithVillager();

        } else if (packet instanceof SetTradeOffersS2CPacket setTradeOffers) {
            if (tradeWorkFlowHandler.state != TradingState.CHECK_OFFERS)
                return;
            tradeWorkFlowHandler.checkVillagerOffers(setTradeOffers.getOffers());
        } else if (packet instanceof OpenScreenS2CPacket screenPacket && screenPacket.getScreenHandlerType() == ScreenHandlerType.MERCHANT) {
            if (tradeWorkFlowHandler.state != TradingState.CHECK_OFFERS)
                return;
            if(MinecraftClient.getInstance().getNetworkHandler() == null)
                return;
            MinecraftClient.getInstance().executeSync(() -> MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(screenPacket.getSyncId() + 1)));
            ci.cancel();
        }
    }*/

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void channelRead0(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if (!tradeWorkFlowHandler.isEnabled())
            return;
        if (packet instanceof SetTradeOffersS2CPacket setTradeOffers) {
            tradeWorkFlowHandler.getHandler(SelectionInterface.class).getVillager().setOffers(setTradeOffers.getOffers());
        }
    }
}
