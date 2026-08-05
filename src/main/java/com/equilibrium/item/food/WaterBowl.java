package com.equilibrium.item.food;

import com.equilibrium.item.material.MaterialItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class WaterBowl extends Item {
    public WaterBowl(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if(user.isOnFire()){
            user.clearFire();
            world.playLocalSound(user.blockPosition(),SoundEvents.FIRE_EXTINGUISH,SoundSource.PLAYERS,1f,1f,false);
        }
        ItemStack itemStack = user.getItemInHand(hand);
        if(itemStack.getCount()==1){
            itemStack = Items.BOWL.getDefaultInstance();
            return InteractionResultHolder.sidedSuccess(itemStack,world.isClientSide);
        }
        user.getInventory().placeItemBackInInventory(Items.BOWL.getDefaultInstance());
        itemStack.setCount(itemStack.getCount() - 1);
        return InteractionResultHolder.sidedSuccess(itemStack,world.isClientSide);

    }

    public static InteractionResultHolder<ItemStack> vanillaBowlItemUse(Level world, Player user, InteractionHand hand, ItemStack itemStack) {

        BlockHitResult blockHitResult = getPlayerPOVHitResult(world, user, ClipContext.Fluid.SOURCE_ONLY);
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemStack);
        } else {
            if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (!world.mayInteract(user, blockPos)) {
                    return InteractionResultHolder.pass(itemStack);
                }
                if (world.getFluidState(blockPos).is(FluidTags.WATER)) {
                    //与水交互
                    world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    world.gameEvent(user, GameEvent.FLUID_PICKUP, blockPos);
                    //减一
                    itemStack.setCount(itemStack.getCount() - 1);
                    //加一
                    user.getInventory().add(FoodItems.WATER_BOWL.get().getDefaultInstance());
                    //只是增加一次使用次数而已
                    user.awardStat(Stats.ITEM_USED.get(FoodItems.WATER_BOWL.get()));
                    return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
                }
            }

            return InteractionResultHolder.pass(itemStack);
        }
    }


}