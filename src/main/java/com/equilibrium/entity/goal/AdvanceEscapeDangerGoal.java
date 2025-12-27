package com.equilibrium.entity.goal;

import java.util.*;
import java.util.function.Function;

import com.equilibrium.util.AStarForAnimals;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.fix.ChunkPalettedStorageFix;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AdvanceEscapeDangerGoal extends Goal {
    public static final int RANGE_Y = 1;
    protected final PathAwareEntity mob;
    protected final double speed;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected boolean active;
    private final Function<PathAwareEntity, TagKey<DamageType>> entityToDangerousDamageTypes;

    public AdvanceEscapeDangerGoal(PathAwareEntity mob, double speed) {
        this(mob, speed, DamageTypeTags.PANIC_CAUSES);
    }

    public AdvanceEscapeDangerGoal(PathAwareEntity mob, double speed, TagKey<DamageType> dangerousDamageTypes) {
        this(mob, speed, entity -> dangerousDamageTypes);
    }

    public AdvanceEscapeDangerGoal(PathAwareEntity mob, double speed, Function<PathAwareEntity, TagKey<DamageType>> entityToDangerousDamageTypes) {
        this.mob = mob;
        this.speed = speed;
        this.entityToDangerousDamageTypes = entityToDangerousDamageTypes;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    public static HashSet<PathAwareEntity> shouldPanic = new HashSet<PathAwareEntity>();


    @Override
    public boolean canStart() {
        if(shouldPanic.contains(this.mob))
            return true;
        if (!this.isInDanger()) {
            return false;
        } else {
            if (this.mob.isOnFire()) {
                BlockPos blockPos = this.locateClosestWater(this.mob.getWorld(), this.mob, 5);
                if (blockPos != null) {
                    this.targetX = (double) blockPos.getX();
                    this.targetY = (double) blockPos.getY();
                    this.targetZ = (double) blockPos.getZ();
                    return true;
                }
            }

            return this.findTarget();
        }
    }
    protected boolean isInDanger() {
        return this.mob.getRecentDamageSource() != null
                && this.mob.getRecentDamageSource().isIn((TagKey<DamageType>)this.entityToDangerousDamageTypes.apply(this.mob));
    }

    private Vec3d adjustPositionForTerrain(Vec3d pos) {
        // 简单的Y坐标调整，确保位置在地面上方
        BlockPos blockPos = BlockPos.ofFloored(pos);
        World world = this.mob.getWorld();

        // 寻找最近的可行走位置
        for (int i = -3; i <= 3; i++) {
            BlockPos testPos = blockPos.up(i);
            BlockState state = world.getBlockState(testPos);
            BlockState belowState = world.getBlockState(testPos.down());

            if (state.isAir() && !belowState.isAir()) {
                return new Vec3d(
                        pos.x,
                        testPos.getY(),
                        pos.z
                );
            }
        }

        return pos;
    }
    protected boolean findTarget() {
        LivingEntity target = this.mob.getTarget();

        if (target != null) {
            // 计算背对目标的逃跑方向
            Vec3d mobPos = this.mob.getPos();
            Vec3d targetPos = target.getPos();

            // 计算远离目标的方向向量
            double dx = mobPos.x - targetPos.x;
            double dz = mobPos.z - targetPos.z;
            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance > 0) {
                // 归一化并延长到逃跑距离
                double fleeDistance = 20.0;
                dx = dx / distance * fleeDistance;
                dz = dz / distance * fleeDistance;

                Vec3d fleePos = new Vec3d(
                        mobPos.x + dx,
                        mobPos.y,
                        mobPos.z + dz
                );

                // 稍微调整Y坐标以处理地形
                fleePos = adjustPositionForTerrain(fleePos);

                this.targetX = fleePos.x;
                this.targetY = fleePos.y;
                this.targetZ = fleePos.z;
            }
            if(AStarForAnimals.findSimplePath(this.mob.getWorld(),this.mob.getBlockPos(),new BlockPos((int) this.targetX, (int) this.targetY, (int) this.targetZ))!=null)
                return true;
        }
        Vec3d vec3d = NoPenaltyTargeting.find(this.mob, 32, 4);
        if (vec3d == null) {
            return false;
        } else {
            this.targetX = vec3d.x;
            this.targetY = vec3d.y;
            this.targetZ = vec3d.z;
            return true;
        }
    }


    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        this.active = true;
        // 只有直接受到伤害的实体才会传播恐慌
        if (this.isInDanger()) {
            shouldPanic.add(this.mob);

            // 查找附近的同类（不在恐慌中的）
            for(PathAwareEntity nearby :this.mob.getWorld().getEntitiesByClass(
                    PathAwareEntity.class,
                    this.mob.getBoundingBox().expand(16.0),
                    entity->!entity.isPanicking()
            )){
                shouldPanic.add(nearby);
            }

        }
    }

    @Override
    public void stop() {
        this.active = false;
        shouldPanic.remove(this.mob);
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }

    @Nullable
    protected BlockPos locateClosestWater(BlockView world, Entity entity, int rangeX) {
        BlockPos blockPos = entity.getBlockPos();
        return !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()
                ? null
                : (BlockPos)BlockPos.findClosest(entity.getBlockPos(), rangeX, 1, pos -> world.getFluidState(pos).isIn(FluidTags.WATER)).orElse(null);
    }
}
