package com.equilibrium.mixin.vanilla_itemsmixin;

import com.equilibrium.OnServerInitialize;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.core.component.DataComponents.ENCHANTMENTS;
import static net.minecraft.world.item.BucketItem.getEmptySuccessItem;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item implements DispensibleContainerItem {
    public BucketItemMixin(Properties settings) {
        super(settings);
    }
    @Shadow
    @Final
    private Fluid content;
    @Override
    public int getEnchantmentValue() {
        return 25;
    }
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
        return false;
    }

    @Inject(method = "getEmptySuccessItem",at = @At("HEAD"),cancellable = true)
    private static void getEmptiedStackMixin(ItemStack stack, Player player, CallbackInfoReturnable<ItemStack> cir) {
        cir.cancel();
        // 保证返回的空水桶保持附魔
        ItemStack bucket = new ItemStack(Items.BUCKET);
        bucket.set(ENCHANTMENTS,stack.getEnchantments());
        cir.setReturnValue(!player.hasInfiniteMaterials() ? bucket : stack);
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


    @Inject(method = "use",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/BlockHitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;",ordinal = 0),cancellable = true)
    public void use(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        cir.cancel();
        //itemStack : 手里的桶物品
        ItemStack itemStack = user.getItemInHand(hand);


        if(world.isClientSide)
            cir.setReturnValue(InteractionResultHolder.pass(itemStack));


        //若想修改对生物实体(比如美西螈)的use,去生物那边修改,这个use是放在方块上的use





        BlockHitResult blockHitResult = getPlayerPOVHitResult(
                world, user, this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE
        );
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            cir.setReturnValue(InteractionResultHolder.pass(itemStack));
        } else if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            cir.setReturnValue(InteractionResultHolder.pass(itemStack));
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos blockPos2 = blockPos.relative(direction);
            if (!world.mayInteract(user, blockPos) || !user.mayUseItemAt(blockPos2, direction, itemStack)) {
                cir.setReturnValue(InteractionResultHolder.fail(itemStack));
            } else if (this.content == Fluids.EMPTY) {
                //空桶盛液体
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.getBlock() instanceof BucketPickup fluidDrainable) {
                    ItemStack itemStack2 = fluidDrainable.pickupBlock(user, world, blockPos, blockState);
                    if (!itemStack2.isEmpty()) {
                        user.awardStat(Stats.ITEM_USED.get(this));
                        fluidDrainable.getPickupSound().ifPresent(sound -> user.playSound(sound, 1.0F, 1.0F));
                        world.gameEvent(user, GameEvent.FLUID_PICKUP, blockPos);

                        //将试图盛起来液体的桶打上之前的附魔
                        itemStack2.set(ENCHANTMENTS,itemStack.getEnchantments());

                        ItemStack itemStack3 = ItemUtils.createFilledResult(itemStack, user, itemStack2);
                        if (!world.isClientSide) {
                            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)user, itemStack2);
                        }

                        cir.setReturnValue(InteractionResultHolder.sidedSuccess(itemStack3, world.isClientSide()));
                    }
                    OnServerInitialize.LOGGER.error("tryDrainFluid method return a empty value rather ItemStack.Empty");
                }
                else
                    cir.setReturnValue(InteractionResultHolder.fail(itemStack));
            }
            else {
                //满桶释放
                BlockState blockState = world.getBlockState(blockPos);
                BlockPos blockPos3 = blockState.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER ? blockPos : blockPos2;
                if (this.emptyContents(user, world, blockPos3, blockHitResult)) {
                    this.checkExtraContent(user, world, itemStack, blockPos3);
                    if (user instanceof ServerPlayer) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)user, blockPos3, itemStack);
                    }
                    user.awardStat(Stats.ITEM_USED.get(this));
                    //保留附魔的逻辑交给getEmptiedStack
                    ItemStack itemStack2 = ItemUtils.createFilledResult(itemStack, user, getEmptySuccessItem(itemStack, user));

                    cir.setReturnValue( InteractionResultHolder.sidedSuccess(itemStack2, world.isClientSide()));
                } else {
                    cir.setReturnValue( InteractionResultHolder.fail(itemStack));
                }
            }
        }
    }






    @Shadow
    protected void playEmptySound(@Nullable Player player, LevelAccessor world, BlockPos pos) {
        SoundEvent soundEvent = this.content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        world.playSound(player, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
        world.gameEvent(player, GameEvent.FLUID_PLACE, pos);
    }

    @Inject(method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",at = @At("HEAD"), cancellable = true)
    public void emptyContents(Player player, Level world, BlockPos pos, BlockHitResult hitResult, ItemStack container, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        if (!(this.content instanceof FlowingFluid flowableFluid)) {
            cir.setReturnValue(false);
        } else {
            Block block;
            boolean bl;
            BlockState blockState;
            boolean var10000;
            label82: {
                blockState = world.getBlockState(pos);
                block = blockState.getBlock();
                bl = blockState.canBeReplaced(this.content);
                label70:
                if (!blockState.isAir() && !bl) {
                    if (block instanceof LiquidBlockContainer fluidFillable && fluidFillable.canPlaceLiquid(player, world, pos, blockState, this.content)) {
                        break label70;
                    }

                    var10000 = false;
                    break label82;
                }

                var10000 = true;
            }

            boolean bl2 = var10000;
            if (!bl2) {
                cir.setReturnValue(hitResult != null && this.emptyContents(player, world, hitResult.getBlockPos().relative(hitResult.getDirection()), null));
            }
            //地狱蒸发
            else if (world.dimensionType().ultraWarm() && this.content.is(FluidTags.WATER)) {
                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();
                world.playSound(
                        player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
                );

                for (int l = 0; l < 8; l++) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
                }
                cir.setReturnValue(true);
            } else {
                if (block instanceof LiquidBlockContainer fluidFillable && this.content == Fluids.WATER) {
                    fluidFillable.placeLiquid(world, pos, blockState, flowableFluid.getSource(false));
                    this.playEmptySound(player, world, pos);
                    cir.setReturnValue(true);
                }

                if (!world.isClientSide && bl && !blockState.liquid()) {
                    world.destroyBlock(pos, true);
                }

                if (!world.setBlock(pos, this.content.defaultFluidState().createLegacyBlock(), Block.UPDATE_ALL_IMMEDIATE) && !blockState.getFluidState().isSource()) {
                    cir.setReturnValue(false);
                } else {
                    this.playEmptySound(player, world, pos);
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
