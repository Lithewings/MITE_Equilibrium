package com.equilibrium.structure_generator;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;

import java.util.function.Predicate;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class StructureRegister {

    public static final Identifier MOD_MONSTER_ROOM_ID = Identifier.of(MOD_ID, "monster_room");
    public static final ModDungeonFeature modDungeonConfigFeature = new ModDungeonFeature(DefaultFeatureConfig.CODEC);

    public static final RegistryKey<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE_REGISTRY_KEY = RegistryKey.of(
            RegistryKeys.CONFIGURED_FEATURE,
            Identifier.of(MOD_ID, "monster_room")
    );

    public static final RegistryKey<PlacedFeature> MONSTER_ROOM_PLACED_FEATURE_FOR_REGISTRY = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE,
            Identifier.of(MOD_ID, "monster_room")
    );
//
    public static Predicate<BiomeSelectionContext> foundInUnderworld() {
        return context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD);
    }


    public static void registerStructure(){
        Registry.register(Registries.FEATURE, MOD_MONSTER_ROOM_ID, modDungeonConfigFeature);
        BiomeModifications.addFeature(
                foundInUnderworld(), // 添加到地下世界
                GenerationStep.Feature.UNDERGROUND_STRUCTURES, // 在地下结构阶段生成
                StructureRegister.MONSTER_ROOM_PLACED_FEATURE_FOR_REGISTRY // 使用 PlacedFeature 的 RegistryKey
        );

    }
}
