package de.gamedude.easyvillagertrade.render.widget;

import de.gamedude.easyvillagertrade.utils.DrawUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import org.joml.Quaternionf;

import java.util.Set;

// padding 20 is optimal, if the entity isn't static, so it is not looking out of its box
public class VillagerRenderWidget extends AbstractWidget {

    private final VillagerEntity entity;
    private final int entitySize;
    private final int padding;
    private boolean isStatic;
    private boolean renderBox;

    private DrawUtils.RenderCallback renderCallback;
    private Quaternionf rotation = new Quaternionf();

    public VillagerRenderWidget(int x, int y, int width, int height, int padding) { // change to size usage
        super(x, y, width, height);
        this.entity = EntityType.VILLAGER.create(MinecraftClient.getInstance().world);
        this.padding = padding;
        this.entitySize = determineEntitySize();
    }

    public VillagerRenderWidget(int x, int y, int size) {
        this(x, y, (int) (EntityType.VILLAGER.getWidth() * size), (int) (EntityType.VILLAGER.getHeight() * size), 0);
    }

    private int determineEntitySize() {
        return (int) Math.min((width -  2*padding) /entity.getWidth(), (height - 2 * padding) / entity.getHeight());
    }

    public void setNbtCompoundSupplier(NbtCompound nbtCompound) {
        this.entity.readNbt(nbtCompound);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null)
            return;


        if (world.getTime() % 2 == 0)
            world.tickEntity(entity);

        int entityX = x + width / 2;
        int entityY = y + height / 2 + (int) (entity.getHeight() / 2 * entitySize);

        float c_mouseX = (isStatic) ? 0 : -(mouseX - entityX);
        float c_mouseY = (isStatic) ? 0 : (float) -(mouseY - entityY + entitySize * entity.getEyeY());

        int boxHeight = height;

        if (!isStatic) {
            double varX = Math.atan(c_mouseX / 40f); // 0 -> pi/2
            double varY = Math.atan(c_mouseY / 40f);
            //boxHeight += Math.abs(varY * entitySize / 5d);
        }

        if (renderCallback != null)
            renderCallback.render(context, x, y, width, height, mouseX, mouseY, delta);

        DrawUtils.EntityUtils.drawEntity(context.getMatrices(), entityX, entityY, entitySize, c_mouseX, c_mouseY, entity, rotation);

        if (renderBox)
            context.drawBorder(x, y, width, boxHeight, -1);
    }

    public void setStatic(Quaternionf rotation) {
        this.rotation = rotation;
        this.isStatic = true;
    }

    public void enableRenderingBox() {
        renderBox = true;
    }

    public void renderBackground(DrawUtils.RenderCallback renderCallback) {
        this.renderCallback = renderCallback;
    }

    private Set<String> getNBTKeys() {
        return entity.writeNbt(new NbtCompound()).getKeys();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);

        if (clicked) {
            System.out.println("[DEBUG] VillagerRenderWidget.mouseClicked: " +"test");
        }
        return clicked;
    }
}
