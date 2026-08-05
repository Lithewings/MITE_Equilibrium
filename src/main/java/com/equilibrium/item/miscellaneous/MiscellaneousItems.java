package com.equilibrium.item.miscellaneous;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class MiscellaneousItems {

    // 使用 DeferredRegister.Items 创建物品注册器
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    // 使用 DeferredItem<Item> 字段注册每个物品
    public static final DeferredItem<Item> PUDDING_SLIME_BALL = ITEMS.register("pudding_slime_ball",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> GLASS_FRAGMENT = ITEMS.register("glass_fragment",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> DIAMOND_SHARD = ITEMS.register("diamond_shard",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> EMERALD_SHARD = ITEMS.register("emerald_shard",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> SINEW = ITEMS.register("sinew",
            () -> new Item(new Item.Properties().stacksTo(64)));
}