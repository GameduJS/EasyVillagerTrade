package de.gamedude.easyvillagertrade.mixin;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.utils.TradingState;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientConnection.class)
public abstract class NetworkPacketMixin {

    private final EasyVillagerTradeBase modBase = EasyVillagerTrade.getModBase();

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void channelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if(packet instanceof EntityStatusS2CPacket statusPacket) {
            World world = MinecraftClient.getInstance().world;
            if(world == null)
                return;
            if(modBase.getState() != TradingState.WAIT_PROFESSION)
                return;
            if(!(statusPacket.getEntity(world) instanceof VillagerEntity villager))
                return;
            if(!villager.equals(modBase.getSelectionInterface().getVillager()))
                return;
            modBase.setState(TradingState.CHECK_OFFERS);
            modBase.handleInteractionWithVillager();
        } else if (packet instanceof SetTradeOffersS2CPacket setTradeOffers) {
            if (modBase.getState() != TradingState.CHECK_OFFERS)
                return;
            modBase.checkVillagerOffers(setTradeOffers.getOffers());
        } else if (packet instanceof OpenScreenS2CPacket screenPacket && screenPacket.getScreenHandlerType() == ScreenHandlerType.MERCHANT) {
            if (modBase.getState() != TradingState.CHECK_OFFERS)
                return;
            if(MinecraftClient.getInstance().getNetworkHandler() == null)
                return;
            MinecraftClient.getInstance().executeSync(() -> MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(screenPacket.getSyncId() + 1)));
            ci.cancel();
        }
    }
}
