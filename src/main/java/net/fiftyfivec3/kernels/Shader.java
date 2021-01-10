package net.fiftyfivec3.kernels;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Matrix4f;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL20;

public class Shader {
    private static int ID = 0;
    public static boolean loaded = false;

    private static int kernelLoc = -1;
    private static int brightnessLoc = -1;
    private static int contrastLoc = -1;
    private static int widthLoc = -1, heightLoc = -1;

    private static final int VERTEX = 35633;
    private static final int FRAGMENT = 35632;

    private static int vid = -1, fid = -1, pid = -1;

    private static VertexBuffer vertexBuffer;

    private static final Matrix4f IDENTITY;

    static {
        Matrix4f identity = new Matrix4f();
        identity.loadIdentity();
        IDENTITY = identity;
    }

    private static void compileShader(String vertex, String fragment) {
        vid = GlStateManager.createShader(VERTEX);
        GlStateManager.shaderSource(vid, vertex);
        GlStateManager.compileShader(vid);

        fid = GlStateManager.createShader(FRAGMENT);
        GlStateManager.shaderSource(fid, fragment);
        GlStateManager.compileShader(fid);
        if (GlStateManager.getShader(fid, 35713) == 0) {
            String string3 = StringUtils.trim(GlStateManager.getShaderInfoLog(fid, 32768));
            Mod.LOGGER.warn("Couldn't compile: " + string3);
        }

        pid = GlStateManager.createProgram();

        GlStateManager.attachShader(pid, vid);
        GlStateManager.attachShader(pid, fid);
        GlStateManager.linkProgram(pid);
        int i = GlStateManager.getProgram(pid, 35714);
        if (i == 0) {
            Mod.LOGGER.warn("Error encountered when linking program containing VS  and FS . Log output:");
            Mod.LOGGER.warn(GlStateManager.getProgramInfoLog(pid, 32768));
        }
    }

