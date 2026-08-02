package com.equilibrium.item.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public class MashedPotato extends Item {
    public MashedPotato(Properties settings) {
        super(settings);
    }
    public static final FoodProperties MASHED_POTATO = new FoodProperties(8,14f,false,1.6F, Optional.of(new ItemStack(Items.BOWL)), List.of());
}
