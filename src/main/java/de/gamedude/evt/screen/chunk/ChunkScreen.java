package de.gamedude.evt.screen.chunk;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ChunkScreen extends Screen {

    private float rotationX = 30f;
    private float rotationY = 45f;
    private float zoom = 20f;
    private boolean isDragging;

    private List<BlockPos> cachedBlocks;
    private List<BlockPos> cachedLecterns;
    private List<VillagerEntity> cachedVillagers;
    private BlockPos playerPos;

    private VertexConsumerProvider.Immediate vertexConsumers;

    public ChunkScreen() {
        super(Text.empty());
    }

    private VertexConsumerProvider.Immediate getVertexConsumers() {
        if (vertexConsumers == null) {
            vertexConsumers = MinecraftClient.getInstance()
                    .getBufferBuilders()
                    .getEntityVertexConsumers();
        }
        return vertexConsumers;
    }

    @Override
    protected void init() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        this.playerPos = player.getBlockPos();
        this.cachedBlocks = new ArrayList<>();
        this.cachedLecterns = new ArrayList<>();

        World world = player.getEntityWorld();
        Direction forward = player.getHorizontalFacing();
        Direction right = forward.rotateYClockwise();

        for (int delY = -1; delY <= 2; delY++) {
            for (int delSide = -3; delSide <= 3; delSide++) {
                for (int delFront = -1; delFront <= 2; delFront++) {
                    BlockPos pos = playerPos
                            .offset(forward, delFront)
                            .offset(right, delSide)
                            .up(delY);

                    BlockState state = world.getBlockState(pos);

                    if (state.isAir()) continue;

                    if (state.getBlock() instanceof LecternBlock) {
                        cachedLecterns.add(pos);
                    } else if (!state.hasBlockEntity()) {
                        cachedBlocks.add(pos);
                    }
                }
            }
        }

        this.cachedVillagers = world.getEntitiesByClass(
                VillagerEntity.class,
                new Box(playerPos).expand(6),
                v -> true
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        renderPreview(context);
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderPreview(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        if (world == null || cachedBlocks == null) return;

        BlockRenderManager blockRenderer = client.getBlockRenderManager();
        EntityRenderDispatcher entityRenderer = client.getEntityRenderDispatcher();

        int centerX = this.width / 4;
        int centerY = this.height / 2;

        MatrixStack matrices = context.getMatrices();
        matrices.push();

        matrices.translate(centerX, centerY, 100);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationY));
        matrices.scale(zoom, -zoom, zoom);

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        // --- Normale Blöcke ---
        for (BlockPos pos : cachedBlocks) {
            BlockState state = world.getBlockState(pos);

            double relX = pos.getX() - playerPos.getX();
            double relY = pos.getY() - playerPos.getY();
            double relZ = pos.getZ() - playerPos.getZ();

            matrices.push();
            matrices.translate(relX, relY, relZ);
            blockRenderer.renderBlockAsEntity(
                    state,
                    matrices,
                    getVertexConsumers(),
                    LightmapTextureManager.MAX_LIGHT_COORDINATE,
                    OverlayTexture.DEFAULT_UV
            );
            matrices.pop();
        }

        getVertexConsumers().draw();

        // --- Lecterns ---
        for (BlockPos pos : cachedLecterns) {
            BlockState state = world.getBlockState(pos);

            double relX = pos.getX() - playerPos.getX();
            double relY = pos.getY() - playerPos.getY();
            double relZ = pos.getZ() - playerPos.getZ();

            matrices.push();
            matrices.translate(relX, relY, relZ);
            blockRenderer.renderBlockAsEntity(
                    state,
                    matrices,
                    getVertexConsumers(),
                    LightmapTextureManager.MAX_LIGHT_COORDINATE,
                    OverlayTexture.DEFAULT_UV
            );
            matrices.pop();
        }

        getVertexConsumers().draw();

        // --- Villager ---
        for (VillagerEntity villager : cachedVillagers) {
            double relX = villager.getX() - playerPos.getX();
            double relY = villager.getY() - playerPos.getY();
            double relZ = villager.getZ() - playerPos.getZ();

            matrices.push();
            matrices.translate(relX, relY, relZ);
            entityRenderer.render(
                    villager,
                    0, 0, 0,
                    villager.getYaw(),
                    client.getTickDelta(),
                    matrices,
                    getVertexConsumers(),
                    LightmapTextureManager.MAX_LIGHT_COORDINATE);
            matrices.pop();
        }

        getVertexConsumers().draw();

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();

        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) isDragging = true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging) {
            rotationY += (float) deltaX * 0.5f;
            rotationX += (float) deltaY * 0.5f;
            rotationX = MathHelper.clamp(rotationX, -89f, 89f);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        zoom = MathHelper.clamp(zoom + (float) amount * 2f, 5f, 60f);
        return true;
    }

    @Override
    public void renderBackground(DrawContext context) {
        context.fill(0, 0, this.width, this.height, ColorHelper.Argb.getArgb(150, 7, 7, 7));
    }
}