package com.equilibrium.structure;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ModPlacementGenerator {
    //为主世界添加的矿物
    public static final RegistryKey<PlacedFeature> CUSTOM_ORE_OVERWORLD = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "ore_custom_overworld"));


    public static final RegistryKey<PlacedFeature> GRAVEL_UPPER = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "gravel_upper"));

    public static final RegistryKey<PlacedFeature> GRAVEL = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "gravel"));

    //Identifier名字要与json文件对应
    public static final RegistryKey<PlacedFeature> SILVER_OVERWORLD = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "silver_ore_overworld"));



    //为地下世界添加的矿物
    public static final RegistryKey<PlacedFeature> CUSTOM_ORE_UNDERWORLD = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "ore_custom_underworld"));

    public static final RegistryKey<PlacedFeature> ADAMANTIUM_ORE = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "adamantium_ore_underworld"));

    public static final RegistryKey<PlacedFeature> ADAMANTIUM_DEEP_ORE = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "adamantium_ore_deep_underworld"));

    public static final RegistryKey<PlacedFeature> COPPER_ORE = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "copper_ore_underworld"));


    public static final RegistryKey<PlacedFeature> IRON_ORE = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "iron_ore_underworld"));


    public static final RegistryKey<PlacedFeature> SILVER_ORE = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "silver_ore_underworld"));

    public static final RegistryKey<PlacedFeature> MITHRIL_ORE = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "mithril_ore_underworld"));

    public static final RegistryKey<PlacedFeature> GOLD_ORE = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "gold_ore_underworld"));

    public static final RegistryKey<PlacedFeature> DIAMOND_ORE = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "diamond_ore_underworld"));

    public static final RegistryKey<PlacedFeature> LAPIS_ORE = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "lapis_ore_underworld"));

    public static final RegistryKey<PlacedFeature> REDSTONE_ORE = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "redstone_ore_underworld"));






    //为地下世界添加的地物

    public static final RegistryKey<PlacedFeature> HUGE_BROWN_MUSHROOM = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "mushroom_island_vegetation"));

    public static final RegistryKey<PlacedFeature> TINY_BROWN_MUSHROOM = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "brown_mushroom_normal"));


    //为下界添加的矿物
    public static final RegistryKey<PlacedFeature> CUSTOM_ORE_NETHER = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "ore_custom_nether"));

    //为末地添加的矿物
    public static final RegistryKey<PlacedFeature> CUSTOM_ORE_END = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            Identifier.of("miteequilibrium", "ore_custom_end"));


    //Identifier.of("miteequilibrium","ore_custom_xxx")格式,就是第一个填你的模组名字(也是文件夹名字),第二个填json名字,记得也去json文件里改名字

    public static void registerModOre() {
        //用自己实现的类在指定维度注册矿物
        //主世界添加矿物
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.OVERWORLD), GenerationStep.Feature.UNDERGROUND_ORES, CUSTOM_ORE_OVERWORLD);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.OVERWORLD), GenerationStep.Feature.UNDERGROUND_ORES, SILVER_OVERWORLD);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.OVERWORLD), GenerationStep.Feature.UNDERGROUND_ORES, GRAVEL_UPPER);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.OVERWORLD), GenerationStep.Feature.UNDERGROUND_ORES, GRAVEL);



        //地下世界添加矿物
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.UNDERGROUND_ORES, CUSTOM_ORE_UNDERWORLD);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.UNDERGROUND_ORES, ADAMANTIUM_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.UNDERGROUND_ORES,
                COPPER_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.UNDERGROUND_ORES,
                SILVER_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.UNDERGROUND_ORES,
                MITHRIL_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.UNDERGROUND_ORES,
                DIAMOND_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.UNDERGROUND_ORES,
                LAPIS_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.UNDERGROUND_ORES,
                REDSTONE_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.UNDERGROUND_ORES,
                GOLD_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.UNDERGROUND_ORES,
                IRON_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.UNDERGROUND_ORES,
                ADAMANTIUM_DEEP_ORE);



        //地下世界添加蘑菇,注意选对Feather标签,见生物群系数据格式
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.SURFACE_STRUCTURES, HUGE_BROWN_MUSHROOM);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.SURFACE_STRUCTURES, TINY_BROWN_MUSHROOM);


        //下界添加矿物
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.NETHER), GenerationStep.Feature.UNDERGROUND_ORES, CUSTOM_ORE_NETHER);

        //末地添加矿物
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.END), GenerationStep.Feature.UNDERGROUND_ORES, CUSTOM_ORE_END);


        //调用规则:替换UnderWorldDimensionOptions.后面的内容,具体去文件去看
        //CUSTOM_ORE_PLACED_KEY是注册的名字,随便改
    }
}