    public static void createShader()  {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        loaded = true;

        String vertexSource = "#version 120\n" +
                "\n" +
                "uniform float viewWidth;\n" +
                "uniform float viewHeight;\n" +
                "\n" +
                "varying vec2 data[9];\n" +
                "\n" +
                "void main() {\n" +
                "\tgl_Position = ftransform();\n" +
                "\tvec4 texcoord = gl_MultiTexCoord0;\n" +
                "\n" +
                "\tfloat texelWidth = 1.0 / viewWidth;\n" +
                "\tfloat texelHeight = 1.0 / viewHeight;\n" +
                "\n" +
                "\tfor (int i = 0; i < 3; i++) {\n" +
                "\t\tfor (int j = 0; j < 3; j++) {\n" +
                "\t\t\tvec2 step = vec2(texelWidth * (j-1), texelHeight * (i-1));\n" +
                "\t\t\tdata[i*3+j] = texcoord.st + step;\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";

        String fragmentSource = "#version 120\n" +
                "\n" +
                "#define NONE 0\n" +
                "#define EMBOSS 1\n" +
                "#define OUTLINE 2\n" +
                "#define SOBEL 3\n" +
                "#define SHARPEN 4\n" +
                "\n" +
                "uniform int kernel;\n" +
                "uniform int brightness;\n" +
                "uniform int contrast;\n" +
                "varying vec2 data[9];\n" +
                "uniform sampler2D gcolor;\n"+
                "\n" +
                "void main(){if(kernel==NONE)gl_FragColor=texture2D(gcolor,data[5]);else{\n" +
                "\tvec4 resultColor=vec4(0.0);float mat[9];\n" +
                "\tif(kernel==OUTLINE)mat=float[](-1,-1,-1,-1,8,-1,-1,-1,-1);\n" +
                "\telse if(kernel==EMBOSS)mat=float[](2,1,0,1,1,-1,0,-1,-2);\n" +
                "\telse if(kernel==SHARPEN)mat=float[](0,-1,0,-1,5,-1,0,-1,0);\n" +
                "\telse if(kernel==SOBEL)mat=float[](-1,0,1,-2,0,2,-1,0,1);\n" +
                "\telse mat=float[](1,1,1,1,1,1,1,1,1);\n" +
                "\tfor (int i=0;i<9;i++)resultColor+=texture2D(gcolor,data[i])*mat[i];\n" +
                "\tif (brightness!=0)resultColor += brightness / 255.0;\n" +
                "\tif (contrast!=0){\n" +
                "\t\tfloat factor=(259.0*(contrast+255))/(255.0*(259-contrast));\n" +
                "\t\tresultColor+=factor*(resultColor-0.5)+0.5;\n" +
                "\t}\n" +
                "\tgl_FragColor = vec4(resultColor.rgb, 1.0);}}\n";
        compileShader(vertexSource, fragmentSource);

        ID = pid;
        GlProgramManager.useProgram(ID);
        int gcolorLoc = GlStateManager.getUniformLocation(ID, "gcolor");
        GlStateManager.uniform1(gcolorLoc, 0);

        kernelLoc = GlStateManager.getUniformLocation(ID, "kernel");
        brightnessLoc = GlStateManager.getUniformLocation(ID, "brightness");
        contrastLoc = GlStateManager.getUniformLocation(ID, "contrast");
        widthLoc = GlStateManager.getUniformLocation(ID, "viewWidth");
        heightLoc = GlStateManager.getUniformLocation(ID, "viewHeight");
        if (kernelLoc < 0) Mod.LOGGER.warn("SOMETHING WENT WRONG");
        if (brightnessLoc < 0) Mod.LOGGER.warn("BRIGHT WENT WRONG");
        if (contrastLoc < 0) Mod.LOGGER.warn("CONTRAST WENT WRONG");

        GlProgramManager.useProgram(0);

        vertexBuffer = new VertexBuffer(VertexFormats.POSITION_COLOR_TEXTURE);

        BufferBuilder buffer = new BufferBuilder(6 * VertexFormats.POSITION_COLOR_TEXTURE.getVertexSizeInteger());
        buffer.begin(GL11C.GL_TRIANGLES, VertexFormats.POSITION_COLOR_TEXTURE);

        buffer.vertex(1f, 1f, 0f).color(1f,1f,1f,1f).texture(1f, 1f).next();
        buffer.vertex(-1f, 1f, 0f).color(1f,1f,1f,1f).texture(0f, 1f).next();
        buffer.vertex(1f, -1f, 0f).color(1f,1f,1f,1f).texture(1f, 0f).next();

        buffer.vertex(1f, -1f, 0f).color(1f,1f,1f,1f).texture(1f, 0f).next();
        buffer.vertex(-1f, 1f, 0f).color(1f,1f,1f,1f).texture(0f, 1f).next();
        buffer.vertex(-1f, -1f, 0f).color(1f,1f,1f,1f).texture(0f, 0f).next();

        buffer.end();
        vertexBuffer.upload(buffer);
    }

    private static void enable(int width, int height) {
        GlProgramManager.useProgram(ID);


        GL20.glUniform1f(widthLoc, (float) width);
        GL20.glUniform1f(heightLoc, (float) height);
        GlStateManager.uniform1(kernelLoc, Mod.kernel.opt);
        GlStateManager.uniform1(brightnessLoc, Mod.brightness);
        GlStateManager.uniform1(contrastLoc, Mod.contrast);
    }

    private static void disable() {
        GlProgramManager.useProgram(0);
    }
    public static void apply(int width, int height) {
        enable(width, height);

        RenderSystem.disableDepthTest();

        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);

        RenderSystem.enableTexture();
        vertexBuffer.bind();
        VertexFormats.POSITION_COLOR_TEXTURE.startDrawing(0L);
        vertexBuffer.draw(IDENTITY, GL11C.GL_TRIANGLES);
        VertexFormats.POSITION_COLOR_TEXTURE.endDrawing();
        VertexBuffer.unbind();

        RenderSystem.enableDepthTest();

        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);

        disable();
    }
}

