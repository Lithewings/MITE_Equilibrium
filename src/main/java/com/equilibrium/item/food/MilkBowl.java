package com.equilibrium.item.food;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class MilkBowl extends Item {
    public MilkBowl(Properties settings) {
        super(settings);
    }
    public static final FoodProperties BOWL_MILK = new FoodProperties(1,0f,false,1.6F, Optional.of(new ItemStack(Items.BOWL)), List.of());
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if(user.isShiftKeyDown()) {
            ItemStack itemStack = user.getItemInHand(hand);
            if(itemStack.getCount()==1){
                itemStack = Items.BOWL.getDefaultInstance();
                return InteractionResultHolder.sidedSuccess(itemStack,world.isClientSide);
            }
            user.getInventory().placeItemBackInInventory(Items.BOWL.getDefaultInstance());
            itemStack.setCount(itemStack.getCount() - 1);
            return InteractionResultHolder.sidedSuccess(itemStack,world.isClientSide);
        }
        return super.use(world, user, hand);
    }

    @Override
    public SoundEvent getEatingSound() {
        return this.getDrinkingSound();
    }
}
