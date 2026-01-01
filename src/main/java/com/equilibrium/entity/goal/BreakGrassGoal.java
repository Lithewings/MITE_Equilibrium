package com.equilibrium.entity.goal;


import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;


public class BreakGrassGoal extends Goal {
    private final PathAwareEntity entity;

    public BreakGrassGoal(PathAwareEntity entity) {
        this.entity =entity;
    }

    public PathAwareEntity getEntity() {
        return entity;
    }

    @Override
    public boolean canStart() {
        World world = this.getEntity().getWorld();
        return this.getEntity().getRandom().nextInt(128)==1 &&
                world.getBlockState(this.getEntity().getBlockPos()).isOf(Blocks.SHORT_GRASS)||world.getBlockState(this.getEntity().getBlockPos()).isOf(Blocks.TALL_GRASS);
    }

    @Override
    public void start() {
        World world = this.getEntity().getWorld();
        if(world.getBlockState(this.getEntity().getBlockPos()).isOf(Blocks.SHORT_GRASS)||world.getBlockState(this.getEntity().getBlockPos()).isOf(Blocks.TALL_GRASS))
            world.breakBlock(this.getEntity().getBlockPos(),false);
    }

}
