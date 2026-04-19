package com.equilibrium;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class DamageSourceRegister {
    public static final RegistryKey<DamageType> FATAL_POISON =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(MOD_ID,"fatal_poison"));
    public static void damageSourceInit(){}
}
