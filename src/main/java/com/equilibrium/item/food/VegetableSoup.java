package com.equilibrium.item.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public class VegetableSoup extends Item {
    public VegetableSoup(Properties settings) {
        super(settings);
    }

    public static final FoodProperties VEGETABLE_SOUP = new FoodProperties(6,8f,false,1.6F, Optional.of(new ItemStack(Items.BOWL)), List.of());

}
