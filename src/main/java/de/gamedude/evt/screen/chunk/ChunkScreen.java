package de.gamedude.evt.screen.chunk;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class ChunkScreen extends Screen {
    private float rotationX = 0;
    private float rotationY = 0;

    // Adjust these to change the look
    private final float sphereRadius = 60f;
    private final int segments = 32;

    public ChunkScreen() {
        super(Text.literal("3D Centered Sphere"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw the default dark background
        // Update rotations for a "floating" feel
        rotationY += delta * 0.8f;
        rotationX += delta * 0.3f;

        renderCenteredSphere(context);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderCenteredSphere(DrawContext context) {
        // 1. Move the "origin" to the center of the window
        context.getMatrices().push();
        context.getMatrices().translate(this.width / 2f, this.height / 2f, 200);

        // 2. Apply the rotation based on mouse or auto-timer
        context.getMatrices().multiply(new Quaternionf().rotationXYZ(
                (float) Math.toRadians(rotationX),
                (float) Math.toRadians(rotationY),
                0
        ));

        // 3. Setup the Shader
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest(); // Ensures the back of the sphere stays behind the front

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        // 4. Generate the Sphere Geometry
        for (int i = 0; i <= segments; i++) {
            float lat0 = (float) Math.PI * (-0.5f + (float) (i - 1) / segments);
            float z0 = (float) Math.sin(lat0);
            float zr0 = (float) Math.cos(lat0);

            float lat1 = (float) Math.PI * (-0.5f + (float) i / segments);
            float z1 = (float) Math.sin(lat1);
            float zr1 = (float) Math.cos(lat1);

            for (int j = 0; j <= segments; j++) {
                float lng = (float) (2 * Math.PI * (float) (j - 1) / segments);
                float x = (float) Math.cos(lng);
                float y = (float) Math.sin(lng);

                // Add vertices with a color gradient to show 3D form
                bufferBuilder.vertex(matrix, x * zr0 * sphereRadius, y * zr0 * sphereRadius, z0 * sphereRadius)
                        .color(0.3f, 0.5f, 1.0f, 1.0f).next();
                bufferBuilder.vertex(matrix, x * zr1 * sphereRadius, y * zr1 * sphereRadius, z1 * sphereRadius)
                        .color(0.1f, 0.2f, 0.5f, 1.0f).next();
            }
        }

        tessellator.draw();

        // 5. Clean up
        context.getMatrices().pop();
        RenderSystem.disableDepthTest();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Allows user to spin the sphere with the mouse
        rotationY += deltaX;
        rotationX += deltaY;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}