package net.fiftyfivec3.kernels.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
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
    @Inject(method="render", at = @At("RETURN"))
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        if (MinecraftClient.getInstance().skipGameRender) return;
        RenderSystem.activeTexture(GL15C.GL_TEXTURE0);
        MinecraftClient mc = MinecraftClient.getInstance();
        Framebuffer main = mc.getFramebuffer();

        if (!Shader.loaded) Shader.createShader();

        main.beginWrite(false);
        RenderSystem.activeTexture(33984);
        RenderSystem.enableTexture();
        int j = main.getColorAttachment();
        RenderSystem.bindTexture(j);
        Shader.apply(mc.getWindow().getWidth(), mc.getWindow().getHeight());
        main.beginWrite(true);
    }
}
