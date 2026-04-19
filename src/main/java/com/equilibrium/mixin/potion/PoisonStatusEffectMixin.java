package com.equilibrium.mixin.potion;

import com.equilibrium.DamageSourceRegister;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(net.minecraft.entity.effect.PoisonStatusEffect.class)
public class PoisonStatusEffectMixin {
//    @ModifyConstant(method = "applyUpdateEffect",constant = @Constant(floatValue = 1.0F,ordinal = 0))
//    private float fatalPoison(float constant){
//        //Players will lose their last half heart.
//        return 0.0F;
//    }
    @Inject(method = "applyUpdateEffect",at = @At(value = "HEAD"),cancellable = true)
    public void applyUpdateEffect(LivingEntity entity, int amplifier, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        RegistryEntry<DamageType> fatalPoison = entity.getWorld().getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .getEntry(DamageSourceRegister.FATAL_POISON)
                .orElseThrow(() -> new IllegalStateException("DamageType not registered"));
        entity.damage(new DamageSource(fatalPoison), 1.0F);
        cir.setReturnValue(true);
    }
}
