package de.gamedude.easyvillagertrade;

import de.gamedude.easyvillagertrade.commands.EasyVillagerTradeCommand;
import de.gamedude.easyvillagertrade.config.Config;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.screen.TradeSelectScreen;
import de.gamedude.easyvillagertrade.utils.TradingState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class EasyVillagerTrade implements ModInitializer {

    public static final Config CONFIG = new Config("easyvillagertrade");
    private static EasyVillagerTradeBase modBase;
    private final KeyBinding keyBinding = new KeyBinding("key.custom.openscreen", GLFW.GLFW_KEY_F6, "EasyVillagerTrade");

    @Override
    public void onInitialize() {
        modBase = new EasyVillagerTradeBase();
        ClientCommandRegistrationCallback.EVENT.register(new EasyVillagerTradeCommand(modBase));
        registerCallbacks();

        KeyBindingHelper.registerKeyBinding(keyBinding);
    }

    public void registerCallbacks() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (hand == Hand.OFF_HAND || hitResult == null || !world.isClient())
                return ActionResult.PASS;
            if (hitResult.getEntity() instanceof VillagerEntity villager && modBase.getState() == TradingState.MODE_SELECTION) {
                modBase.getSelectionInterface().setVillager(villager);
                player.sendMessage(Text.translatable("evt.command.selected.villager"), false);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (hand == Hand.OFF_HAND || hitResult == null || !world.isClient)
                return ActionResult.PASS;
            BlockPos blockPos = hitResult.getBlockPos();
            if (world.getBlockState(blockPos).getBlock() == Blocks.LECTERN && modBase.getState() == TradingState.MODE_SELECTION) {
                modBase.getSelectionInterface().setLecternPos(blockPos);
                player.sendMessage(Text.translatable("evt.command.selected.lectern"), false);
            }
            return ActionResult.PASS;
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> modBase.handle());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(keyBinding.wasPressed())
                client.setScreen(new TradeSelectScreen());
        });
    }

    public static EasyVillagerTradeBase getModBase() {
        return modBase;
    }
}
