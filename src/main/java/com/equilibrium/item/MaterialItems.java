package com.equilibrium.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.equilibrium.item.Items.ITEMS;

public class MaterialItems {

    // ---------- 锭（堆叠 16） ----------
    public static final DeferredItem<Item> ADAMANTIUM_INGOT = ITEMS.registerItem(
            "adamantium_ingot",
            properties -> new Item(properties.stacksTo(16))
    );
    public static final DeferredItem<Item> ANCIENT_METAL_INGOT = ITEMS.registerItem(
            "ancient_metal_ingot",
            properties -> new Item(properties.stacksTo(16))
    );
    public static final DeferredItem<Item> COPPER_INGOT = ITEMS.registerItem(
            "copper_ingot",
            properties -> new Item(properties.stacksTo(16))
    );
    public static final DeferredItem<Item> GOLD_INGOT = ITEMS.registerItem(
            "gold_ingot",
            properties -> new Item(properties.stacksTo(16))
    );
    public static final DeferredItem<Item> MITHRIL_INGOT = ITEMS.registerItem(
            "mithril_ingot",
            properties -> new Item(properties.stacksTo(16))
    );
    public static final DeferredItem<Item> SILVER_INGOT = ITEMS.registerItem(
            "silver_ingot",
            properties -> new Item(properties.stacksTo(16))
    );

    // ---------- 粒（堆叠 64） ----------
    public static final DeferredItem<Item> ADAMANTIUM_NUGGET = ITEMS.registerItem(
            "adamantium_nugget",
            properties -> new Item(properties.stacksTo(64))
    );
    public static final DeferredItem<Item> ANCIENT_METAL_NUGGET = ITEMS.registerItem(
            "ancient_metal_nugget",
            properties -> new Item(properties.stacksTo(64))
    );
    public static final DeferredItem<Item> COPPER_NUGGET = ITEMS.registerItem(
            "copper_nugget",
            properties -> new Item(properties.stacksTo(64))
    );
    public static final DeferredItem<Item> GOLD_NUGGET = ITEMS.registerItem(
            "gold_nugget",
            properties -> new Item(properties.stacksTo(64))
    );
    public static final DeferredItem<Item> MITHRIL_NUGGET = ITEMS.registerItem(
            "mithril_nugget",
            properties -> new Item(properties.stacksTo(64))
    );
    public static final DeferredItem<Item> SILVER_NUGGET = ITEMS.registerItem(
            "silver_nugget",
            properties -> new Item(properties.stacksTo(64))
    );

    // ---------- 粗矿（堆叠 32）变量名改为 RAW_XXX，注册 ID 为 raw_xxx ----------
    public static final DeferredItem<Item> RAW_ADAMANTIUM = ITEMS.registerItem(
            "raw_adamantium",
            properties -> new Item(properties.stacksTo(32))
    );
    public static final DeferredItem<Item> RAW_MITHRIL = ITEMS.registerItem(
            "raw_mithril",
            properties -> new Item(properties.stacksTo(32))
    );
    public static final DeferredItem<Item> RAW_SILVER = ITEMS.registerItem(
            "raw_silver",
            properties -> new Item(properties.stacksTo(32))
    );

    // ---------- 燧石（堆叠 64） ----------
    public static final DeferredItem<Item> FLINT = ITEMS.registerItem(
            "flint",
            properties -> new Item(properties.stacksTo(64))
    );
}