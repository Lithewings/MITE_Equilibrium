package com.equilibrium.item.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public class BeefSoup extends Item {
    public BeefSoup(Properties settings) {
        super(settings);
    }

    public static final FoodProperties BEEF_SOUP = new FoodProperties(20,20f,false,1.6F, Optional.of(new ItemStack(Items.BOWL)),List.of(
            new FoodProperties.PossibleEffect(
            () -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2000), 1F
            )));

}
