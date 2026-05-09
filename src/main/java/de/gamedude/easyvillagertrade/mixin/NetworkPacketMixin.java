package de.gamedude.easyvillagertrade.mixin;

import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.utils.TradingState;
import io.netty.channel.ChannelHandlerContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(Connection.class)
public abstract class NetworkPacketMixin {

    @Unique
    private final EasyVillagerTradeBase modBase = EasyVillagerTrade.getModBase();

    private final List<String> deny = List.of(
            "world", "chunk", "look", "input", "pos", "Rotate", "Motion", "tick", "rot", "entityevent", "time", "sound", "remove", "entitydata", "bundle"
    );

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void channelRead(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
        String packetName = packet.getClass().getSimpleName();

        if (deny.stream().map(String::toLowerCase).noneMatch(packetName.toLowerCase()::contains)) {
            System.out.println(packetName);
        }

        if ( packet instanceof ClientboundEntityEventPacket cEEp) {
            Level level = Minecraft.getInstance().level;
            if ( level == null )
                return;
            if(modBase.getState() != TradingState.WAIT_PROFESSION)
                return;
            if(!(cEEp.getEntity(level) instanceof Villager villager))
                return;
            if(!villager.equals(modBase.getSelectionInterface().getVillager()))
                return;
            modBase.setState(TradingState.CHECK_OFFERS);
            modBase.handleInteractionWithVillager();
        } else if ( packet instanceof ClientboundMerchantOffersPacket setTradeOffers) {
            if (modBase.getState() != TradingState.CHECK_OFFERS)
                return;
            modBase.checkVillagerOffers(setTradeOffers.getOffers());
        } else if ( packet instanceof ClientboundOpenScreenPacket screenPacket && screenPacket.getType() == MenuType.MERCHANT) {
            if (modBase.getState() != TradingState.CHECK_OFFERS)
                return;
            Minecraft.getInstance().execute(() ->
                    Minecraft.getInstance().getConnection().send(new ServerboundContainerClosePacket( screenPacket.getContainerId() )));
            ci.cancel();
        }

    }

}
