package com.equilibrium.item.food;

import com.equilibrium.block.ModBlocksRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.List;
import java.util.Optional;

import static com.equilibrium.OnServerInitialize.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class FoodOrFarmItems {

    // 只声明字段，不在此处实例化
    public static Item SALAD;
    public static Item CHEESE;
    public static Item MASHED_POTATO;
    public static Item PUMPKIN_SOUP;
    public static Item WATER_BOWL;
    public static Item MILK_BOWL;
    public static Item BEEF_SOUP;
    public static Item VEGETABLE_SOUP;
    public static Item ONION;
    public static Item MANURE;
    public static Item CHOCOLATE;

    @SubscribeEvent
    public static void registerFoodItems(RegisterEvent event) {
        event.register(BuiltInRegistries.ITEM.key(), helper -> {
            // 此时方块已注册完毕，可以安全引用 ModBlocksRegistry.ONION_BLOCK
            SALAD = new Salad(new Item.Properties().food(Salad.SALAD).stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "salad"), SALAD);

            PUMPKIN_SOUP = new PumpkinSoup(new Item.Properties().food(PumpkinSoup.PUMPKIN_SOUP).stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "pumpkin_soup"), PUMPKIN_SOUP);

            WATER_BOWL = new WaterBowl(new Item.Properties().stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "bowl_water"), WATER_BOWL);

            MILK_BOWL = new MilkBowl(new Item.Properties().food(MilkBowl.BOWL_MILK).stacksTo(16).craftRemainder(Items.BOWL));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "bowl_milk"), MILK_BOWL);

            BEEF_SOUP = new BeefSoup(new Item.Properties().food(BeefSoup.BEEF_SOUP).stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "beef_soup"), BEEF_SOUP);

            VEGETABLE_SOUP = new BeefSoup(new Item.Properties().food(VegetableSoup.VEGETABLE_SOUP).stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "vegetable_soup"), VEGETABLE_SOUP);

            ONION = new ItemNameBlockItem(ModBlocksRegistry.ONION_BLOCK,
                    new Item.Properties().food(new FoodProperties(1, 1f, false, 1.6F, Optional.empty(), List.of())).stacksTo(32));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "onion"), ONION);

            CHEESE = new Cheese(new Item.Properties().food(Cheese.CHEESE).stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "cheese"), CHEESE);

            MASHED_POTATO = new Cheese(new Item.Properties().food(MashedPotato.MASHED_POTATO).stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "mashed_potato"), MASHED_POTATO);

            MANURE = new ManureItem(new Item.Properties().stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "manure"), MANURE);

            CHOCOLATE = new Chocolate(new Item.Properties().food(Chocolate.CHOCOLATE).stacksTo(16));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "chocolate"), CHOCOLATE);
        });
    }
}