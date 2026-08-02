package com.equilibrium.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {


    public static final TagKey<Item> REACHING_ENCHANTABLE = of("reaching_enchantable");

    public static final TagKey<Item> HARVEST_ONE = of("tool_harvest_1");
    public static final TagKey<Item> HARVEST_TWO = of("tool_harvest_2");
    public static final TagKey<Item> HARVEST_THREE = of("tool_harvest_3");
    public static final TagKey<Item> HARVEST_FOUR = of("tool_harvest_4");
    public static final TagKey<Item> HARVEST_FIVE = of("tool_harvest_5");


    public static final TagKey<Item> IRON_ANVIL_REJECTION = of("iron_anvil_rejection");
    public static final TagKey<Item> MITHRIL_ANVIL_REJECTION = of("mithril_anvil_rejection");




    public static final TagKey<Item> HATCHET = of("hatchet");
    public static final TagKey<Item> AXES = of("axe");
    public static final TagKey<Item> PICKAXES = of("pickaxe");
    public static final TagKey<Item> SWORDS = of("sword");
    public static final TagKey<Item> HOES = of("hoe");
    public static final TagKey<Item> SHOVELS = of("shovel");
    public static final TagKey<Item> DAGGERS = of("dagger");
    public static final TagKey<Item> HAMMERS = of("hammer");

    public static final TagKey<Item> REMOVEITEM = of("remove_item");

    public static final TagKey<Item> HARMFOOD = of("harm_food");
    public static final TagKey<Item> PHYTONUTRIENT_LEVEL1 = of("phytonutrient_level1");

    public static final TagKey<Item> PHYTONUTRIENT_LEVEL2 = of("phytonutrient_level2");

    public static final TagKey<Item> MINING_ENCHANTABLE = of("mining_enchantable");

    public static final TagKey<Item> UNBREAKING = of("unbreaking");
    public static final TagKey<Item> UNBREAKING_ENCHANTABLE = of("unbreaking_enchantable");

    //铁砧上可以生效的
    public static final TagKey<Item> FORTUNE = of("fortune");
    //只能在附魔台附魔的,应该是铁砧的子集
    public static final TagKey<Item> FORTUNE_ENCHANTABLE = of("fortune_enchantable");

    public static final TagKey<Item> SILKTOUCH = of("silktouch");


    //燃料等级
    public static final TagKey<Item> FUEL_LEVEL1 = of("fuel_level1");
    public static final TagKey<Item> FUEL_LEVEL2 = of("fuel_level2");
    public static final TagKey<Item> FUEL_LEVEL3 = of("fuel_level3");
    public static final TagKey<Item> FUEL_LEVEL4 = of("fuel_level4");
    //物品燃烧的所需要的燃料等级
    public static final TagKey<Item> BLOCK_NEED_FUEL_LEVEL1 = of("block_need_fuel_level1");
    public static final TagKey<Item> BLOCK_NEED_FUEL_LEVEL2 = of("block_need_fuel_level2");
    public static final TagKey<Item> BLOCK_NEED_FUEL_LEVEL3 = of("block_need_fuel_level3");
    public static final TagKey<Item> BLOCK_NEED_FUEL_LEVEL4 = of("block_need_fuel_level4");
    //有些物品燃烧也需要等级
    public static final TagKey<Item> ITEM_NEED_FUEL_LEVEL1 = of("item_need_fuel_level1");
    public static final TagKey<Item> ITEM_NEED_FUEL_LEVEL2 = of("item_need_fuel_level2");
    public static final TagKey<Item> ITEM_NEED_FUEL_LEVEL3 = of("item_need_fuel_level3");
    public static final TagKey<Item> ITEM_NEED_FUEL_LEVEL4 = of("item_need_fuel_level4");


    //合成等级,若出现以下物品,则获取更高的合成等级
    public static final TagKey<Item> CRAFT_LEVEL1 = of("craft_level1");
    public static final TagKey<Item> CRAFT_LEVEL2 = of("craft_level2");
    public static final TagKey<Item> CRAFT_LEVEL3 = of("craft_level3");
    public static final TagKey<Item> CRAFT_LEVEL4 = of("craft_level4");
    public static final TagKey<Item> CRAFT_LEVEL5 = of("craft_level5");
    //合成合成台时应该降低合成要求

    public static final TagKey<Item> CRAFT_TABLE = of("crafttable");



    //所有金属锭
    public static final TagKey<Item> INGOT = of("ingot");

    private static TagKey<Item> of(String id) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium",id));
    }


    public static void registerModItemTags(){
    }

}
