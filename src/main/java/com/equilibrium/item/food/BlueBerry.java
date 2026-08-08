package com.equilibrium.item.food;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;

import java.util.List;
import java.util.Optional;

public class BlueBerry extends Item {
    public BlueBerry(Settings settings) {
        super(settings);
    }
    public static final FoodComponent BLUE_BERRY = new FoodComponent(1,1f,false,1.6F, Optional.empty(), List.of());
}
