package de.gamedude.evt.handler;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Map;

public interface Handler {

    default <T extends Handler> T getHandler(Class<T> clazz) {
        return TradeWorkflow.INSTANCE.getHandler(clazz);
    }

    default ClientPlayerEntity player() {
        return MinecraftClient.getInstance().player;
    }

    class TestTask extends MultiTickTask<PlayerEntity> {

        public TestTask() {
            super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED), Integer.MAX_VALUE);
        }

        @Override
        protected void run(ServerWorld world, PlayerEntity entity, long time) {
            entity.sendMessage(Text.of("Hola mucho gusto"));
        }

        @Override
        protected void keepRunning(ServerWorld world, PlayerEntity entity, long time) {
            System.out.println("[DEBUG] TestTask.keepRunning: " + "keep running");
        }

        @Override
        protected boolean shouldKeepRunning(ServerWorld world, PlayerEntity entity, long time) {
            return true;
        }
    }

}
