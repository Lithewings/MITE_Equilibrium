package com.equilibrium.entity.goal;


import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.world.World;


public class BreakGrassGoal extends Goal {
    private final CowEntity cowEntity;

    public BreakGrassGoal(CowEntity cow) {
        cowEntity =cow;
    }

    public CowEntity getCowEntity() {
        return cowEntity;
    }

    @Override
    public boolean canStart() {
        World world = this.getCowEntity().getWorld();
        return this.cowEntity.getRandom().nextInt(64)==1 &&
                world.getBlockState(this.getCowEntity().getBlockPos()).isOf(Blocks.SHORT_GRASS)||world.getBlockState(this.getCowEntity().getBlockPos()).isOf(Blocks.TALL_GRASS);
    }

    @Override
    public void start() {
        World world = this.getCowEntity().getWorld();
        if(world.getBlockState(this.getCowEntity().getBlockPos()).isOf(Blocks.SHORT_GRASS)||world.getBlockState(this.getCowEntity().getBlockPos()).isOf(Blocks.TALL_GRASS))
            world.breakBlock(this.getCowEntity().getBlockPos(),false);
    }

}
