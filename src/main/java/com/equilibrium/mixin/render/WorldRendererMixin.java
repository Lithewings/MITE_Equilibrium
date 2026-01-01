package com.equilibrium.mixin.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.*;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static com.equilibrium.event.MoonPhaseEvent.getMoonType;


@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    private static final Identifier MOON_PHASES = Identifier.of("miteequilibrium","textures/environment/moon_phases.png");
    private static final Identifier END_SKY = Identifier.ofVanilla("textures/environment/end_sky.png");

    private static final Identifier BLOOD_MOON = Identifier.of("miteequilibrium","textures/environment/blood_moon.png");
    private static final Identifier BLUE_MOON = Identifier.of("miteequilibrium","textures/environment/blue_moon.png");
    private static final Identifier HARVEST_MOON = Identifier.of("miteequilibrium","textures/environment/harvest_moon.png");
    private static final Identifier HALO_MOON = Identifier.of("miteequilibrium","textures/environment/halo_moon.png");




    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private ClientWorld world;
    @Shadow
    private VertexBuffer lightSkyBuffer;
    @Shadow
    private static final Identifier SUN = Identifier.ofVanilla("textures/environment/sun.png");

    @Shadow
    private VertexBuffer starsBuffer;
    @Shadow
    private VertexBuffer darkSkyBuffer;

    @Shadow @Final private static Logger LOGGER;

    @Shadow public abstract ChunkBuilder getChunkBuilder();


    @Shadow public abstract void renderSky(Matrix4f matrix4f, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback);

    private void renderEndSkyMixin(MatrixStack matrices) {
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, END_SKY);
        Tessellator tessellator = Tessellator.getInstance();

        for (int i = 0; i < 6; i++) {
            matrices.push();
            if (i == 1) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
            }

            if (i == 2) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            }

            if (i == 4) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
            }

            if (i == 5) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
            }

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(-14145496);
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 16.0F).color(-14145496);
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(16.0F, 16.0F).color(-14145496);
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(16.0F, 0.0F).color(-14145496);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            matrices.pop();
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
    private boolean hasBlindnessOrDarknessMixin(Camera camera) {
        return !(camera.getFocusedEntity() instanceof LivingEntity livingEntity)
                ? false
                : livingEntity.hasStatusEffect(StatusEffects.BLINDNESS) || livingEntity.hasStatusEffect(StatusEffects.DARKNESS);
    }
    @Inject(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FDDD)V",at = @At(value = "HEAD"),cancellable = true)
    public void renderClouds(MatrixStack matrices, Matrix4f matrix4f, Matrix4f matrix4f2, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if(this.world.getRegistryKey()!= World.OVERWORLD)
            ci.cancel();
    }










    @Inject(method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",at = @At(value = "HEAD"),cancellable = true)
    public void renderSky(Matrix4f matrix4f, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
        ci.cancel();
        if(this.client.world.getRegistryKey()== RegistryKey.of(RegistryKeys.WORLD, Identifier.of("miteequilibrium", "underworld")))
            return;
        fogCallback.run();
        if (!thickFog) {
            CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
            if (cameraSubmersionType != CameraSubmersionType.POWDER_SNOW && cameraSubmersionType != CameraSubmersionType.LAVA && !this.hasBlindnessOrDarknessMixin(camera)) {
                MatrixStack matrixStack = new MatrixStack();
                matrixStack.multiplyPositionMatrix(matrix4f);
                if (this.client.world.getDimensionEffects().getSkyType() == DimensionEffects.SkyType.END) {
                    this.renderEndSkyMixin(matrixStack);
                } else if (this.client.world.getDimensionEffects().getSkyType() == DimensionEffects.SkyType.NORMAL) {
                    Vec3d vec3d = this.world.getSkyColor(this.client.gameRenderer.getCamera().getPos(), tickDelta);
                    float f = (float)vec3d.x;
                    float g = (float)vec3d.y;
                    float h = (float)vec3d.z;
                    BackgroundRenderer.applyFogColor();
                    Tessellator tessellator = Tessellator.getInstance();
                    RenderSystem.depthMask(false);
                    RenderSystem.setShaderColor(f, g, h, 1.0F);
                    ShaderProgram shaderProgram = RenderSystem.getShader();
                    this.lightSkyBuffer.bind();
                    this.lightSkyBuffer.draw(matrixStack.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
                    VertexBuffer.unbind();
                    RenderSystem.enableBlend();
                    float[] fs = this.world.getDimensionEffects().getFogColorOverride(this.world.getSkyAngle(tickDelta), tickDelta);
                    if (fs != null) {
                        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        matrixStack.push();
                        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
                        float i = MathHelper.sin(this.world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i));
                        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
                        float j = fs[0];
                        float k = fs[1];
                        float l = fs[2];
                        Matrix4f matrix4f2 = matrixStack.peek().getPositionMatrix();
                        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
                        bufferBuilder.vertex(matrix4f2, 0.0F, 100.0F, 0.0F).color(j, k, l, fs[3]);
                        int m = 16;

                        for (int n = 0; n <= 16; n++) {
                            float o = (float)n * (float) (Math.PI * 2) / 16.0F;
                            float p = MathHelper.sin(o);
                            float q = MathHelper.cos(o);
                            bufferBuilder.vertex(matrix4f2, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F);
                        }

                        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                        matrixStack.pop();
                    }

                    RenderSystem.blendFuncSeparate(
                            GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
                    );
                    matrixStack.push();
                    float i = 1.0F - this.world.getRainGradient(tickDelta);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, i);
                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(this.world.getSkyAngle(tickDelta) * 360.0F));
                    Matrix4f matrix4f3 = matrixStack.peek().getPositionMatrix();
                    float k = 30.0F;
                    RenderSystem.setShader(GameRenderer::getPositionTexProgram);
                    RenderSystem.setShaderTexture(0, SUN);
                    BufferBuilder bufferBuilder2 = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
                    bufferBuilder2.vertex(matrix4f3, -k, 100.0F, -k).texture(0.0F, 0.0F);
                    bufferBuilder2.vertex(matrix4f3, k, 100.0F, -k).texture(1.0F, 0.0F);
                    bufferBuilder2.vertex(matrix4f3, k, 100.0F, k).texture(1.0F, 1.0F);
                    bufferBuilder2.vertex(matrix4f3, -k, 100.0F, k).texture(0.0F, 1.0F);
                    BufferRenderer.drawWithGlobalProgram(bufferBuilder2.end());

//获取月相
                    String moonType =getMoonType(this.world);
//                    LOGGER.info(moonType);

                    //非特殊材质的月相时:
                    if(!Objects.equals(moonType, "harvestMoon") && !Objects.equals(moonType, "haloMoon") && !Objects.equals(moonType, "bloodMoon") && !Objects.equals(moonType, "blueMoon"))
                    { //尺寸大小
//                        LOGGER.info("Not special moon!");
                        k=20.0F;
                        //一般的月相图
                        RenderSystem.setShaderTexture(0,MOON_PHASES);
                        //用自带的函数获取即可
                        int r = this.world.getMoonPhase();
                        int s = r % 4;
                        int m = r / 4 % 2;
                        float t = (float)(s + 0) / 4.0F;
                        float o = (float)(m + 0) / 2.0F;
                        float p = (float)(s + 1) / 4.0F;
                        float q = (float)(m + 1) / 2.0F;
                        bufferBuilder2 = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
                        bufferBuilder2.vertex(matrix4f3, -k, -100.0F, k).texture(p, q);
                        bufferBuilder2.vertex(matrix4f3, k, -100.0F, k).texture(t, q);
                        bufferBuilder2.vertex(matrix4f3, k, -100.0F, -k).texture(t, o);
                        bufferBuilder2.vertex(matrix4f3, -k, -100.0F, -k).texture(p, o);
                        BufferRenderer.drawWithGlobalProgram(bufferBuilder2.end());
                    }else if(moonType.equals("bloodMoon")){
                        //尺寸大小
                        k=160.0F;
                        RenderSystem.setShaderTexture(0,BLOOD_MOON);

                        //用来确定行和列
                        int s = 0;
                        int m = 0;
                        //左右比例,取得左侧
                        float t = (float)(s + 0) / 1.0F;

                        //上下比例,取得上侧
                        float o = (float)(m + 0) / 1.0F;

                        //左右空间所占比例,取到右侧
                        float p = (float)(s + 1) / 1.0F;

                        //上下空间所占比例,取到下侧
                        float q = (float)(m + 1) / 1.0F;


                        bufferBuilder2 = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

                        bufferBuilder2.vertex(matrix4f3, -k, -100.0F, k).texture(p, q);

                        bufferBuilder2.vertex(matrix4f3, k, -100.0F, k).texture(t, q);

                        bufferBuilder2.vertex(matrix4f3, k, -100.0F, -k).texture(t, o);

                        bufferBuilder2.vertex(matrix4f3, -k, -100.0F, -k).texture(p, o);
                        BufferRenderer.drawWithGlobalProgram(bufferBuilder2.end());
                    }else if(moonType.equals("blueMoon"))
                    {
                        //尺寸大小
                        k=160.0F;
                        RenderSystem.setShaderTexture(0,BLUE_MOON);


                        //用来确定行和列
                        int s = 0;
                        int m = 0;
                        //左右比例,取得左侧
                        float t = (float)(s + 0) / 1.0F;

                        //上下比例,取得上侧
                        float o = (float)(m + 0) / 1.0F;

                        //左右空间所占比例,取到右侧
                        float p = (float)(s + 1) / 1.0F;

                        //上下空间所占比例,取到下侧
                        float q = (float)(m + 1) / 1.0F;


                        bufferBuilder2 = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
                        bufferBuilder2.vertex(matrix4f3, -k, -100.0F, k).texture(p, q);
                        bufferBuilder2.vertex(matrix4f3, k, -100.0F, k).texture(t, q);
                        bufferBuilder2.vertex(matrix4f3, k, -100.0F, -k).texture(t, o);
                        bufferBuilder2.vertex(matrix4f3, -k, -100.0F, -k).texture(p, o);
                        BufferRenderer.drawWithGlobalProgram(bufferBuilder2.end());
                    }
                    else if(moonType.equals("harvestMoon")){
                        //尺寸大小
                        k=160.0F;
                        RenderSystem.setShaderTexture(0,HARVEST_MOON);

                        //用来确定行和列
                        int s = 0;
                        int m = 0;
                        //左右比例,取得左侧
                        float t = (float)(s + 0) / 1.0F;

                        //上下比例,取得上侧
                        float o = (float)(m + 0) / 1.0F;

                        //左右空间所占比例,取到右侧
                        float p = (float)(s + 1) / 1.0F;

                        //上下空间所占比例,取到下侧
                        float q = (float)(m + 1) / 1.0F;


                        bufferBuilder2 = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
                        bufferBuilder2.vertex(matrix4f3, -k, -100.0F, k).texture(p, q);
                        bufferBuilder2.vertex(matrix4f3, k, -100.0F, k).texture(t, q);
                        bufferBuilder2.vertex(matrix4f3, k, -100.0F, -k).texture(t, o);
                        bufferBuilder2.vertex(matrix4f3, -k, -100.0F, -k).texture(p, o);
                        BufferRenderer.drawWithGlobalProgram(bufferBuilder2.end());
                    } else if (moonType.equals("haloMoon")){

                        //尺寸大小
                        k=80.0F;
                        RenderSystem.setShaderTexture(0,HALO_MOON);


                        //用来确定行和列
                        int s = 0;
                        int m = 0;
                        //左右比例,取得左侧
                        float t = (float)(s + 0) / 1.0F;

                        //上下比例,取得上侧
                        float o = (float)(m + 0) / 1.0F;

                        //左右空间所占比例,取到右侧
                        float p = (float)(s + 1) / 1.0F;

                        //上下空间所占比例,取到下侧
                        float q = (float)(m + 1) / 1.0F;


                        bufferBuilder2 = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
                        bufferBuilder2.vertex(matrix4f3, -k, -100.0F, k).texture(p, q);
                        bufferBuilder2.vertex(matrix4f3, k, -100.0F, k).texture(t, q);
                        bufferBuilder2.vertex(matrix4f3, k, -100.0F, -k).texture(t, o);
                        bufferBuilder2.vertex(matrix4f3, -k, -100.0F, -k).texture(p, o);
                        BufferRenderer.drawWithGlobalProgram(bufferBuilder2.end());
                    }


//                    int r =(int)(time / 24000L % 8L + 8L) % 8;

//                    int r =(int)(time / 24000L) % 128;
//                    //用来确定行和列
//                    int s = r % 4;
//                    int m = r / 4 % 32;
//                    //左右比例,取得左侧
//                    float t = (float)(s + 0) / 4.0F;
//
//                    //上下比例,取得上侧
//                    float o = (float)(m + 0) / 32.0F;
//
//                    //左右空间所占比例,取到右侧
//                    float p = (float)(s + 1) / 4.0F;
//
//                    //上下空间所占比例,取到下侧
//                    float q = (float)(m + 1) / 32.0F;


                    float u = this.world.getStarBrightness(tickDelta) * i;
                    if (u > 0.0F) {
                        RenderSystem.setShaderColor(u, u, u, u);
                        BackgroundRenderer.clearFog();
                        this.starsBuffer.bind();
                        this.starsBuffer.draw(matrixStack.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionProgram());
                        VertexBuffer.unbind();
                        fogCallback.run();
                    }

                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.disableBlend();
                    RenderSystem.defaultBlendFunc();
                    matrixStack.pop();
                    RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                    double d = this.client.player.getCameraPosVec(tickDelta).y - this.world.getLevelProperties().getSkyDarknessHeight(this.world);
                    if (d < 0.0) {
                        matrixStack.push();
                        matrixStack.translate(0.0F, 12.0F, 0.0F);
                        this.darkSkyBuffer.bind();
                        this.darkSkyBuffer.draw(matrixStack.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
                        VertexBuffer.unbind();
                        matrixStack.pop();
                    }

                    RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                    RenderSystem.depthMask(true);
                }
            }
        }
    }
}
