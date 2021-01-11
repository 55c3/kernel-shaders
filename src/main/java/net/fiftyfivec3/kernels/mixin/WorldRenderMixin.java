package net.fiftyfivec3.kernels.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fiftyfivec3.kernels.shaders.IdentityShader;
import net.fiftyfivec3.kernels.shaders.KernelShader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRenderMixin {
    private Framebuffer fb = null;
    private KernelShader shader = null;
    private IdentityShader identity = null;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    @Inject(method="render", at = @At("RETURN"))
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        if (mc.skipGameRender) return;
        Framebuffer main = mc.getFramebuffer();

        if (fb == null) {
            fb = new Framebuffer(main.textureWidth, main.textureHeight, false, true);
            fb.initFbo(main.textureWidth, main.textureHeight, true);
        }

        if (shader == null) {
            shader = new KernelShader(main.textureWidth, main.textureHeight);
            identity = new IdentityShader();
        }

        // check if a resize occurred
        if (fb.textureHeight != main.textureHeight || fb.textureWidth != main.textureWidth) {
            fb.resize(main.textureWidth, main.textureHeight, false);
            KernelShader.width = main.textureWidth;
            KernelShader.height = main.textureHeight;
        }

        // read from main
        RenderSystem.activeTexture(GL20.GL_TEXTURE0);
        RenderSystem.bindTexture(main.getColorAttachment());
        fb.beginWrite(false);
        shader.apply();

        // read from fb
        RenderSystem.activeTexture(GL20.GL_TEXTURE0);
        RenderSystem.bindTexture(fb.getColorAttachment());
        main.beginWrite(false);
        identity.apply();
    }
}
