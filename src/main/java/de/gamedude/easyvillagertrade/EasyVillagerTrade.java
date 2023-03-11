package de.gamedude.easyvillagertrade;

import de.gamedude.easyvillagertrade.commands.CommandRegister;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.utils.TradingState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class EasyVillagerTrade implements ModInitializer {

    private static EasyVillagerTradeBase modBase;
    public static Logger LOGGER = LoggerFactory.getLogger("evt");
    @Override
    public void onInitialize() {

        modBase = new EasyVillagerTradeBase();
        CommandRegister commandRegister = new CommandRegister(modBase);
        commandRegister.init();


        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (hand == Hand.OFF_HAND || hitResult == null || !world.isClient())
                return ActionResult.PASS;
            if (hitResult.getEntity() instanceof VillagerEntity villager && modBase.getState() == TradingState.MODE_SELECTION) {
                modBase.getSelectionInterface().setVillager(villager);
                player.sendMessage(Text.of("§8| §7Selected villager"));
                return ActionResult.FAIL;
            }
            // cannot read offers here
            // get offers with mixins
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (hand == Hand.OFF_HAND || hitResult == null || !world.isClient)
                return ActionResult.PASS;
            BlockPos blockPos = hitResult.getBlockPos();
            if (world.getBlockState(blockPos).getBlock() == Blocks.LECTERN && modBase.getState() == TradingState.MODE_SELECTION) {
                modBase.getSelectionInterface().setLecternPos(blockPos);
                player.sendMessage(Text.of("§8| §7Selected lectern"));
            }
            return ActionResult.PASS;
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> modBase.handle());
    }

    public static EasyVillagerTradeBase getModBase() {
        return modBase;
    }
}
