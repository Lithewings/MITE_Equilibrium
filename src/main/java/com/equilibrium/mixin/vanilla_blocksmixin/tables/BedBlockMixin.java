package com.equilibrium.mixin.vanilla_blocksmixin.tables;

import com.equilibrium.server_and_client.server.moonphase_tasks.WorldMoonPhasesSelector;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

import static com.equilibrium.entity.path_finder.AStarSimplePathfinder.findPath;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin extends HorizontalFacingBlock implements BlockEntityProvider {


    protected BedBlockMixin(Settings settings) {
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
    private BlockPos findFirstAirAbove(World world, BlockPos start) {
        int maxHeight = world.getHeight(); // 获取世界最大高度
        BlockPos mutablePos = new BlockPos(start.getX(), start.getY(), start.getZ());

        // 1. 先找到第一个非空气方块（屋顶）
        BlockPos roofPos = null; // 记录屋顶的 `BlockPos`
        for (int y = start.getY() + 1; y < maxHeight; y++) {
            mutablePos = new BlockPos(mutablePos.getX(),y,mutablePos.getZ());
            if (!world.getBlockState(mutablePos).isAir()) {
                roofPos = mutablePos.toImmutable(); // 记录第一个固体方块（屋顶）
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
                return mutablePos.toImmutable(); // 找到空气方块，返回其位置
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
    private static void spawnParticle(World world, double x, double y, double z) {
        if (world instanceof ServerWorld serverWorld) {
            //委托服务端完成粒子渲染
            serverWorld.spawnParticles(ParticleTypes.END_ROD, x + 0.5, y + 0.5, z + 0.5, 1, 0, 0, 0, 0);
        }
    }







    @Inject(method = "onUse", at = @At(value = "HEAD"), cancellable = true)
    protected void onUse1(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient) {
            cir.setReturnValue(ActionResult.CONSUME);
        }

//        RegistryKey<World> registryKey = RegistryKey.of(RegistryKeys.WORLD, Identifier.of("miteequilibrium", "underworld"));
        if (player.getWorld().getRegistryKey() !=World.OVERWORLD) {
            player.sendMessage(Text.translatable("sleep.failure.reason_1"),true);
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }
        if(Objects.equals(WorldMoonPhasesSelector.calculateMoonType(world), "bloodMoon")) {
            player.sendMessage(Text.translatable("sleep.failure.reason_2"), true);
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }


        if (!world.isClient) {
            // 向上搜索第一个空气方块
            BlockPos firstAirPos = findFirstAirAbove(world, pos);
            if (firstAirPos != null) {
                // 向玩家发送找到的坐标信息
//                player.sendMessage(Text.literal("找到的空气方块位置: " + firstAirPos+"并以此计算休息位置的安全程度"), true);
                if(findPath(world,pos,firstAirPos) instanceof List<BlockPos> list){
                    for(BlockPos blockPos : list){
                        spawnParticle(world,blockPos.getX(),blockPos.getY(),blockPos.getZ());
                    }
                    player.sendMessage(Text.translatable("sleep.failure.reason_3"),true);
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
                else{
                    //足够安全后,检查时间
                    int time = (int) (world.getTimeOfDay() % 24000L);
                    boolean isSleepy = time>15500 && time<22000;
                    //玩家会在22500之后醒来
                    if(!isSleepy && !Objects.equals(WorldMoonPhasesSelector.calculateMoonType(world), "fullMoon")){
                    player.sendMessage(Text.translatable("sleep.failure.reason_4"), true);
                    cir.setReturnValue(ActionResult.SUCCESS);
                    } else if (isSleepy && Objects.equals(WorldMoonPhasesSelector.calculateMoonType(world), "fullMoon")) {
                        player.sendMessage(Text.translatable("sleep.failure.reason_5"),true);
                        cir.setReturnValue(ActionResult.SUCCESS);
                    }
                }
            } else {
                player.sendMessage(Text.translatable("sleep.failure.reason_6"),true);
                cir.setReturnValue(ActionResult.SUCCESS);
            }



        }

    }
}