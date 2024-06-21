package de.gamedude.evt.mixin;

import de.gamedude.evt.EasyVillagerTrade;
import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.logic.BuyState;
import de.gamedude.evt.logic.CheckTradeState;
import de.gamedude.evt.logic.InteractState;
import de.gamedude.evt.script.ScriptManager;
import de.gamedude.old.core.TradeWorkflowHandler;
import de.gamedude.old.utils.TradingState;
import io.netty.channel.ChannelHandlerContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
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
    private final TradeWorkflow tradeWorkFlowHandler = TradeWorkflow.INSTANCE;

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void channelRead0(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if (!tradeWorkFlowHandler.isEnabled())
            return;
        if (packet instanceof SetTradeOffersS2CPacket setTradeOffers) {
            if(!TradeWorkflow.INSTANCE.isEnabled())
                return;
            VillagerEntity villager = tradeWorkFlowHandler.getHandler(SelectionInterface.class).getVillager().get();
            if(villager == null)
                return;
            villager.setOffers(setTradeOffers.getOffers());
        } else if (packet instanceof OpenScreenS2CPacket openScreenS2CPacket) {
            if(!TradeWorkflow.INSTANCE.isEnabled())
                return;
            if(MinecraftClient.getInstance().getNetworkHandler() == null)
                return;
            if(openScreenS2CPacket.getScreenHandlerType() != ScreenHandlerType.MERCHANT)
                return;
            if(!(TradeWorkflow.INSTANCE.getHandler(ScriptManager.class).getScript().getCurrentState() instanceof BuyState)) {
                MinecraftClient.getInstance().executeSync(() -> MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(openScreenS2CPacket.getSyncId() + 1)));
                ci.cancel();
            }
        }
    }
}
