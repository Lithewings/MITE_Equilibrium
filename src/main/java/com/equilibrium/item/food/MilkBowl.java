package com.equilibrium.item.food;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class MilkBowl extends Item {
    public MilkBowl(Settings settings) {
        super(settings);
    }
    public static final FoodComponent BOWL_MILK = new FoodComponent(1,0f,false,1.6F, Optional.of(new ItemStack(Items.BOWL)), List.of());
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user.isSneaking()) {
            ItemStack itemStack = user.getStackInHand(hand);
            itemStack.setCount(itemStack.getCount() - 1);
            user.getInventory().offerOrDrop(Items.BOWL.getDefaultStack());
            return TypedActionResult.success(itemStack, world.isClient);
        }
        return super.use(world, user, hand);
    }

    @Override
    public SoundEvent getEatSound() {
        return this.getDrinkSound();
    }
}
