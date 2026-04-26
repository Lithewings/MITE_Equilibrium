package com.equilibrium;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.WorldPreset;

public class ModWorldPreset {
    public static final RegistryKey<WorldPreset> CLASSIC_PRESET =
            RegistryKey.of(RegistryKeys.WORLD_PRESET, Identifier.of(OnServerInitialize.MOD_ID,"classic_overworld"));
    public static void worldPresetRegister(){}
}
