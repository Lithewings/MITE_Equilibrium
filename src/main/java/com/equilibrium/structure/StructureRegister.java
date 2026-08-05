package com.equilibrium.structure;

import com.equilibrium.OnServerInitialize;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class StructureRegister {

    // === Feature 注册（DeferredRegister） ===
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, OnServerInitialize.MOD_ID);

    public static final ResourceLocation MOD_MONSTER_ROOM_ID =
            ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "monster_room");

    public static final Supplier<ModDungeonFeature> MOD_DUNGEON_FEATURE =
            FEATURES.register("monster_room", () -> new ModDungeonFeature(NoneFeatureConfiguration.CODEC));

    // === ResourceKey（供 PlacedFeature 引用） ===
    public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, MOD_MONSTER_ROOM_ID);

    public static final ResourceKey<PlacedFeature> PLACED_FEATURE_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, MOD_MONSTER_ROOM_ID);

    // === 生物群系筛选 ===
    public static Predicate<BiomeSelectionContext> foundInUnderworld() {
        return context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD);
    }

    // === 将特征添加到生物群系（延迟调用） ===
    public static void addFeatureToBiomes() {
        BiomeModifications.addFeature(
                foundInUnderworld(),
                GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
                PLACED_FEATURE_KEY
        );
    }
}