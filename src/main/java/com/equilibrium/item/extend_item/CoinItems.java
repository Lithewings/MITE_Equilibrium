package com.equilibrium.item.extend_item;

import com.equilibrium.item.Metal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class CoinItems {
    public static final int COPPER_COIN_EXPERIENCE_COST = 160;
    public static final int IRON_COIN_EXPERIENCE_COST = 640;


    public static final Item COPPER_COIN = new BaseCoinItem(new Item.Settings(),COPPER_COIN_EXPERIENCE_COST,Metal.copper_nugget);
    public static final Item IRON_COIN = new BaseCoinItem(new Item.Settings(),IRON_COIN_EXPERIENCE_COST, Items.IRON_NUGGET);




    public static void registerCoinItems() {
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"copper_coin"),COPPER_COIN);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"iron_coin"),IRON_COIN);

    }

}
