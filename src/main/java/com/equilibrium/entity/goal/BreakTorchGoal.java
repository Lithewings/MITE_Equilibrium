package com.equilibrium.entity.goal;


import com.equilibrium.util.AStarCanGoTo;
import com.equilibrium.util.AStarPathfinder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;

import static com.equilibrium.util.AStarPathfinder.findPath;


public class BreakTorchGoal extends Goal {
    private final PathAwareEntity entity;

    public BreakTorchGoal(PathAwareEntity entity) {
        this.entity =entity;
    }

    public PathAwareEntity getEntity() {
        return this.entity;
    }

    //导航到最近的火把位置,若不存在,ArrayList为空

    private static ArrayList<BlockPos> blockPosNearestTorch(PathAwareEntity entity) {

        World world =entity.getWorld();

        // 以生物为中心，搜索16格范围内的方块
        int searchRadius = 16;
        int x = entity.getBlockPos().getX();
        int y = entity.getBlockPos().getY();
        int z = entity.getBlockPos().getZ();


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

                    if (blockState.isOf(Blocks.TORCH)||blockState.isOf(Blocks.WALL_TORCH)) {
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
    public boolean canStart() {
        this.torchBlockPosList = blockPosNearestTorch(getEntity());
        if(torchBlockPosList.isEmpty())
            return false;
        //过滤掉不可达的火把路径
        torchBlockPosList.removeIf(pos -> !AStarPathfinder.hasPath(this.entity.getWorld(), this.entity.getBlockPos(), pos));
        //现在,如果torchBlockPosList存在,那么其中记录的是可到的所有火把坐标

        // 按距离排序
        torchBlockPosList.sort(Comparator.comparingDouble(
                pos -> this.entity.getBlockPos().getSquaredDistance(pos)
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
        if(this.entity.getNavigation().isIdle()) {
            this.entity.getNavigation().startMovingTo(target.getX(), target.getY(), target.getZ(), 1);
        }
        if(this.entity.getBlockPos().isWithinDistance(target,3)){
            this.entity.getWorld().breakBlock(target,true);

            this.entity.getWorld().playSound(this.entity,target, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS,1,1);

            this.shouldStop=true;
        }

    }

    @Override
    public boolean shouldContinue() {

        if(this.shouldStop)
            return false;

        if(target==null)
            return false;
        //最近的目标路径存在
        if(!AStarPathfinder.hasPath(this.entity.getWorld(), this.entity.getBlockPos(), target))
            return false;


        return true;
    }

    @Override
    public void stop() {
        torchBlockPosList.clear();
    }
}
