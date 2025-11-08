package com.equilibrium.item.food;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Optional;

public class VegetableSoup extends Item {
    public VegetableSoup(Settings settings) {
        super(settings);
    }

    public static final FoodComponent VEGETABLE_SOUP = new FoodComponent(6,8f,false,1.6F, Optional.of(new ItemStack(Items.BOWL)), List.of());

}
