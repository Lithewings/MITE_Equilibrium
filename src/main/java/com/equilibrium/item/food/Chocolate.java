package com.equilibrium.item.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;

public class Chocolate extends Item {
    public Chocolate(Properties settings) {
        super(settings);
    }
    public static final FoodProperties CHOCOLATE = new FoodProperties(3,3f,false,1.6F, Optional.empty(), List.of());
}
