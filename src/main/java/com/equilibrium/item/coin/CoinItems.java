package com.equilibrium.item.coin;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.item.material.MaterialItems;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CoinItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OnServerInitialize.MOD_ID);

    public static final int COPPER_COIN_EXPERIENCE_COST = 160;
    public static final int IRON_COIN_EXPERIENCE_COST = 640;

    // 使用 registerItem，返回类型即为 DeferredItem<BaseCoinItem>
    public static final DeferredItem<BaseCoinItem> COPPER_COIN = ITEMS.registerItem("copper_coin",
            properties -> new BaseCoinItem(properties, COPPER_COIN_EXPERIENCE_COST, MaterialItems.COPPER_NUGGET.get()));

    public static final DeferredItem<BaseCoinItem> IRON_COIN = ITEMS.registerItem("iron_coin",
            properties -> new BaseCoinItem(properties, IRON_COIN_EXPERIENCE_COST, Items.IRON_NUGGET));



    public static void deferredRegisterLoadCoinItems(){}


}