package com.equilibrium.item.food;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;

import java.util.List;
import java.util.Optional;

public class Chocolate extends Item {
    public Chocolate(Settings settings) {
        super(settings);
    }
    public static final FoodComponent CHOCOLATE = new FoodComponent(3,3f,false,1.6F, Optional.empty(), List.of());
}
