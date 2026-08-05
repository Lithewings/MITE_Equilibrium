package com.equilibrium.item.material;

import com.equilibrium.OnServerInitialize;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


public class MaterialItems {

    //为了匹配assets和data中的注册名,这里按照Fabric版本的习惯来命名
    //adamantium_ingot->adamantium
    //raw_adamantium->adamantium_raw
    //或者一起提供配套的assets和data
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OnServerInitialize.MOD_ID);


    // ---------- 锭（堆叠 16） ----------
    public static final DeferredItem<Item> ADAMANTIUM_INGOT = ITEMS.registerItem(
            "adamantium",
            properties -> new Item(properties.stacksTo(16))
    );
    public static final DeferredItem<Item> ANCIENT_METAL_INGOT = ITEMS.registerItem(
            "ancient_metal",
            properties -> new Item(properties.stacksTo(16))
    );
    public static final DeferredItem<Item> COPPER_INGOT = ITEMS.registerItem(
            "copper",
            properties -> new Item(properties.stacksTo(16))
    );
    public static final DeferredItem<Item> GOLD_INGOT = ITEMS.registerItem(
            "gold",
            properties -> new Item(properties.stacksTo(16))
    );
    public static final DeferredItem<Item> MITHRIL_INGOT = ITEMS.registerItem(
            "mithril",
            properties -> new Item(properties.stacksTo(16))
    );
    public static final DeferredItem<Item> SILVER_INGOT = ITEMS.registerItem(
            "silver",
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

    // ---------- 粗矿（堆叠 32）
    public static final DeferredItem<Item> RAW_ADAMANTIUM = ITEMS.registerItem(
            "adamantium_raw",
            properties -> new Item(properties.stacksTo(32))
    );
    public static final DeferredItem<Item> RAW_MITHRIL = ITEMS.registerItem(
            "mithril_raw",
            properties -> new Item(properties.stacksTo(32))
    );
    public static final DeferredItem<Item> RAW_SILVER = ITEMS.registerItem(
            "silver_raw",
            properties -> new Item(properties.stacksTo(32))
    );

    // ---------- 燧石（堆叠 64） ----------
    public static final DeferredItem<Item> FLINT = ITEMS.registerItem(
            "flint",
            properties -> new Item(properties.stacksTo(64))
    );
}