package com.equilibrium.item.food;

import com.equilibrium.block.ModBlocks;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

import static com.equilibrium.OnServerInitialize.MOD_ID;


public class FoodOrFarmItems {

    public static final Item CHEESE= new Cheese(new Item.Settings().food(Cheese.CHEESE).maxCount(32));
    public static final Item MASHED_POTATO= new Cheese(new Item.Settings().food(MashedPotato.MASHED_POTATO).maxCount(16));
    public static final Item PUMPKIN_SOUP= new PumpkinSoup(new Item.Settings().food(PumpkinSoup.PUMPKIN_SOUP).maxCount(16));
    public static final Item WATER_BOWL= new WaterBowl(new Item.Settings().maxCount(16));
    public static final Item MILK_BOWL= new MilkBowl(new Item.Settings().food(MilkBowl.BOWL_MILK).maxCount(16));
    public static final Item BEEF_SOUP= new BeefSoup(new Item.Settings().food(BeefSoup.BEEF_SOUP).maxCount(16));
    public static final Item VEGETABLE_SOUP = new BeefSoup(new Item.Settings().food(VegetableSoup.VEGETABLE_SOUP).maxCount(16));
    public static final Item ONION = new AliasedBlockItem(ModBlocks.ONION_BLOCK,new Item.Settings().food(new FoodComponent(1,1f,false,1.6F,Optional.empty(), List.of())).maxCount(32));
    public static final Item MANURE = new ManureItem(new Item.Settings().maxCount(32));

    public static void registerFoodItems() {
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"pumpkin_soup"), PUMPKIN_SOUP);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"bowl_water"), WATER_BOWL);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"bowl_milk"), MILK_BOWL);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"beef_soup"), BEEF_SOUP);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"vegetable_soup"), VEGETABLE_SOUP);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"onion"), ONION);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"cheese"), CHEESE);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"mashed_potato"), MASHED_POTATO);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"manure"), MANURE);
    }

}
