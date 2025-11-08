package com.equilibrium.item.food;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.List;
import java.util.Optional;

public class BeefSoup extends Item {
    public BeefSoup(Settings settings) {
        super(settings);
    }

    public static final FoodComponent BEEF_SOUP = new FoodComponent(20,20f,false,1.6F, Optional.of(new ItemStack(Items.BOWL)), List.of(new FoodComponent.StatusEffectEntry(new StatusEffectInstance(StatusEffects.STRENGTH,2000),1)));

}
