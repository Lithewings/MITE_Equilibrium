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
public class OtherItems {

    // 只声明字段，不在此处实例化
    public static Item PUDDING_SLIME_BALL;
    public static Item GLASS_FRAGMENT;
    public static Item DIAMOND_SHARD;
    public static Item EMERALD_SHARD;
    public static Item SINEW;

    @SubscribeEvent
    public static void registerItems(RegisterEvent event) {
        event.register(BuiltInRegistries.ITEM.key(), helper -> {
            PUDDING_SLIME_BALL = new Item(new Item.Properties().stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "pudding_slime_ball"),
                    PUDDING_SLIME_BALL);

            GLASS_FRAGMENT = new Item(new Item.Properties().stacksTo(64));
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "glass_fragment"),
                    GLASS_FRAGMENT);

            DIAMOND_SHARD = new Item(new Item.Properties().stacksTo(64));
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "diamond_shard"),
                    DIAMOND_SHARD);

            EMERALD_SHARD = new Item(new Item.Properties().stacksTo(64));
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "emerald_shard"),
                    EMERALD_SHARD);

            SINEW = new Item(new Item.Properties().stacksTo(64));
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "sinew"),
                    SINEW);
        });
    }

}