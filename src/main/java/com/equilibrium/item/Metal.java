package com.equilibrium.item;


import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class Metal {

    //以下开始添加物品:
    public static final Item adamantium= new Item(new Item.Settings());
    public static final Item ancient_metal = new Item(new Item.Settings());
    public static final Item copper = new Item(new Item.Settings());
    public static final Item gold = new Item(new Item.Settings());
    public static final Item mithril = new Item(new Item.Settings());
    public static final Item silver = new Item(new Item.Settings());

    //maxCount = 32
    public static final Item adamantium_nugget= new Item(new Item.Settings().maxCount(64));
    public static final Item ancient_metal_nugget= new Item(new Item.Settings().maxCount(64));
    public static final Item copper_nugget = new Item(new Item.Settings().maxCount(64));
    public static final Item gold_nugget = new Item(new Item.Settings().maxCount(64));
    public static final Item mithril_nugget = new Item(new Item.Settings().maxCount(64));
    public static final Item silver_nugget = new Item(new Item.Settings().maxCount(64));


    public static final Item FLINT = new Item(new Item.Settings().maxCount(64));

    //maxCount = 16
    public static final Item ADAMANTIUM_RAW= new Item(new Item.Settings().maxCount(32));
    public static final Item MITHRIL_RAW = new Item(new Item.Settings().maxCount(32));
    public static final Item SILVER_RAW = new Item(new Item.Settings().maxCount(32));








    public static void registerModItemIngots() {
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"adamantium"), adamantium);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"ancient_metal"), ancient_metal);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"copper"), copper);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"gold"), gold);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"mithril"), mithril);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"silver"), silver);

        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"flint"), FLINT);
    }
    public static void registerModItemNuggets(){
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"adamantium_nugget"), adamantium_nugget);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"ancient_metal_nugget"), ancient_metal_nugget);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"copper_nugget"), copper_nugget);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"gold_nugget"), gold_nugget);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"mithril_nugget"), mithril_nugget);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"silver_nugget"), silver_nugget);

    }
    public static void registerModItemRaw(){
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"adamantium_raw"), ADAMANTIUM_RAW);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"mithril_raw"), MITHRIL_RAW);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"silver_raw"), SILVER_RAW);

    }




}
