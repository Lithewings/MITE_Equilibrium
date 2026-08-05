package com.equilibrium.item.food;

import com.equilibrium.block.ModBlocksRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class FoodItems {

        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

        public static final DeferredItem<Item> SALAD = ITEMS.register("salad",
                () -> new Salad(new Item.Properties().food(Salad.SALAD).stacksTo(16)));

        public static final DeferredItem<Item> PUMPKIN_SOUP = ITEMS.register("pumpkin_soup",
                () -> new PumpkinSoup(new Item.Properties().food(PumpkinSoup.PUMPKIN_SOUP).stacksTo(16)));

        public static final DeferredItem<Item> WATER_BOWL = ITEMS.register("bowl_water",
                () -> new WaterBowl(new Item.Properties().stacksTo(16)));

        public static final DeferredItem<Item> MILK_BOWL = ITEMS.register("bowl_milk",
                () -> new MilkBowl(new Item.Properties()
                        .food(MilkBowl.BOWL_MILK)
                        .stacksTo(16)
                        .craftRemainder(Items.BOWL)));

        public static final DeferredItem<Item> BEEF_SOUP = ITEMS.register("beef_soup",
                () -> new BeefSoup(new Item.Properties().food(BeefSoup.BEEF_SOUP).stacksTo(16)));

        public static final DeferredItem<Item> VEGETABLE_SOUP = ITEMS.register("vegetable_soup",
                () -> new VegetableSoup(new Item.Properties().food(VegetableSoup.VEGETABLE_SOUP).stacksTo(16)));

        public static final DeferredItem<Item> ONION = ITEMS.register("onion",
                () -> new ItemNameBlockItem(ModBlocksRegistry.ONION_BLOCK,
                        new Item.Properties()
                                .food(new FoodProperties(1, 1f, false, 1.6F, Optional.empty(), List.of()))
                                .stacksTo(32)));

        public static final DeferredItem<Item> CHEESE = ITEMS.register("cheese",
                () -> new Cheese(new Item.Properties().food(Cheese.CHEESE).stacksTo(16)));

        public static final DeferredItem<Item> MASHED_POTATO = ITEMS.register("mashed_potato",
                () -> new Cheese(new Item.Properties().food(MashedPotato.MASHED_POTATO).stacksTo(16)));

        public static final DeferredItem<Item> MANURE = ITEMS.register("manure",
                () -> new ManureItem(new Item.Properties().stacksTo(16)));

        public static final DeferredItem<Item> CHOCOLATE = ITEMS.register("chocolate",
                () -> new Chocolate(new Item.Properties().food(Chocolate.CHOCOLATE).stacksTo(16)));
}