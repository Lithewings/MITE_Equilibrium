package com.equilibrium.mixin.color_light;

import com.equilibrium.util.MoonlightController;
import com.equilibrium.util.WorldMoonPhasesSelector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static net.minecraft.client.render.LightmapTextureManager.getBrightness;

@Mixin(LightmapTextureManager.class)

public abstract class LightmapTextureManagerMixin {

    @Shadow
    @Final
    private  NativeImageBackedTexture texture;
    @Shadow
    @Final
    private NativeImage image;

    @Shadow
    @Final
    private  Identifier textureIdentifier;
    @Shadow
    private boolean dirty;
    @Shadow
    private float flickerIntensity;
    @Shadow
    @Final
    private  GameRenderer renderer;
    @Shadow
    @Final
    private  MinecraftClient client;

    @Unique
    private float getDarkness(LivingEntity entity, float factor, float delta) {
        float f = 0.45F * factor;
        return Math.max(0.0F, MathHelper.cos(((float)entity.age - delta) * (float) Math.PI * 0.025F) * f);
    }
    @Unique
    private float getDarknessFactor(float delta) {
        StatusEffectInstance statusEffectInstance = this.client.player.getStatusEffect(StatusEffects.DARKNESS);
        return statusEffectInstance != null ? statusEffectInstance.getFadeFactor(this.client.player, delta) : 0.0F;
    }


    @Unique
    private static void clamp(Vector3f vec) {
        vec.set(MathHelper.clamp(vec.x, 0.0F, 1.0F), MathHelper.clamp(vec.y, 0.0F, 1.0F), MathHelper.clamp(vec.z, 0.0F, 1.0F));
    }
    @Unique
    private float easeOutQuart(float x) {
        float f = 1.0F - x;
        return 1.0F - f * f * f * f;
    }

    @Inject(method = "update",at = @At(value = "HEAD"),cancellable = true)
    public void update(float delta, CallbackInfo ci) {
        ci.cancel();
        if (this.dirty) {
            this.dirty = false;
            this.client.getProfiler().push("lightTex");
            ClientWorld clientWorld = this.client.world;
            if (clientWorld != null) {
                float f = clientWorld.getSkyBrightness(1.0F);
                float g;
                if (clientWorld.getLightningTicksLeft() > 0) {
                    g = 1.0F;
                } else {
                    g = f * 0.95F + 0.05F;
                }

                float h = this.client.options.getDarknessEffectScale().getValue().floatValue();
                float i = this.getDarknessFactor(delta) * h;
                float j = this.getDarkness(this.client.player, i, delta) * h;
                //水下清晰
                float k = this.client.player.getUnderwaterVisibility();
                float l;
                //设定特殊gamma值
                if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                    l = GameRenderer.getNightVisionStrength(this.client.player, delta);
                } else if (k > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
                    l = k;
                } else {
                    l = 0.0F;
                }

                Vector3f vector3f = new Vector3f(f, f, 1.0F).lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
                float m = this.flickerIntensity + 1.5F;
                Vector3f vector3f2 = new Vector3f();

                for (int n = 0; n < 16; n++) {
                    for (int o = 0; o < 16; o++) {
                        float p = getBrightness(clientWorld.getDimension(), n) * g;
                        float q = getBrightness(clientWorld.getDimension(), o) * m;
                        float s = q * ((q * 0.6F + 0.4F) * 0.6F + 0.4F);
                        float t = q * (q * q * 0.6F + 0.4F);
                        vector3f2.set(q, s, t);
                        //雷电闪烁
                        boolean bl = clientWorld.getDimensionEffects().shouldBrightenLighting();
                        if (bl) {
                            vector3f2.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
                            clamp(vector3f2);
                        } else {
                            Vector3f vector3f3 = new Vector3f(vector3f).mul(p);
                            vector3f2.add(vector3f3);
                            vector3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                            if (this.renderer.getSkyDarkness(delta) > 0.0F) {
                                float u = this.renderer.getSkyDarkness(delta);
                                Vector3f vector3f4 = new Vector3f(vector3f2).mul(0.7F, 0.6F, 0.6F);
                                vector3f2.lerp(vector3f4, u);
                            }
                        }

                        if (l > 0.0F) {
                            float v = Math.max(vector3f2.x(), Math.max(vector3f2.y(), vector3f2.z()));
                            if (v < 1.0F) {
                                float u = 1.0F / v;
                                Vector3f vector3f4 = new Vector3f(vector3f2).mul(u);
                                vector3f2.lerp(vector3f4, l);
                            }
                        }

                        if (!bl) {
                            if (j > 0.0F) {
                                vector3f2.add(-j, -j, -j);
                            }

                            clamp(vector3f2);
                        }

                        float v = this.client.options.getGamma().getValue().floatValue();
                        Vector3f vector3f5 = new Vector3f(this.easeOutQuart(vector3f2.x), this.easeOutQuart(vector3f2.y), this.easeOutQuart(vector3f2.z));


                        //原来的代码:
                        //vector3f2.lerp(vector3f5, Math.max(0.0F, v - i));
                        //方块底色,可以用来表现月光

                        //获取月相
                        String moonType = WorldMoonPhasesSelector.calculateMoonType(clientWorld);


                        //夜视增益
                        //新建集合
                        //100级时获得最大增益值
                        float nightVision = Math.clamp((this.client.player.experienceLevel)*0.01F,0,1);


                        //返回方块底色颜色浓淡的因子
                        float factor = MoonlightController.calculateFactor(clientWorld.getTimeOfDay());
//                        LOGGER.info("light factor is " +factor);
                        if(Objects.equals(moonType, "blueMoon")|| Objects.equals(moonType, "haloMoon")){
                            //蓝色月亮渲染
                            //伽马值修正
                            vector3f2.lerp(vector3f5, 0.1F+nightVision);
//                            LOGGER.info("Blue Moonlight rendering.");
                            vector3f2.lerp(new Vector3f(0F, 0F, factor), 0.07F);
                        } else if (Objects.equals(moonType, "harvestMoon")) {
                            //伽马值修正
                            vector3f2.lerp(vector3f5, 0.1F+nightVision);
                            //黄色月亮渲染
//                            LOGGER.info("Yellow Moonlight rendering.");
                            vector3f2.lerp(new Vector3f(factor, factor, 0F), 0.04F);
                        } else if (Objects.equals(moonType, "bloodMoon")) {
                            //伽马值修正
                            vector3f2.lerp(vector3f5, 0F+nightVision);
                            //红色月亮渲染
//                            LOGGER.info("Blood Moonlight rendering.");
                            vector3f2.lerp(new Vector3f(factor,0F, 0F), 0.08F);}

                        else if (Objects.equals(moonType, "newMoon")) {
                                //伽马值修正
                                vector3f2.lerp(vector3f5, 0F+nightVision);
                                //新月渲染
//                            LOGGER.info("Blood Moonlight rendering.");
                                vector3f2.lerp(new Vector3f(factor,0F, 0F), 0F);
                            }else {
                            //伽马值修正
                            vector3f2.lerp(vector3f5, 0.04F+nightVision);
//                            LOGGER.info("Normal Moonlight rendering.");
                            vector3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0F);
                        }

                        clamp(vector3f2);
                        vector3f2.mul(255.0F);
                        int w = 255;
                        int x = (int)vector3f2.x();
                        int y = (int)vector3f2.y();
                        int z = (int)vector3f2.z();
                        this.image.setColor(o, n, 0xFF000000 | z << 16 | y << 8 | x);
                    }
                }

                this.texture.upload();
                this.client.getProfiler().pop();
            }
        }
    }
}
