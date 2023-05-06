package de.gamedude.easyvillagertrade;

import de.gamedude.easyvillagertrade.commands.EasyVillagerTradeCommand;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.screen.TradeSelectScreen;
import de.gamedude.easyvillagertrade.utils.ScriptArgumentType;
import de.gamedude.easyvillagertrade.utils.TradingState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class EasyVillagerTrade implements ModInitializer {

    private static EasyVillagerTradeBase modBase;
    KeyBinding keyBinding = new KeyBinding("key.custom.openscreen", GLFW.GLFW_KEY_F6, "EasyVillagerTrade");

    @Override
    public void onInitialize() {
        KeyBindingHelper.registerKeyBinding(keyBinding);
        ArgumentTypeRegistry.registerArgumentType(new Identifier("easyvillagertrade", "scriptname"), ScriptArgumentType.class, ConstantArgumentSerializer.of(ScriptArgumentType::scriptArgumentType));

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            modBase = new EasyVillagerTradeBase();
            ClientCommandRegistrationCallback.EVENT.register(new EasyVillagerTradeCommand(modBase));
            registerCallbacks();
        });

    }

    public void registerCallbacks() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (hand == Hand.OFF_HAND || hitResult == null || !world.isClient())
                return ActionResult.PASS;
            if (hitResult.getEntity() instanceof VillagerEntity villager && modBase.getState() == TradingState.MODE_SELECTION) {
                modBase.getSelectionInterface().setVillager(villager);
                player.sendMessage(Text.of("§8| §7Selected villager"));
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
                player.sendMessage(Text.of("§8| §7Selected lectern"));
            }
            return ActionResult.PASS;
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> modBase.handle());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(keyBinding.wasPressed()) {
                client.setScreen(new TradeSelectScreen());
            }
        });

        Vec3d vec3d = new Vec3d(600, 0, 0);
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            var speed = 0.13;


            /*if(player.getPos().distanceTo(vec3d) > 0.1) {
                System.out.println(player.getMovementSpeed());
                Vec3d dir = vec3d.subtract(player.getPos()).normalize().multiply(speed);
                player.move(MovementType.SELF, dir);
                player.tickMovement();
            }*/
        });
    }

    public static EasyVillagerTradeBase getModBase() {
        return modBase;
    }
}
