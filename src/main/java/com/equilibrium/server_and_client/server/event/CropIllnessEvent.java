package com.equilibrium.server_and_client.server.event;

import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.equilibrium.OnServerInitialize.CROP_IS_ILLNESS;

public class CropIllnessEvent {

    public static Map<BlockPos,Boolean> CROP_BLOCK_POS =new ConcurrentHashMap<>();



    //提供两个方法,是否引起邻居更新?或者理解为无参执行,这是由服务器调用的
    public static void updateCropBlockPos(ServerWorld world, BlockPos triggerPos){
        //更新集合,删除非空元素
        for(BlockPos cropPos : CROP_BLOCK_POS.keySet()){
            updateOld(world, cropPos);
            //更新邻居
            updateCropNeighborhood(world,triggerPos);

        }
    }

    private static void updateOld(ServerWorld world, BlockPos cropPos) {
        if(world.getBlockState(cropPos).isAir()) {
            CROP_BLOCK_POS.remove(cropPos);
            world.setBlockState(cropPos, Blocks.AIR.getDefaultState());
            return;
        }
        //更新,删除非法状态
        if(!world.getBlockState(cropPos).contains(CROP_IS_ILLNESS)) {
            CROP_BLOCK_POS.remove(cropPos);
            //这句代码只是触发更新
            world.setBlockState(cropPos, world.getBlockState(cropPos));
            return;
        }
        //现在剩下的是要更新状态的作物

        //更新疾病状态
        world.setBlockState(cropPos, world.getBlockState(cropPos).with(CROP_IS_ILLNESS, CROP_BLOCK_POS.get(cropPos)));

        //破坏生病的作物
        if(world.getBlockState(cropPos).get(CROP_IS_ILLNESS)) {
            if(world.getRandom().nextInt(1024)==0){
                world.breakBlock(cropPos,true);
                CROP_BLOCK_POS.remove(cropPos);
            }
        }
    }

    public static void updateCropBlockPos(ServerWorld world){
        //更新集合,删除非空元素
        for(BlockPos cropPos : CROP_BLOCK_POS.keySet()){
            updateOld(world, cropPos);
        }
    }


    public static void updateCropNeighborhood(ServerWorld world,BlockPos centerPos){

        List<BlockPos> neighborList = List.of(centerPos,centerPos.east(),centerPos.south(),centerPos.west(),centerPos.north());

        for(BlockPos cropPos : neighborList){
            if(world.getBlockState(cropPos).getBlock() instanceof CropBlock) {
                //放入哈希表中
                CROP_BLOCK_POS.put(cropPos,world.getBlockState(cropPos).get(CROP_IS_ILLNESS));
            }
        }
    }


    //批量施加生病逻辑,提供两个方法,是否引起邻居更新?或者理解为无参执行,这是由服务器调用的
    public static void applyIllnessForCrop(ServerWorld world,BlockPos triggerPos){
        updateCropBlockPos(world,triggerPos);
        applyIllnessOnOld(world);
    }
    public static void applyIllnessForCrop(ServerWorld world){
        updateCropBlockPos(world);
        applyIllnessOnOld(world);
    }

    private static void applyIllnessOnOld(ServerWorld world) {
        for(BlockPos cropPos : CROP_BLOCK_POS.keySet()){
            //若没有生病,使其生病
            if(cropPos!=null && world.getBlockState(cropPos).contains(CROP_IS_ILLNESS)&& !world.getBlockState(cropPos).get(CROP_IS_ILLNESS)) {
                if(world.getRandom().nextInt(64)==0){
                    world.setBlockState(cropPos, world.getBlockState(cropPos).with(CROP_IS_ILLNESS, true));
                    CROP_BLOCK_POS.put(cropPos,true);
                }
            }
            //如果检查是生病的作物,应该如何做? :打碎
            if(world.getBlockState(cropPos).getBlock() instanceof CropBlock) {
                //破坏生病的作物
                if(world.getBlockState(cropPos).get(CROP_IS_ILLNESS)) {
                    if(world.getRandom().nextInt(128)==0){
                        world.breakBlock(cropPos,false);
                        CROP_BLOCK_POS.remove(cropPos);
                    }

            }


        }
    }
}}
