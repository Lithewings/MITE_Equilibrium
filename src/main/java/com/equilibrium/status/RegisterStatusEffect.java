package com.equilibrium.status;

import com.equilibrium.OnServerInitialize;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegisterStatusEffect {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, OnServerInitialize.MOD_ID);

    // 返回类型改为 DeferredHolder，它实现了 Holder<MobEffect>
    public static final DeferredHolder<MobEffect, PhytonutrientStatusEffect> PHYTONUTRIENT =
            MOB_EFFECTS.register("phytonutrient", PhytonutrientStatusEffect::new);

    public static final DeferredHolder<MobEffect, InsulinResistanceStatusEffect> INSULIN_RESISTANCE =
            MOB_EFFECTS.register("insulin_resistance", InsulinResistanceStatusEffect::new);
}