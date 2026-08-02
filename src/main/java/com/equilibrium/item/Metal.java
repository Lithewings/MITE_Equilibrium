package com.equilibrium.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import static com.equilibrium.OnServerInitialize.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class Metal {

    // 锭
    public static Item adamantium;
    public static Item ancient_metal;
    public static Item copper;
    public static Item gold;
    public static Item mithril;
    public static Item silver;

    // 粒
    public static Item adamantium_nugget;
    public static Item ancient_metal_nugget;
    public static Item copper_nugget;
    public static Item gold_nugget;
    public static Item mithril_nugget;
    public static Item silver_nugget;

    // 其他
    public static Item FLINT;

    // 粗矿
    public static Item ADAMANTIUM_RAW;
    public static Item MITHRIL_RAW;
    public static Item SILVER_RAW;

    @SubscribeEvent
    public static void registerItems(RegisterEvent event) {
        event.register(BuiltInRegistries.ITEM.key(), helper -> {
            // 锭
            adamantium = new Item(new Item.Properties().stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "adamantium"), adamantium);

            ancient_metal = new Item(new Item.Properties().stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "ancient_metal"), ancient_metal);

            copper = new Item(new Item.Properties().stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "copper"), copper);

            gold = new Item(new Item.Properties().stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "gold"), gold);

            mithril = new Item(new Item.Properties().stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "mithril"), mithril);

            silver = new Item(new Item.Properties().stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "silver"), silver);

            FLINT = new Item(new Item.Properties().stacksTo(64));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "flint"), FLINT);

            // 粒
            adamantium_nugget = new Item(new Item.Properties().stacksTo(64));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "adamantium_nugget"), adamantium_nugget);

            ancient_metal_nugget = new Item(new Item.Properties().stacksTo(64));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "ancient_metal_nugget"), ancient_metal_nugget);

            copper_nugget = new Item(new Item.Properties().stacksTo(64));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "copper_nugget"), copper_nugget);

            gold_nugget = new Item(new Item.Properties().stacksTo(64));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "gold_nugget"), gold_nugget);

            mithril_nugget = new Item(new Item.Properties().stacksTo(64));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "mithril_nugget"), mithril_nugget);

            silver_nugget = new Item(new Item.Properties().stacksTo(64));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "silver_nugget"), silver_nugget);

            // 粗矿
            ADAMANTIUM_RAW = new Item(new Item.Properties().stacksTo(32));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "adamantium_raw"), ADAMANTIUM_RAW);

            MITHRIL_RAW = new Item(new Item.Properties().stacksTo(32));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "mithril_raw"), MITHRIL_RAW);

            SILVER_RAW = new Item(new Item.Properties().stacksTo(32));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "silver_raw"), SILVER_RAW);
        });
    }
}