package com.equilibrium.mixin.potion;

import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(net.minecraft.entity.effect.PoisonStatusEffect.class)
public class PoisonStatusEffectMixin {
    @ModifyConstant(method = "applyUpdateEffect",constant = @Constant(floatValue = 1.0F,ordinal = 0))
    private float fatalPoison(float constant){
        //Players will lose their last half heart.
        return 0.0F;
    }
}
