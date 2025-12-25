package com.equilibrium.mixin.tables;

import com.equilibrium.util.WorldMoonPhasesSelector;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
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

import java.util.Objects;

import static com.equilibrium.util.AStarPathfinder.findPath;

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

    @Inject(method = "onUse", at = @At(value = "HEAD"), cancellable = true)
    protected void onUse1(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient) {
            cir.setReturnValue(ActionResult.CONSUME);
        }






//        RegistryKey<World> registryKey = RegistryKey.of(RegistryKeys.WORLD, Identifier.of("miteequilibrium", "underworld"));
        if (player.getWorld().getRegistryKey() !=World.OVERWORLD) {
            player.sendMessage(Text.of("你无法在主世界之外的维度睡觉"),true);
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }
        if(Objects.equals(WorldMoonPhasesSelector.getMoonType(), "bloodMoon")) {
            player.sendMessage(Text.of("血月让你无法休息"), true);
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }


        if (!world.isClient) {

            // 向上搜索第一个空气方块
            BlockPos firstAirPos = findFirstAirAbove(world, pos);


            if (firstAirPos != null) {
                // 向玩家发送找到的坐标信息
//                player.sendMessage(Text.literal("找到的空气方块位置: " + firstAirPos+"并以此计算休息位置的安全程度"), true);
                if(!(findPath(world,pos,firstAirPos)==null)){
                    player.sendMessage(Text.of("这里并不安全,你无法入睡,尝试彻底封闭周围空间"),true);
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
                else{
                    //足够安全后,检查时间
                    if(world.getTimeOfDay() % 24000L<15500 && !Objects.equals(WorldMoonPhasesSelector.getMoonType(), "fullMoon")){
                    player.sendMessage(Text.of("你并没有困意"), true);
                    cir.setReturnValue(ActionResult.SUCCESS);
                    } else if (Objects.equals(WorldMoonPhasesSelector.getMoonType(), "fullMoon")) {
                        player.sendMessage(Text.of("满月让你感到失眠"),true);
                        cir.setReturnValue(ActionResult.SUCCESS);
                    }
                }


            } else {
                player.sendMessage(Text.literal("你无法在露天位置睡觉"), true);
                cir.setReturnValue(ActionResult.SUCCESS);
            }



        }

    }
}