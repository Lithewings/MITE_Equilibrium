package com.equilibrium.status;


import static com.equilibrium.OnServerInitialize.MOD_ID;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class RegisterStatusEffect {
    public static final Holder<MobEffect> PHYTONUTRIENT = register("phytonutrient",new PhytonutrientStatusEffect());
    public static final Holder<MobEffect> INSULIN_RESISTANCE = register("insulin_resistance",new InsulinResistanceStatusEffect());

    private static Holder<MobEffect> register(String id, MobEffect statusEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(MOD_ID,id), statusEffect);
    }





    public static void registerStatusEffects(){

    }
}
