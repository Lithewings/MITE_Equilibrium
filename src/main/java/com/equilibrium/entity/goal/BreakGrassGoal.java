package com.equilibrium.entity.goal;


import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;


public class BreakGrassGoal extends Goal {
    private final PathfinderMob entity;

    public BreakGrassGoal(PathfinderMob entity) {
        this.entity =entity;
    }

    public PathfinderMob getEntity() {
        return entity;
    }

    @Override
    public boolean canUse() {
        Level world = this.getEntity().level();
        return this.getEntity().getRandom().nextInt(512)==1 &&
                world.getBlockState(this.getEntity().blockPosition()).is(Blocks.SHORT_GRASS)||world.getBlockState(this.getEntity().blockPosition()).is(Blocks.TALL_GRASS);
    }

    @Override
    public void start() {
        Level world = this.getEntity().level();
        if(world.getBlockState(this.getEntity().blockPosition()).is(Blocks.SHORT_GRASS)||world.getBlockState(this.getEntity().blockPosition()).is(Blocks.TALL_GRASS))
            world.destroyBlock(this.getEntity().blockPosition(),false);
    }

}
