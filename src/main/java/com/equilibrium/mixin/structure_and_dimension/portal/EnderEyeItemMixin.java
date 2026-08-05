package com.equilibrium.mixin.structure_and_dimension.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderEyeItem.class)
public abstract class EnderEyeItemMixin extends Item {
    public EnderEyeItemMixin(Properties settings) {
        super(settings);
    }

    @Inject(
            method = "use",   // 目标方法名
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;findNearestMapStructure(Lnet/minecraft/tags/TagKey;Lnet/minecraft/core/BlockPos;IZ)Lnet/minecraft/core/BlockPos;"
            ),
            cancellable = true)
    private void redirectLocateStructure(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {

        if (world instanceof ServerLevel serverWorld) {
            BlockPos blockPos = serverWorld.findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, user.blockPosition(), 100, false);
            if (blockPos!=null && (Math.abs(blockPos.getX()) >= 12000 || Math.abs(blockPos.getZ()) >= 12000))
                return;
            else{
                user.displayClientMessage(Component.nullToEmpty("The eye lost its navigation."),true);
                cir.setReturnValue(InteractionResultHolder.consume(user.getItemInHand(hand)));
            }
        }



    }

//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
//        ItemStack itemStack = user.getStackInHand(hand);
//        BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.NONE);
//        if (blockHitResult.getType() == HitResult.Type.BLOCK && world.getBlockState(blockHitResult.getBlockPos()).isOf(Blocks.END_PORTAL_FRAME)) {
//            return TypedActionResult.pass(itemStack);
//        } else {
//            user.setCurrentHand(hand);
//            if (world instanceof ServerWorld serverWorld) {
//                BlockPos blockPos = serverWorld.locateStructure(StructureTags.EYE_OF_ENDER_LOCATED, user.getBlockPos(), 300, false);
//                if (blockPos != null && (Math.abs(blockPos.getX()) >= 12000 || Math.abs(blockPos.getZ()) >= 12000)){
//                    EyeOfEnderEntity eyeOfEnderEntity = new EyeOfEnderEntity(world, user.getX(), user.getBodyY(0.5), user.getZ());
//                    eyeOfEnderEntity.setItem(itemStack);
//                    eyeOfEnderEntity.initTargetPos(blockPos);
//                    world.emitGameEvent(GameEvent.PROJECTILE_SHOOT, eyeOfEnderEntity.getPos(), GameEvent.Emitter.of(user));
//                    world.spawnEntity(eyeOfEnderEntity);
//                    if (user instanceof ServerPlayerEntity serverPlayerEntity) {
//                        Criteria.USED_ENDER_EYE.trigger(serverPlayerEntity, blockPos);
//                    }
//
//                    float f = MathHelper.lerp(world.random.nextFloat(), 0.33F, 0.5F);
//                    world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, f);
//                    itemStack.decrementUnlessCreative(1, user);
//                    user.incrementStat(Stats.USED.getOrCreateStat(this));
//                    user.swingHand(hand, true);
//                    return TypedActionResult.success(itemStack);
//                }
//            }
//
//            return TypedActionResult.consume(itemStack);
//        }
//    }
}
