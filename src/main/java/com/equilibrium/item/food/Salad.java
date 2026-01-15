package com.equilibrium.item.food;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Optional;

public class Salad extends Item {
    public Salad(Settings settings) {
        super(settings);
    }
    public static final FoodComponent SALAD = new FoodComponent(3,3f,false,1.6F, Optional.of(new ItemStack(Items.BOWL)), List.of());
}
