package com.equilibrium.item.extend_item;

import com.equilibrium.item.MaterialItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.item.Items.ITEMS;

public class CoinItems {



    public static final int COPPER_COIN_EXPERIENCE_COST = 160;
    public static final int IRON_COIN_EXPERIENCE_COST = 640;

    // 使用 registerItem，返回类型即为 DeferredItem<BaseCoinItem>
    public static final DeferredItem<BaseCoinItem> COPPER_COIN = ITEMS.registerItem("copper_coin",
            properties -> new BaseCoinItem(properties, COPPER_COIN_EXPERIENCE_COST, MaterialItems.COPPER_NUGGET.get()));

    public static final DeferredItem<BaseCoinItem> IRON_COIN = ITEMS.registerItem("iron_coin",
            properties -> new BaseCoinItem(properties, IRON_COIN_EXPERIENCE_COST, Items.IRON_NUGGET));



    public static void deferredRegisterLoadCoinItems(){}


}