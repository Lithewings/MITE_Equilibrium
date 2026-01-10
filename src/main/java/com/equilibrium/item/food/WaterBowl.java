package com.equilibrium.item.food;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class WaterBowl extends Item {
    public WaterBowl(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user.isOnFire()){
            user.extinguish();
            world.playSoundAtBlockCenter(user.getBlockPos(),SoundEvents.BLOCK_FIRE_EXTINGUISH,SoundCategory.PLAYERS,1f,1f,false);
        }
        ItemStack itemStack = user.getStackInHand(hand);
        if(itemStack.getCount()==1){
            itemStack = Items.BOWL.getDefaultStack();
            return TypedActionResult.success(itemStack,world.isClient);
        }
        user.getInventory().offerOrDrop(Items.BOWL.getDefaultStack());
        itemStack.setCount(itemStack.getCount() - 1);
        return TypedActionResult.success(itemStack,world.isClient);

    }

    public static TypedActionResult<ItemStack> vanillaBowlItemUse(World world, PlayerEntity user, Hand hand, ItemStack itemStack) {

        BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(itemStack);
        } else {
            if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (!world.canPlayerModifyAt(user, blockPos)) {
                    return TypedActionResult.pass(itemStack);
                }
                if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                    //与水交互
                    world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                    world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos);
                    //减一
                    itemStack.setCount(itemStack.getCount() - 1);
                    //加一
                    user.getInventory().insertStack(FoodOrFarmItems.WATER_BOWL.getDefaultStack());
                    //只是增加一次使用次数而已
                    user.incrementStat(Stats.USED.getOrCreateStat(FoodOrFarmItems.WATER_BOWL));
                    return TypedActionResult.success(itemStack, world.isClient());
                }
            }

            return TypedActionResult.pass(itemStack);
        }
    }


}