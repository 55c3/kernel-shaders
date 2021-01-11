package net.fiftyfivec3.kernels.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gl.GlProgramManager;

public class KernelShader extends BaseShader {
    private final int heightLoc, widthLoc, contrastLoc, brightnessLoc, kernelLoc;
    public static int width, height, kernel, contrast, brightness;

    public KernelShader(int w, int h) {
        String vertexSource = "#version 120\nuniform int w;uniform int h;varying vec2 data[9];void main(){gl_Position=ftransform();vec4 tc=gl_MultiTexCoord0;float tw=1.0/w;float th=1.0/h;for(int i=0;i<3;i++){for(int j=0;j<3;j++){vec2 step=vec2(tw*(j-1),th*(i-1));data[i*3+j]=tc.st+step;}}}";
        String fragmentSource = "#version 120\nuniform sampler2D gcolor;uniform int kernel;uniform int brightness;uniform int contrast;varying vec2 data[9];void main(){if(kernel==0)gl_FragColor=texture2D(gcolor,data[5]);else{vec4 resultColor=vec4(0.0);float mat[9];if(kernel==2)mat=float[](-1,-1,-1,-1,8,-1,-1,-1,-1);else if(kernel==1)mat=float[](2,1,0,1,1,-1,0,-1,-2);else if(kernel==4)mat=float[](0,-1,0,-1,5,-1,0,-1,0);else if(kernel==3)mat=float[](-1,0,1,-2,0,2,-1,0,1);else mat=float[](0,0,0,0,1,0,0,0,0);for(int i=0;i<9;i++)resultColor+=texture2D(gcolor,data[i])*mat[i];if(brightness!=0)resultColor+=brightness/255.0;if(contrast!=0){float factor=(259.0*(contrast+255))/(255.0*(259-contrast));resultColor+=factor*(resultColor-0.5)+0.5;}gl_FragColor=vec4(resultColor.rgb,1.0);}}";
        compileShader(vertexSource, fragmentSource);

        GlProgramManager.useProgram(id);
        int gcolorLoc = GlStateManager.getUniformLocation(id, "gcolor");
        GlStateManager.uniform1(gcolorLoc, 0);

        kernelLoc = GlStateManager.getUniformLocation(id, "kernel");
        brightnessLoc = GlStateManager.getUniformLocation(id, "brightness");
        contrastLoc = GlStateManager.getUniformLocation(id, "contrast");
        widthLoc = GlStateManager.getUniformLocation(id, "w");
        heightLoc = GlStateManager.getUniformLocation(id, "h");

        width = w;
        height = h;
    }

    @Override
    public void uniforms() {
        if (width > -1) GlStateManager.uniform1(widthLoc, width);
        if (height > -1) GlStateManager.uniform1(heightLoc, height);
        if (kernel > -1) GlStateManager.uniform1(kernelLoc, kernel);
        if (contrast > -1) GlStateManager.uniform1(contrastLoc, contrast);
        if (brightness > -1) GlStateManager.uniform1(brightnessLoc, brightness);
        width = height = kernel = contrast = brightness = -1;
    }
}

