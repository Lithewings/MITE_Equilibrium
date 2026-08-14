package com.equilibrium.mixin.vanilla_itemsmixin;

import com.equilibrium.OnServerInitialize;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.component.DataComponentTypes.ENCHANTMENTS;
import static net.minecraft.item.BucketItem.getEmptiedStack;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item implements FluidModificationItem {
    public BucketItemMixin(Settings settings) {
        super(settings);
    }
    @Shadow
    @Final
    private Fluid fluid;
    @Override
    public int getEnchantability() {
        return 25;
    }
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Inject(method = "getEmptiedStack",at = @At("HEAD"),cancellable = true)
    private static void getEmptiedStackMixin(ItemStack stack, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
        cir.cancel();
        // 保证返回的空水桶保持附魔
        ItemStack bucket = new ItemStack(Items.BUCKET);
        bucket.set(ENCHANTMENTS,stack.getEnchantments());
        cir.setReturnValue(!player.isInCreativeMode() ? bucket : stack);
    }


//    @Override
//    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
//        if(!user.getWorld().isClient()){
//            user.sendMessage(Text.of("stack的附魔为"+stack.get(ENCHANTMENTS)));
//            user.sendMessage(Text.of("玩家主手的附魔为"+user.getMainHandStack().get(ENCHANTMENTS)));
//        }
////        stack.set(ENCHANTMENTS,user.getMainHandStack().getEnchantments());
//        return ActionResult.PASS;
//    }


    @Inject(method = "use",at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/BlockHitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;",ordinal = 0),cancellable = true)
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        cir.cancel();
        //itemStack : 手里的桶物品
        ItemStack itemStack = user.getStackInHand(hand);


        if(world.isClient)
            cir.setReturnValue(TypedActionResult.pass(itemStack));


        //若想修改对生物实体(比如美西螈)的use,去生物那边修改,这个use是放在方块上的use





        BlockHitResult blockHitResult = raycast(
                world, user, this.fluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE
        );
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            cir.setReturnValue(TypedActionResult.pass(itemStack));
        } else if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            cir.setReturnValue(TypedActionResult.pass(itemStack));
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getSide();
            BlockPos blockPos2 = blockPos.offset(direction);
            if (!world.canPlayerModifyAt(user, blockPos) || !user.canPlaceOn(blockPos2, direction, itemStack)) {
                cir.setReturnValue(TypedActionResult.fail(itemStack));
            } else if (this.fluid == Fluids.EMPTY) {
                //空桶盛液体
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.getBlock() instanceof FluidDrainable fluidDrainable) {
                    ItemStack itemStack2 = fluidDrainable.tryDrainFluid(user, world, blockPos, blockState);
                    if (!itemStack2.isEmpty()) {
                        user.incrementStat(Stats.USED.getOrCreateStat(this));
                        fluidDrainable.getBucketFillSound().ifPresent(sound -> user.playSound(sound, 1.0F, 1.0F));
                        world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos);

                        //将试图盛起来液体的桶打上之前的附魔
                        itemStack2.set(ENCHANTMENTS,itemStack.getEnchantments());

                        ItemStack itemStack3 = ItemUsage.exchangeStack(itemStack, user, itemStack2);
                        if (!world.isClient) {
                            Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)user, itemStack2);
                        }

                        cir.setReturnValue(TypedActionResult.success(itemStack3, world.isClient()));
                    }
                    OnServerInitialize.LOGGER.error("tryDrainFluid method return a empty value rather ItemStack.Empty");
                }
                else
                    cir.setReturnValue(TypedActionResult.fail(itemStack));
            }
            else {
                //满桶释放
                BlockState blockState = world.getBlockState(blockPos);
                BlockPos blockPos3 = blockState.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER ? blockPos : blockPos2;
                if (this.placeFluid(user, world, blockPos3, blockHitResult)) {
                    this.onEmptied(user, world, itemStack, blockPos3);
                    if (user instanceof ServerPlayerEntity) {
                        Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)user, blockPos3, itemStack);
                    }
                    user.incrementStat(Stats.USED.getOrCreateStat(this));
                    //保留附魔的逻辑交给getEmptiedStack
                    ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, user, getEmptiedStack(itemStack, user));

                    cir.setReturnValue( TypedActionResult.success(itemStack2, world.isClient()));
                } else {
                    cir.setReturnValue( TypedActionResult.fail(itemStack));
                }
            }
        }
    }






    @Shadow
    protected void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
        SoundEvent soundEvent = this.fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
        world.playSound(player, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(player, GameEvent.FLUID_PLACE, pos);
    }

    @Inject(method = "placeFluid",at = @At("HEAD"), cancellable = true)
    public void placeFluid(PlayerEntity player, World world, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        if (!(this.fluid instanceof FlowableFluid flowableFluid)) {
            cir.setReturnValue(false);
        } else {
            Block block;
            boolean bl;
            BlockState blockState;
            boolean var10000;
            label82: {
                blockState = world.getBlockState(pos);
                block = blockState.getBlock();
                bl = blockState.canBucketPlace(this.fluid);
                label70:
                if (!blockState.isAir() && !bl) {
                    if (block instanceof FluidFillable fluidFillable && fluidFillable.canFillWithFluid(player, world, pos, blockState, this.fluid)) {
                        break label70;
                    }

                    var10000 = false;
                    break label82;
                }

                var10000 = true;
            }

            boolean bl2 = var10000;
            if (!bl2) {
                cir.setReturnValue(hitResult != null && this.placeFluid(player, world, hitResult.getBlockPos().offset(hitResult.getSide()), null));
            }
            //地狱蒸发
            else if (world.getDimension().ultrawarm() && this.fluid.isIn(FluidTags.WATER)) {
                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();
                world.playSound(
                        player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
                );

                for (int l = 0; l < 8; l++) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
                }
                cir.setReturnValue(true);
            } else {
                if (block instanceof FluidFillable fluidFillable && this.fluid == Fluids.WATER) {
                    fluidFillable.tryFillWithFluid(world, pos, blockState, flowableFluid.getStill(false));
                    this.playEmptyingSound(player, world, pos);
                    cir.setReturnValue(true);
                }

                if (!world.isClient && bl && !blockState.isLiquid()) {
                    world.breakBlock(pos, true);
                }

                if (!world.setBlockState(pos, this.fluid.getDefaultState().getBlockState(), Block.NOTIFY_ALL_AND_REDRAW) && !blockState.getFluidState().isStill()) {
                    cir.setReturnValue(false);
                } else {
                    this.playEmptyingSound(player, world, pos);
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
