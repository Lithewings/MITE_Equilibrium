package com.equilibrium.mixin.render.new_sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionSpecialEffects.class)
public abstract class NotRenderSunriseSunsetEffectWhenRain {
    @Shadow
    @Final
    private float[] sunriseCol;


    @Inject(method = "getSunriseColor",at = @At("HEAD"), cancellable = true)
    public void getFogColorOverride(float skyAngle, float tickDelta, CallbackInfoReturnable<float[]> cir) {
        cir.cancel();
        // 获取当前世界（仅客户端生效）
        Minecraft client = Minecraft.getInstance();
        if (client.level == null)
            cir.setReturnValue(null);

        // 1. 计算渐变系数
        float rainGradient = client.level.getRainLevel(tickDelta);
        boolean isOverworld = client.level.dimension()== Level.OVERWORLD;

        if(!isOverworld)
            cir.setReturnValue(null);



        float fadeFactor = isOverworld ? (1.0F - rainGradient) : 0.0F;
        fadeFactor = Mth.clamp(fadeFactor, 0.0F, 1.0F);

        // 渐变系数太小，直接返回null
        if (fadeFactor < 0.01F) cir.setReturnValue(null);

        // 2. 原有逻辑计算基础颜色
        float f = 0.4F;
        float g = Mth.cos(skyAngle * (float) (Math.PI * 2)) - 0.0F;
        float h = -0.0F;
        if (g >= -0.4F && g <= 0.4F) {
            float i = (g - -0.0F) / 0.4F * 0.5F + 0.5F;
            float j = 1.0F - (1.0F - Mth.sin(i * (float) Math.PI)) * 0.99F;
            j *= j;

            // 3. 应用渐变系数到颜色和透明度
            this.sunriseCol[0] = (i * 0.3F + 0.7F) * fadeFactor; // 红色 × 渐变系数
            this.sunriseCol[1] = (i * i * 0.7F + 0.2F) * fadeFactor; // 绿色 × 渐变系数
            this.sunriseCol[2] = (i * i * 0.0F + 0.2F) * fadeFactor; // 蓝色 × 渐变系数
            this.sunriseCol[3] = j * fadeFactor; // 透明度 × 渐变系数

            cir.setReturnValue(this.sunriseCol);
        } else {
            cir.setReturnValue(null);
        }
    }

    }
