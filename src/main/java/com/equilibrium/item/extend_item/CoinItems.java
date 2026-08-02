package com.equilibrium.item.extend_item;

import com.equilibrium.item.Metal;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import static com.equilibrium.OnServerInitialize.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class CoinItems {

    public static final int COPPER_COIN_EXPERIENCE_COST = 160;
    public static final int IRON_COIN_EXPERIENCE_COST = 640;

    // 只声明，不在此处创建实例
    public static Item COPPER_COIN;
    public static Item IRON_COIN;

    @SubscribeEvent
    public static void registerCoinItems(RegisterEvent event) {
        event.register(BuiltInRegistries.ITEM.key(), helper -> {
            // 此时 Metal.copper_nugget 等已经注册完成，可安全使用
            COPPER_COIN = new BaseCoinItem(new Item.Properties(), COPPER_COIN_EXPERIENCE_COST, Metal.copper_nugget);
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "copper_coin"),
                    COPPER_COIN);

            IRON_COIN = new BaseCoinItem(new Item.Properties(), IRON_COIN_EXPERIENCE_COST, Items.IRON_NUGGET);
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "iron_coin"),
                    IRON_COIN);
        });
    }
}