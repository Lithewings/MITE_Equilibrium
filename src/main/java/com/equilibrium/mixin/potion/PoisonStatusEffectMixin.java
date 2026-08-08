package com.equilibrium.mixin.potion;

import com.equilibrium.DamageSourceRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(net.minecraft.world.effect.PoisonMobEffect.class)
public class PoisonStatusEffectMixin {
//    @ModifyConstant(method = "applyUpdateEffect",constant = @Constant(floatValue = 1.0F,ordinal = 0))
//    private float fatalPoison(float constant){
//        //Players will lose their last half heart.
//        return 0.0F;
//    }
    @Inject(method = "applyEffectTick",at = @At(value = "HEAD"),cancellable = true)
    public void applyUpdateEffect(LivingEntity entity, int amplifier, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        Holder<DamageType> fatalPoison = entity.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolder(DamageSourceRegister.FATAL_POISON)
                .orElseThrow(() -> new IllegalStateException("DamageType not registered"));
        entity.hurt(new DamageSource(fatalPoison), 1.0F);
        cir.setReturnValue(true);
    }
}
