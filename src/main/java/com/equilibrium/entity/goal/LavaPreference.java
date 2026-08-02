package com.equilibrium.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Function;

public class LavaPreference extends Goal {
    public static final int RANGE_Y = 1;
    protected final PathfinderMob mob;
    protected final double speed;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected boolean active;
    private final Function<PathfinderMob, TagKey<DamageType>> entityToDangerousDamageTypes;

    public LavaPreference(PathfinderMob mob, double speed) {
        this(mob, speed, DamageTypeTags.PANIC_CAUSES);
    }

    public LavaPreference(PathfinderMob mob, double speed, TagKey<DamageType> dangerousDamageTypes) {
        this(mob, speed, entity -> dangerousDamageTypes);
    }

    public LavaPreference(PathfinderMob mob, double speed, Function<PathfinderMob, TagKey<DamageType>> entityToDangerousDamageTypes) {
        this.mob = mob;
        this.speed = speed;
        this.entityToDangerousDamageTypes = entityToDangerousDamageTypes;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.level().getBlockState(this.mob.blockPosition()).is(Blocks.LAVA)) {
            BlockPos blockPos = this.locateClosestLava(this.mob.level(), this.mob, 16);
            if (blockPos != null) {
                this.targetX = (double)blockPos.getX();
                this.targetY = (double)blockPos.getY();
                this.targetZ = (double)blockPos.getZ();
                return true;
            }
        }
        return false;

    }


    protected boolean findTarget() {
        Vec3 vec3d = DefaultRandomPos.getPos(this.mob, 5, 4);
        if (vec3d == null) {
            return false;
        } else {
            this.targetX = vec3d.x;
            this.targetY = vec3d.y;
            this.targetZ = vec3d.z;
            return true;
        }
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.speed);
        this.active = true;
    }

    @Override
    public void stop() {
        this.active = false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    @Nullable
    protected BlockPos locateClosestLava(BlockGetter world, Entity entity, int rangeX) {
        BlockPos blockPos = entity.blockPosition();
        return !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()
                ? null
                : (BlockPos)BlockPos.findClosestMatch(entity.blockPosition(), rangeX, 1, pos -> world.getFluidState(pos).is(FluidTags.LAVA)).orElse(null);
    }
}
