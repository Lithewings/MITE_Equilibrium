package com.equilibrium.event;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.equilibrium.MITEequilibrium.CROP_IS_ILLNESS;

public class CropIllnessEvent {

    public static Map<BlockPos,Boolean> CROP_BLOCK_POS =new ConcurrentHashMap<>();

    public static void updateCropBlockPos(ServerWorld world){
        //更新集合,删除非空元素
        for(BlockPos cropPos : CROP_BLOCK_POS.keySet()){
            if(world.getBlockState(cropPos).isAir()) {
                CROP_BLOCK_POS.remove(cropPos);
                world.setBlockState(cropPos, Blocks.AIR.getDefaultState());
                continue;
            }
            //更新,删除非法状态
            if(!world.getBlockState(cropPos).contains(CROP_IS_ILLNESS)) {
                CROP_BLOCK_POS.remove(cropPos);
                //这句代码只是触发更新
                world.setBlockState(cropPos,world.getBlockState(cropPos));
                continue;
            }
            //现在剩下的是要更新状态的作物

            //更新疾病状态
            world.setBlockState(cropPos,world.getBlockState(cropPos).with(CROP_IS_ILLNESS, CROP_BLOCK_POS.get(cropPos)));

            //破坏生病的作物
            if(world.getBlockState(cropPos).get(CROP_IS_ILLNESS)) {
                if(world.getRandom().nextInt(1024)==0){
                    world.breakBlock(cropPos,true);
                    CROP_BLOCK_POS.remove(cropPos);
                }
            }

        }
    }
    //批量施加生病逻辑
    public static void applyIllnessForCrop(ServerWorld world){
        updateCropBlockPos(world);
        for(BlockPos cropPos : CROP_BLOCK_POS.keySet()){
            //若没有生病,使其生病
            if(cropPos!=null && world.getBlockState(cropPos).contains(CROP_IS_ILLNESS)&& !world.getBlockState(cropPos).get(CROP_IS_ILLNESS)) {
                if(world.getRandom().nextInt(128)==0){
                    world.setBlockState(cropPos, world.getBlockState(cropPos).with(CROP_IS_ILLNESS, true));
                    CROP_BLOCK_POS.put(cropPos,true);
                }
            }
            //如果检查是生病的作物,应该如何做? :打碎



        }
        updateCropBlockPos(world);
    }

}
