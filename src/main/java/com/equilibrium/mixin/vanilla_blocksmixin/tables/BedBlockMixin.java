package com.equilibrium.mixin.vanilla_blocksmixin.tables;

import com.equilibrium.server_and_client.server.moonphase_tasks.WorldMoonPhasesSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import static com.equilibrium.entity.path_finder.AStarSimplePathfinder.findPath;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin extends HorizontalDirectionalBlock implements EntityBlock {


    protected BedBlockMixin(Properties settings) {
        super(settings);
    }


    /**
     * 从指定位置向上搜索第一个空气方块。
     *
     * @param world 世界对象
     * @param start 起始位置
     * @return 找到的第一个空气方块位置；未找到则返回 null
     */
    @Unique
    private BlockPos findFirstAirAbove(Level world, BlockPos start) {
        int maxHeight = world.getHeight(); // 获取世界最大高度
        BlockPos mutablePos = new BlockPos(start.getX(), start.getY(), start.getZ());

        // 1. 先找到第一个非空气方块（屋顶）
        BlockPos roofPos = null; // 记录屋顶的 `BlockPos`
        for (int y = start.getY() + 1; y < maxHeight; y++) {
            mutablePos = new BlockPos(mutablePos.getX(),y,mutablePos.getZ());
            if (!world.getBlockState(mutablePos).isAir()) {
                roofPos = mutablePos.immutable(); // 记录第一个固体方块（屋顶）
                break;
            }
        }

        // 如果没有找到屋顶，说明是露天的，直接返回 null
        if (roofPos == null) {
            return null;
        }

        // 2. 从屋顶的下一格开始，寻找第一个空气方块
        for (int y = roofPos.getY() + 1; y < maxHeight; y++) {
            mutablePos = new BlockPos(mutablePos.getX(),y,mutablePos.getZ());
            if (world.getBlockState(mutablePos).isAir()) {
                return mutablePos.immutable(); // 找到空气方块，返回其位置
            }
        }

        return null; // 如果没有找到空气方块，返回 null
    }

//    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z", ordinal = 0), cancellable = true)
//    protected void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
//        player.sendMessage(Text.of("这里并不安全,你无法入睡"));
//        cir.setReturnValue(ActionResult.SUCCESS);
//    }

    @Unique
    private static void spawnParticle(Level world, double x, double y, double z) {
        if (world instanceof ServerLevel serverWorld) {
            //委托服务端完成粒子渲染
            serverWorld.sendParticles(ParticleTypes.END_ROD, x + 0.5, y + 0.5, z + 0.5, 1, 0, 0, 0, 0);
        }
    }







    @Inject(method = "useWithoutItem", at = @At(value = "HEAD"), cancellable = true)
    protected void onUse1(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (world.isClientSide) {
            cir.setReturnValue(InteractionResult.CONSUME);
        }

//        RegistryKey<World> registryKey = RegistryKey.of(RegistryKeys.WORLD, Identifier.of("miteequilibrium", "underworld"));
        if (player.level().dimension() !=Level.OVERWORLD) {
            player.displayClientMessage(Component.translatable("sleep.failure.reason_1"),true);
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }
        if(Objects.equals(WorldMoonPhasesSelector.calculateMoonType(world), "bloodMoon")) {
            player.displayClientMessage(Component.translatable("sleep.failure.reason_2"), true);
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }


        if (!world.isClientSide) {
            // 向上搜索第一个空气方块
            BlockPos firstAirPos = findFirstAirAbove(world, pos);
            if (firstAirPos != null) {
                // 向玩家发送找到的坐标信息
//                player.sendMessage(Text.literal("找到的空气方块位置: " + firstAirPos+"并以此计算休息位置的安全程度"), true);
                if(findPath(world,pos,firstAirPos) instanceof List<BlockPos> list){
                    for(BlockPos blockPos : list){
                        spawnParticle(world,blockPos.getX(),blockPos.getY(),blockPos.getZ());
                    }
                    player.displayClientMessage(Component.translatable("sleep.failure.reason_3"),true);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
                else{
                    //足够安全后,检查时间
                    int time = (int) (world.getDayTime() % 24000L);
                    boolean isSleepy = time>15500 && time<22000;
                    //玩家会在22500之后醒来
                    if(!isSleepy && !Objects.equals(WorldMoonPhasesSelector.calculateMoonType(world), "fullMoon")){
                    player.displayClientMessage(Component.translatable("sleep.failure.reason_4"), true);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                    } else if (isSleepy && Objects.equals(WorldMoonPhasesSelector.calculateMoonType(world), "fullMoon")) {
                        player.displayClientMessage(Component.translatable("sleep.failure.reason_5"),true);
                        cir.setReturnValue(InteractionResult.SUCCESS);
                    }
                }
            } else {
                player.displayClientMessage(Component.translatable("sleep.failure.reason_6"),true);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }



        }

    }
}