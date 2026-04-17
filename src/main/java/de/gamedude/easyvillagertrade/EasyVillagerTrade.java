package de.gamedude.easyvillagertrade;

import com.mojang.brigadier.CommandDispatcher;
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
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.Blocks;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class EasyVillagerTrade implements ModInitializer {

    public static final Config CONFIG = new Config("easyvillagertrade");
    private static EasyVillagerTradeBase modBase;
    private final KeyMapping.Category evtCategory = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("evt", "category"));
    private final KeyMapping keyBinding = new KeyMapping("key.custom.openscreen", GLFW.GLFW_KEY_F6, evtCategory);

    @Override
    public void onInitialize() {
        modBase = new EasyVillagerTradeBase();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> new EasyVillagerTradeCommand(modBase).register(dispatcher, buildContext));
        registerCallbacks();

        CommandDispatcher<CommandSourceStack> dispatcher = new Commands(Commands.CommandSelection.ALL, Commands.createValidationContext(Commands.createValidationContext(VanillaRegistries.createLookup()))).getDispatcher();
        KeyMappingHelper.registerKeyMapping(keyBinding);
    }

    public void registerCallbacks() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (hand == InteractionHand.OFF_HAND || hitResult == null || !world.isClientSide())
                return InteractionResult.PASS;
            if (hitResult.getEntity() instanceof Villager villager && modBase.getState() == TradingState.MODE_SELECTION) {
                modBase.getSelectionInterface().setVillager(villager);
                player.sendOverlayMessage(Component.translatable("evt.command.selected.villager"));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (hand == InteractionHand.OFF_HAND || !world.isClientSide())
                return InteractionResult.PASS;
            BlockPos blockPos = hitResult.getBlockPos();
            if (world.getBlockState(blockPos).getBlock() == Blocks.LECTERN && modBase.getState() == TradingState.MODE_SELECTION) {
                modBase.getSelectionInterface().setLecternPos(blockPos);
                player.sendOverlayMessage(Component.translatable("evt.command.selected.lectern"));
            }
            return InteractionResult.PASS;
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> modBase.handle());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(keyBinding.isDown())
                client.setScreen(new TradeSelectScreen());
        });
    }

    public static EasyVillagerTradeBase getModBase() {
        return modBase;
    }
}
