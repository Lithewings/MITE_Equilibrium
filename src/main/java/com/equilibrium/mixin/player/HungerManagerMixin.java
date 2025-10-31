package com.equilibrium.mixin.player;

import com.equilibrium.util.PlayerMaxHealthOrFoodLevelHelper;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HungerManager.class)


public abstract class HungerManagerMixin {

    @Shadow
    private int foodLevel;
    @Shadow
    private float saturationLevel;


    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getHealth()F"))
    private float forceHealthCheck(PlayerEntity player) {
        // 强制返回一个大于 10 的值，使得 `player.getHealth() > 10.0F` 永远为 true,所以玩家一定会饿死
        return 20.0F; // 玩家满血时返回 20.0F，但任意大于 10 的值均可
    }

    @Inject(method = "update", at = @At("HEAD"))
    public void update(PlayerEntity player, CallbackInfo ci) {
        int maxFoodLevel = PlayerMaxHealthOrFoodLevelHelper.getMaxHealthOrFoodLevel(player);
        this.foodLevel = MathHelper.clamp(this.foodLevel, 0, maxFoodLevel);
        this.saturationLevel = MathHelper.clamp(this.saturationLevel, 0.0F, maxFoodLevel);
    }



    @Inject(method = "addInternal", at = @At("HEAD"), cancellable = true)
    private void addInternal(int nutrition, float saturation, CallbackInfo ci) {
        this.foodLevel = MathHelper.clamp(nutrition + this.foodLevel, 0, 20);
        this.saturationLevel = MathHelper.clamp(saturation + this.saturationLevel, 0.0F, 20);
        ci.cancel();
    }

    @Inject(method = "isNotFull", at = @At("HEAD"), cancellable = true)
    public void isNotFull(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }


}
