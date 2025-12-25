package com.equilibrium.item.food;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;

import java.util.List;
import java.util.Optional;

public class MashedPotato extends Item {
    public MashedPotato(Settings settings) {
        super(settings);
    }
    public static final FoodComponent MASHED_POTATO = new FoodComponent(8,14f,false,1.6F, Optional.of(new ItemStack(Items.BOWL)), List.of());
}
