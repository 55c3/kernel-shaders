package net.fiftyfivec3.kernels.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL20;

public abstract class BaseShader {
    private static final int VERTEX = 35633;
    private static final int FRAGMENT = 35632;

    private static VertexBuffer vertexBuffer;

    private static final Matrix4f IDENTITY;

    static {
        Matrix4f identity = new Matrix4f();
        identity.loadIdentity();
        IDENTITY = identity;
    }

    public int id = -1;

    public static void initShaders() {
        vertexBuffer = new VertexBuffer(VertexFormats.POSITION_COLOR_TEXTURE);

        BufferBuilder buffer = new BufferBuilder(6 * VertexFormats.POSITION_COLOR_TEXTURE.getVertexSizeInteger());
        buffer.begin(GL20.GL_TRIANGLES, VertexFormats.POSITION_COLOR_TEXTURE);

        buffer.vertex(1f, 1f, 0f).color(1f,1f,1f,1f).texture(1f, 1f).next();
        buffer.vertex(-1f, 1f, 0f).color(1f,1f,1f,1f).texture(0f, 1f).next();
        buffer.vertex(1f, -1f, 0f).color(1f,1f,1f,1f).texture(1f, 0f).next();

        buffer.vertex(1f, -1f, 0f).color(1f,1f,1f,1f).texture(1f, 0f).next();
        buffer.vertex(-1f, 1f, 0f).color(1f,1f,1f,1f).texture(0f, 1f).next();
        buffer.vertex(-1f, -1f, 0f).color(1f,1f,1f,1f).texture(0f, 0f).next();

        buffer.end();
        vertexBuffer.upload(buffer);
    }

    public void compileShader(String vertex, String fragment) {
        int vid = GlStateManager.createShader(VERTEX);
        GlStateManager.shaderSource(vid, vertex);
        GlStateManager.compileShader(vid);

        int fid = GlStateManager.createShader(FRAGMENT);
        GlStateManager.shaderSource(fid, fragment);
        GlStateManager.compileShader(fid);

        id = GlStateManager.createProgram();

        GlStateManager.attachShader(id, vid);
        GlStateManager.attachShader(id, fid);
        GlStateManager.linkProgram(id);
    }

    public abstract void uniforms();

    public void apply() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (vertexBuffer == null) initShaders();

        GlProgramManager.useProgram(id);
        uniforms();

        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();

        RenderSystem.matrixMode(GL20.GL_PROJECTION);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.matrixMode(GL20.GL_MODELVIEW);

        RenderSystem.enableTexture();
        vertexBuffer.bind();
        VertexFormats.POSITION_COLOR_TEXTURE.startDrawing(0L);
        vertexBuffer.draw(IDENTITY, GL20.GL_TRIANGLES);
        VertexFormats.POSITION_COLOR_TEXTURE.endDrawing();
        VertexBuffer.unbind();

        RenderSystem.enableDepthTest();

        RenderSystem.matrixMode(GL20.GL_PROJECTION);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL20.GL_MODELVIEW);

        GlProgramManager.useProgram(0);
    }
}

