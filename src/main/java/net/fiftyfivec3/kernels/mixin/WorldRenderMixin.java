package net.fiftyfivec3.kernels.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fiftyfivec3.kernels.Mod;
import net.fiftyfivec3.kernels.Options;
import net.fiftyfivec3.kernels.Shader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL15C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRenderMixin {
    private Framebuffer fb = null;

    @Inject(method="render", at = @At("RETURN"))
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        if (MinecraftClient.getInstance().skipGameRender) return;
        RenderSystem.activeTexture(GL15C.GL_TEXTURE0);
        MinecraftClient mc = MinecraftClient.getInstance();
        Framebuffer main = mc.getFramebuffer();

        if (fb == null) {
            fb = new Framebuffer(main.textureWidth, main.textureHeight, false, true);
            fb.initFbo(main.textureWidth, main.textureHeight, true);
        }
        if (!Shader.loaded) Shader.createShader();

        if (fb.textureHeight != main.textureHeight || fb.textureWidth != main.textureWidth) fb.resize(main.textureWidth, main.textureHeight, false);

        int j = main.getColorAttachment();
        RenderSystem.activeTexture(GL15C.GL_TEXTURE0);
        RenderSystem.bindTexture(j);

        fb.beginWrite(false);
        Shader.apply(main.textureWidth, main.textureHeight);

        Options temp = Mod.kernel;
        Mod.kernel = Options.NONE;
        fb.endWrite();

        RenderSystem.activeTexture(GL15C.GL_TEXTURE0);
        j = fb.getColorAttachment();
        RenderSystem.bindTexture(j);

        main.beginWrite(false);
        Shader.apply(main.textureWidth, main.textureHeight);

        Mod.kernel = temp;

        main.beginWrite(true);
    }
}
