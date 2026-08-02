package com.equilibrium.entity.goal;


import com.equilibrium.entity.path_finder.AStarSimplePathfinder;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;


public class BreakTorchGoal extends Goal {
    private final PathfinderMob entity;

    public BreakTorchGoal(PathfinderMob entity) {
        this.entity =entity;
    }

    public PathfinderMob getEntity() {
        return this.entity;
    }

    //导航到最近的火把位置,若不存在,ArrayList为空

    private static ArrayList<BlockPos> blockPosNearestTorch(PathfinderMob entity) {

        Level world =entity.level();

        // 以生物为中心，搜索16格范围内的方块
        int searchRadius = 16;
        int x = entity.blockPosition().getX();
        int y = entity.blockPosition().getY();
        int z = entity.blockPosition().getZ();


        ArrayList<BlockPos> posArrayList= new ArrayList<>();

        // 从左上角到右下角顺序搜索
        for (int dx = -searchRadius; dx <= searchRadius; dx++) {
            for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                for (int dy = -4; dy <= 4; dy++) {
                    // 计算当前搜索位置的世界坐标
                    int worldX = x + dx;
                    int worldY = y + dy;
                    int worldZ = z + dz;

                    // 获取方块
                    BlockPos pos = new BlockPos(worldX, worldY, worldZ);
                    BlockState blockState = world.getBlockState(pos);

                    if (blockState.is(Blocks.TORCH)||blockState.is(Blocks.WALL_TORCH)) {
                        posArrayList.add(pos);
                    }
                }
            }
        }
        return posArrayList;
    }


    private BlockPos target;
    ArrayList<BlockPos> torchBlockPosList;


    @Override
    public boolean canUse() {
        this.torchBlockPosList = blockPosNearestTorch(getEntity());
        if(torchBlockPosList.isEmpty())
            return false;
        //过滤掉不可达的火把路径
        torchBlockPosList.removeIf(pos -> !AStarSimplePathfinder.hasPath(this.entity.level(), this.entity.blockPosition(), pos));
        //现在,如果torchBlockPosList存在,那么其中记录的是可到的所有火把坐标

        // 按距离排序
        torchBlockPosList.sort(Comparator.comparingDouble(
                pos -> this.entity.blockPosition().distSqr(pos)
        ));




        return !torchBlockPosList.isEmpty();
    }



    @Override
    public void start() {
        if (!torchBlockPosList.isEmpty()) {
            this.target = torchBlockPosList.getFirst();  // 第一个就是最近的
        }
    }


    private boolean shouldStop;

    @Override
    public void tick() {
        if(this.entity.getNavigation().isDone()) {
            this.entity.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 1);
        }
        if(this.entity.blockPosition().closerThan(target,3)){
            this.entity.level().destroyBlock(target,true);

            this.entity.level().playSound(this.entity,target, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,1,1);

            this.shouldStop=true;
        }

    }

    @Override
    public boolean canContinueToUse() {

        if(this.shouldStop)
            return false;

        if(target==null)
            return false;
        //最近的目标路径存在
        if(!AStarSimplePathfinder.hasPath(this.entity.level(), this.entity.blockPosition(), target))
            return false;


        return true;
    }

    @Override
    public void stop() {
        torchBlockPosList.clear();
    }
}
