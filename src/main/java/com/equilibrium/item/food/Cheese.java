package com.equilibrium.item.food;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Optional;

public class Cheese extends Item {
    public Cheese(Settings settings) {
        super(settings);
    }
    public static final FoodComponent CHEESE = new FoodComponent(3,3f,false,1.6F, Optional.empty(), List.of());
}
