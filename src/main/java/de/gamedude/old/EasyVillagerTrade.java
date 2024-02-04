package de.gamedude.old;

import de.gamedude.old.commands.EasyVillagerTradeCommand;
import de.gamedude.old.core.*;
import de.gamedude.old.screen.TradeSelectScreen;
import de.gamedude.old.utils.TradingState;
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

    private final KeyBinding keyBinding = new KeyBinding("key.custom.openscreen", GLFW.GLFW_KEY_F6, "evt.custom.title");
    private static TradeWorkflowHandler tradeWorkflowHandler;

    @Override
    public void onInitialize() {
        tradeWorkflowHandler = new TradeWorkflowHandler();

        ClientCommandRegistrationCallback.EVENT.register(new EasyVillagerTradeCommand(tradeWorkflowHandler));
        KeyBindingHelper.registerKeyBinding(keyBinding);
        registerCallbacks();
    }


    public void registerCallbacks() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (hand == Hand.OFF_HAND || hitResult == null || !world.isClient())
                return ActionResult.PASS;
            if (hitResult.getEntity() instanceof VillagerEntity villager && tradeWorkflowHandler.state == TradingState.MODE_SELECTION) {
                tradeWorkflowHandler.getHandler(SelectionInterface.class).setVillager(villager);
                player.sendMessage(Text.of("§8| §7Selected villager"));
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (hand == Hand.OFF_HAND || hitResult == null || !world.isClient)
                return ActionResult.PASS;
            BlockPos blockPos = hitResult.getBlockPos();
            if (world.getBlockState(blockPos).getBlock() == Blocks.LECTERN && tradeWorkflowHandler.state == TradingState.MODE_SELECTION) {
                tradeWorkflowHandler.getHandler(SelectionInterface.class).setLecternPos(blockPos);
                player.sendMessage(Text.of("§8| §7Selected lectern"));
            }
            return ActionResult.PASS;
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> tradeWorkflowHandler.tick());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(keyBinding.wasPressed()) {
                client.setScreen(new TradeSelectScreen(tradeWorkflowHandler));
            }
        });
    }

    public static TradeWorkflowHandler getTradeWorkFlowHandler() {
        return tradeWorkflowHandler;
    }

}
