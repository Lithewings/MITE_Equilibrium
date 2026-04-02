package com.equilibrium.mixin.structure_and_dimension.portal;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderEyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderEyeItem.class)
public abstract class EnderEyeItemMixin extends Item {
    public EnderEyeItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(
            method = "use",   // 目标方法名
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;locateStructure(Lnet/minecraft/registry/tag/TagKey;Lnet/minecraft/util/math/BlockPos;IZ)Lnet/minecraft/util/math/BlockPos;"
            ),
            cancellable = true)
    private void redirectLocateStructure(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {

        if (world instanceof ServerWorld serverWorld) {
            BlockPos blockPos = serverWorld.locateStructure(StructureTags.EYE_OF_ENDER_LOCATED, user.getBlockPos(), 100, false);
            if (blockPos!=null && (Math.abs(blockPos.getX()) >= 12000 || Math.abs(blockPos.getZ()) >= 12000))
                return;
            else{
                user.sendMessage(Text.of("The eye lost its navigation."),true);
                cir.setReturnValue(TypedActionResult.consume(user.getStackInHand(hand)));
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
