package com.equilibrium.craft_time_worklevel;

import com.equilibrium.craft_time_register.BlockInit;
import net.minecraft.block.Block;

import java.util.HashMap;
import java.util.Map;

public class CraftingIngredients {


    public static Map<Block, Integer> TABLE_LEVELS = Map.of(
            BlockInit.FLINT_CRAFTING_TABLE, 1,
            BlockInit.COPPER_CRAFTING_TABLE, 2,
            BlockInit.SILVER_CRAFTING_TABLE, 2,
            BlockInit.IRON_CRAFTING_TABLE, 3,
            BlockInit.DIAMOND_CRAFTING_TABLE, 4,
            BlockInit.NETHERITE_CRAFTING_TABLE, 5
    );








//    public static HashMap<Item, CraftingIngredient> ingredients = new HashMap<>();
//    public static HashMap<Material, CraftingIngredient> block_ingredients = new HashMap<>();
    public static HashMap<String, CraftingIngredient> mod_ingredients = new HashMap<>();

    public static void init() {
        //无等级  //五级工作台：下界合金工作台

        //一级工作台：燧石工作台——铜质物品
        register("aliveandwell:copper_crafting_table", CraftingIngredient.FLINT_CRAFTING_TABLE);
        register("aliveandwell:strings", CraftingIngredient.FLINT_CRAFTING_TABLE);
        register("aliveandwell:flint_ingot", CraftingIngredient.FLINT_CRAFTING_TABLE);
        register("minecraft:leather", CraftingIngredient.FLINT_CRAFTING_TABLE);
        register("minecraft:stick", CraftingIngredient.FLINT_CRAFTING_TABLE);

        //二级工作台：铜工作台
        register("aliveandwell:iron_crafting_table", CraftingIngredient.COPPER_CRAFTING_TABLE);
//        register("earlygame:copper_helmet", CraftingIngredient.COPPER_CRAFTING_TABLE);
//        register("earlygame:copper_chestplate", CraftingIngredient.COPPER_CRAFTING_TABLE);
//        register("earlygame:copper_leggings", CraftingIngredient.COPPER_CRAFTING_TABLE);
//        register("earlygame:copper_boots", CraftingIngredient.COPPER_CRAFTING_TABLE);
//        register("earlygame:copper_sword", CraftingIngredient.COPPER_CRAFTING_TABLE);
//        register("earlygame:copper_knife", CraftingIngredient.COPPER_CRAFTING_TABLE);
//        register("earlygame:copper_shove", CraftingIngredient.COPPER_CRAFTING_TABLE);
//        register("earlygame:copper_pickaxe", CraftingIngredient.COPPER_CRAFTING_TABLE);
//        register("earlygame:copper_axe", CraftingIngredient.COPPER_CRAFTING_TABLE);
//        register("earlygame:copper_saw", CraftingIngredient.COPPER_CRAFTING_TABLE);
//        register("earlygame:copper_hoe", CraftingIngredient.COPPER_CRAFTING_TABLE);

        //三级工作台：铁工作台
        register("aliveandwell:diamond_crafting_table", CraftingIngredient.IRON_CRAFTING_TABLE);
//        register("minecraft:iron_sword", CraftingIngredient.IRON_CRAFTING_TABLE);
//        register("minecraft:iron_sword", CraftingIngredient.IRON_CRAFTING_TABLE);
//        register("minecraft:iron_sword", CraftingIngredient.IRON_CRAFTING_TABLE);
//        register("minecraft:iron_sword", CraftingIngredient.IRON_CRAFTING_TABLE);
//        register("minecraft:iron_sword", CraftingIngredient.IRON_CRAFTING_TABLE);
//        register("minecraft:iron_sword", CraftingIngredient.IRON_CRAFTING_TABLE);
//        register("minecraft:iron_sword", CraftingIngredient.IRON_CRAFTING_TABLE);
//        register("minecraft:iron_sword", CraftingIngredient.IRON_CRAFTING_TABLE);


        //四级工作台：钻石工作台
        register("aliveandwell:netherite_crafting_table", CraftingIngredient.DIAMOND_CRAFTING_TABLE);
//        register("minecraft:diamond_sword", CraftingIngredient.DIAMOND_CRAFTING_TABLE);

    }

//    public static void register(Material material, int workbench) {
//        block_ingredients.put(material, new CraftingIngredient(workbench));
//    }


//    public static void register(Item item, int workbench) {
//        ingredients.put(item, new CraftingIngredient(workbench));
//    }

    public static void register(String name, int workbench) {
        mod_ingredients.put(name, new CraftingIngredient(workbench));
    }
}

