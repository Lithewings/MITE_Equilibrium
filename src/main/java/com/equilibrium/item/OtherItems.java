package com.equilibrium.item;


import com.equilibrium.item.food.ManureItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class OtherItems {

//    public static final Item test = new Item(new Item.Settings());
//
    //黑色史莱姆粘液球
    public static final Item PUDDING_SLIME_BALL = new Item(new Item.Settings().maxCount(16));




























    public static void registerModItems() {
//        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"test"), test);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"pudding_slime_ball"), PUDDING_SLIME_BALL);





    }
}

