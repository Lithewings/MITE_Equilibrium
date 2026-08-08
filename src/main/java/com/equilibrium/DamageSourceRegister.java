package com.equilibrium;



import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class DamageSourceRegister {
    public static final ResourceKey<DamageType> FATAL_POISON =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID,"fatal_poison"));
    public static final ResourceKey<DamageType> HURT_BY_BLUE_BERRY =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID,"hurt_by_blue_berry"));
    public static void damageSourceInit(){}
}
