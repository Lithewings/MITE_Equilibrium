package com.equilibrium.item.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;

public class Blueberry extends Item {
    public Blueberry(Properties settings) {
        super(settings);
    }
    public static final FoodProperties BLUEBERRY = new FoodProperties(1,1f,false,1.6F, Optional.empty(), List.of());
}
