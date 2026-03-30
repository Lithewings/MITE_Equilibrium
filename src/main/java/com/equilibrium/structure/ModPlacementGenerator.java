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


        //我是如何实现的?
        //首先重写net.minecraft.world.dimension.DimensionOptions;我命名为UnderWorldDimensionOptions
        //在这里添加了一条代码:
        //public static final RegistryKey<DimensionOptions> UNDERWORLD = RegistryKey.of(RegistryKeys.DIMENSION, Identifier.of("miteequilibrium","underworld"));
        //然后执行BiomeModifications.addFeature(context -> context.canGenerateIn(UnderWorldDimensionOptions.UNDERWORLD), GenerationStep.Feature.UNDERGROUND_ORES, CUSTOM_ORE_PLACED_KEY);即可

        //你会发现这条代码执行的和官网上给出的示例不一样,fabric官网上在主世界添加矿物是这么写的:
        //BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, CUSTOM_ORE_PLACED_KEY);
        //首先我去翻了源码:BiomeSelectors.foundInOverworld()就是return context -> context.canGenerateIn(WorldDimensionOptions.OVERWORLD),于是两者等价

        //但是如果要在自定义维度地下世界添加矿物呢,WorldDimensionOptions里面可没有地下世界成员,况且这个类是不可被修改的,用了record关键字
        //所以我把这个类复制粘贴重新生成了一个,命名为UnderWorldDimensionOptions
        //添加了public static final RegistryKey<DimensionOptions> UNDERWORLD = RegistryKey.of(RegistryKeys.DIMENSION, Identifier.ofVanilla("underworld"));
        //问题又来了,Identifier.ofVanilla(String path)方法里可没有接收"underworld"的参数
        //好吧,我去翻了Identifier.ofVanilla(String path)源码:

        //public static Identifier ofVanilla(String path) {
        //		return new Identifier("minecraft", validatePath("minecraft", path));
        //	}
        //如果调用了Identifier.ofVanilla("underworld"),则return了一个实例化Identifier对象:始终是等价为Identifier.of("minecraft","underworld")
        //那我干嘛还要用这个诡异的函数,我自己new一个不就好了
        //所以我改成:Identifier.of("miteequilibrium","underworld")
        //很好,编译通过而且也正常生成了,这是1.21数据包想都别想的事情,居然在自定义世界生成自定义矿物,自己写代码去吧!(就是以上我说的这些)

    }
}

