package com.equilibrium.status;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class InsulinResistanceStatusEffect extends MobEffect {
    protected InsulinResistanceStatusEffect() {
        super(
                MobEffectCategory.HARMFUL, // 药水效果是有益的还是有害的
                5797459);
    }
}
