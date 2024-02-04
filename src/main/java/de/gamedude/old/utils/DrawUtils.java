package de.gamedude.old.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class DrawUtils {

    public static class EntityUtils {

        public static void drawEntity(MatrixStack matrices, int x, int y, int size, float mouseX, float mouseY, Entity entity, Quaternionf rotation) {
            float f = (float)Math.atan(mouseX / 40.0F);
            float g = (float)Math.atan(mouseY / 40.0F);
            Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F); // Math.PI
            Quaternionf quaternionf2 = (new Quaternionf()).rotateX(g * 20.0F * 0.017453292F); // g * 20f * PI/180 ~ 20° * g
            quaternionf.mul(quaternionf2);
            quaternionf.mul(rotation);

            adjustEntity(entity, g, f, () -> drawEntity(matrices, x, y, size, quaternionf, quaternionf2, entity));
        }

        private static void adjustEntity(Entity entity, float g, float f, Runnable renderRunnable) {
            float h = entity.getBodyYaw();
            float i = entity.getYaw();
            float j = entity.getPitch();
            float k = entity.getHeadYaw();
            entity.setBodyYaw(180.0F + f * 20.0F);
            entity.setYaw(180.0F + f * 40.0F);
            entity.setPitch(-g * 20.0F);
            entity.setHeadYaw(entity.getYaw());
            renderRunnable.run();
            entity.setBodyYaw(h);
            entity.setYaw(i);
            entity.setPitch(j);
            entity.setHeadYaw(k);
        }

        private static void drawEntity(MatrixStack matrices, int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, Entity entity) {
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.translate(0.0, 0.0, 1000.0);
            RenderSystem.applyModelViewMatrix();
            matrices.push();
            matrices.translate(x, y, -950.0);
            matrices.multiplyPositionMatrix((new Matrix4f()).scaling((float) size, (float) size, (float) (-size)));
            matrices.multiply(quaternionf);
            DiffuseLighting.method_34742();
            EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            if (quaternionf2 != null) {
                quaternionf2.conjugate();
                entityRenderDispatcher.setRotation(quaternionf2);
            }

            entityRenderDispatcher.setRenderShadows(false);
            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrices, immediate, 15728880);
            immediate.draw();
            entityRenderDispatcher.setRenderShadows(true);
            matrices.pop();
            DiffuseLighting.enableGuiDepthLighting();
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
        }

        public static class Rotation {
            public static Quaternionf rotate(int x, int y, int z) {
                return new Quaternionf().rotateXYZ(0.017453292F * x, 0.017453292F * y, 0.017453292F * z);
            }
        }

    }

    @FunctionalInterface
    public interface RenderCallback {
        void render(DrawContext context, int x, int y, int w_width, int w_height, int mouseX, int mouseY, float delta);
    }

}
