package com.equilibrium.entity.goal;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class LavaPreference extends Goal {
    public static final int RANGE_Y = 1;
    protected final PathAwareEntity mob;
    protected final double speed;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected boolean active;
    private final Function<PathAwareEntity, TagKey<DamageType>> entityToDangerousDamageTypes;

    public LavaPreference(PathAwareEntity mob, double speed) {
        this(mob, speed, DamageTypeTags.PANIC_CAUSES);
    }

    public LavaPreference(PathAwareEntity mob, double speed, TagKey<DamageType> dangerousDamageTypes) {
        this(mob, speed, entity -> dangerousDamageTypes);
    }

    public LavaPreference(PathAwareEntity mob, double speed, Function<PathAwareEntity, TagKey<DamageType>> entityToDangerousDamageTypes) {
        this.mob = mob;
        this.speed = speed;
        this.entityToDangerousDamageTypes = entityToDangerousDamageTypes;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!this.mob.getWorld().getBlockState(this.mob.getBlockPos()).isOf(Blocks.LAVA)) {
            BlockPos blockPos = this.locateClosestLava(this.mob.getWorld(), this.mob, 16);
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
        Vec3d vec3d = NoPenaltyTargeting.find(this.mob, 5, 4);
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
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        this.active = true;
    }

    @Override
    public void stop() {
        this.active = false;
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }

    @Nullable
    protected BlockPos locateClosestLava(BlockView world, Entity entity, int rangeX) {
        BlockPos blockPos = entity.getBlockPos();
        return !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()
                ? null
                : (BlockPos)BlockPos.findClosest(entity.getBlockPos(), rangeX, 1, pos -> world.getFluidState(pos).isIn(FluidTags.LAVA)).orElse(null);
    }
}
