package com.equilibrium.status;


import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class RegisterStatusEffect {
    public static final RegistryEntry<StatusEffect> PHYTONUTRIENT = register("phytonutrient",new PhytonutrientStatusEffect());
    public static final RegistryEntry<StatusEffect> INSULIN_RESISTANCE = register("insulin_resistance",new InsulinResistanceStatusEffect());

    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID,id), statusEffect);
    }





    public static void registerStatusEffects(){

    }
}
