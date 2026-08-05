package com.equilibrium.structure;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModPlacementGenerator {
    //为主世界添加的矿物
    public static final ResourceKey<PlacedFeature> CUSTOM_ORE_OVERWORLD = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "ore_custom_overworld"));


    public static final ResourceKey<PlacedFeature> GRAVEL_UPPER = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gravel_upper"));

    public static final ResourceKey<PlacedFeature> GRAVEL = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gravel"));

    //Identifier名字要与json文件对应
    public static final ResourceKey<PlacedFeature> SILVER_OVERWORLD = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "silver_ore_overworld"));



    //为地下世界添加的矿物
    public static final ResourceKey<PlacedFeature> CUSTOM_ORE_UNDERWORLD = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "ore_custom_underworld"));

    public static final ResourceKey<PlacedFeature> ADAMANTIUM_ORE = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "adamantium_ore_underworld"));

    public static final ResourceKey<PlacedFeature> ADAMANTIUM_DEEP_ORE = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "adamantium_ore_deep_underworld"));

    public static final ResourceKey<PlacedFeature> COPPER_ORE = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "copper_ore_underworld"));


    public static final ResourceKey<PlacedFeature> IRON_ORE = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "iron_ore_underworld"));


    public static final ResourceKey<PlacedFeature> SILVER_ORE = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "silver_ore_underworld"));

    public static final ResourceKey<PlacedFeature> MITHRIL_ORE = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "mithril_ore_underworld"));

    public static final ResourceKey<PlacedFeature> GOLD_ORE = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gold_ore_underworld"));

    public static final ResourceKey<PlacedFeature> DIAMOND_ORE = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "diamond_ore_underworld"));

    public static final ResourceKey<PlacedFeature> LAPIS_ORE = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "lapis_ore_underworld"));

    public static final ResourceKey<PlacedFeature> REDSTONE_ORE = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "redstone_ore_underworld"));






    //为地下世界添加的地物

    public static final ResourceKey<PlacedFeature> HUGE_BROWN_MUSHROOM = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "mushroom_island_vegetation"));

    public static final ResourceKey<PlacedFeature> TINY_BROWN_MUSHROOM = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "brown_mushroom_normal"));


    //为下界添加的矿物
    public static final ResourceKey<PlacedFeature> CUSTOM_ORE_NETHER = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "ore_custom_nether"));

    //为末地添加的矿物
    public static final ResourceKey<PlacedFeature> CUSTOM_ORE_END = ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("miteequilibrium", "ore_custom_end"));


    //Identifier.of("miteequilibrium","ore_custom_xxx")格式,就是第一个填你的模组名字(也是文件夹名字),第二个填json名字,记得也去json文件里改名字

    public static void registerModOre() {
        //用自己实现的类在指定维度注册矿物
        //主世界添加矿物
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.OVERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES, CUSTOM_ORE_OVERWORLD);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.OVERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES, SILVER_OVERWORLD);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.OVERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES, GRAVEL_UPPER);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.OVERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES, GRAVEL);



        //地下世界添加矿物
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES, CUSTOM_ORE_UNDERWORLD);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES, ADAMANTIUM_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES,
                COPPER_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES,
                SILVER_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES,
                MITHRIL_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES,
                DIAMOND_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES,
                LAPIS_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES,
                REDSTONE_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES,
                GOLD_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES,
                IRON_ORE);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES,
                ADAMANTIUM_DEEP_ORE);



        //地下世界添加蘑菇,注意选对Feather标签,见生物群系数据格式
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Decoration.SURFACE_STRUCTURES, HUGE_BROWN_MUSHROOM);
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Decoration.SURFACE_STRUCTURES, TINY_BROWN_MUSHROOM);


        //下界添加矿物
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.NETHER), GenerationStep.Decoration.UNDERGROUND_ORES, CUSTOM_ORE_NETHER);

        //末地添加矿物
        BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.END), GenerationStep.Decoration.UNDERGROUND_ORES, CUSTOM_ORE_END);


        //调用规则:替换UnderWorldDimensionOptions.后面的内容,具体去文件去看
        //CUSTOM_ORE_PLACED_KEY是注册的名字,随便改
    }
}

