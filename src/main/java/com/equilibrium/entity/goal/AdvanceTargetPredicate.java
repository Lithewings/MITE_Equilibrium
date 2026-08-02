package com.equilibrium.entity.goal;

import com.equilibrium.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AdvanceTargetPredicate extends TargetingConditions {
    public static final AdvanceTargetPredicate DEFAULT = forCombat();
    private static final double MIN_DISTANCE = 2.0;
    private final boolean attackable;
    private double baseMaxDistance = -1.0;
    private boolean respectsVisibility = true;
    private boolean useDistanceScalingFactor = true;
    @Nullable
    private Predicate<LivingEntity> predicate;

    private AdvanceTargetPredicate(boolean attackable) {
        super(attackable);
        this.attackable = attackable;
    }

    public static AdvanceTargetPredicate forCombat() {
        return new AdvanceTargetPredicate(true);
    }

    public static AdvanceTargetPredicate forNonCombat() {
        return new AdvanceTargetPredicate(false);
    }

    public AdvanceTargetPredicate copy() {

        AdvanceTargetPredicate advanceTargetPredicate = this.attackable ? forCombat() : forNonCombat();
        advanceTargetPredicate.baseMaxDistance = this.baseMaxDistance;
        advanceTargetPredicate.respectsVisibility = this.respectsVisibility;
        advanceTargetPredicate.useDistanceScalingFactor = this.useDistanceScalingFactor;
        advanceTargetPredicate.predicate = this.predicate;
        return advanceTargetPredicate;
    }

    public AdvanceTargetPredicate range(double baseMaxDistance) {
        this.baseMaxDistance = baseMaxDistance;
        return this;
    }

    public AdvanceTargetPredicate ignoreLineOfSight() {
        this.respectsVisibility = false;
        return this;
    }

    public AdvanceTargetPredicate ignoreInvisibilityTesting() {
        this.useDistanceScalingFactor = false;
        return this;
    }

    public AdvanceTargetPredicate selector(@Nullable Predicate<LivingEntity> predicate) {
        this.predicate = predicate;
        return this;
    }

    public boolean test(@Nullable LivingEntity baseEntity, LivingEntity targetEntity) {
        if (baseEntity == targetEntity) {
            return false;
        } else if (!targetEntity.canBeSeenByAnyone()) {
            return false;
        } else if (this.predicate != null && !this.predicate.test(targetEntity)) {
            return false;
        } else {
            if (baseEntity == null) {
                if (this.attackable && (!targetEntity.canBeSeenAsEnemy() || targetEntity.level().getDifficulty() == Difficulty.PEACEFUL)) {
                    return false;
                }
            } else {
                if (this.attackable && (!baseEntity.canAttack(targetEntity) || !baseEntity.canAttackType(targetEntity.getType()) || baseEntity.isAlliedTo(targetEntity))) {
                    return false;
                }

                if (this.baseMaxDistance > 0.0) {
                    double d = this.useDistanceScalingFactor ? targetEntity.getVisibilityPercent(baseEntity) : 1.0;
                    double e = Math.max(this.baseMaxDistance * d, 2.0);
                    double f = baseEntity.distanceToSqr(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ());
                    if (f > e * e) {
                        return false;
                    }
                }

                if (this.respectsVisibility && baseEntity instanceof Mob mobEntity && !canSeeThroughTransparentBlocks(mobEntity, targetEntity)) {
                    return false;
                }
            }

            return true;
        }
    }
    private boolean canSeeThroughTransparentBlocks(Mob mobEntity, LivingEntity targetEntity) {
        Level world = mobEntity.level();
        Vec3 startPos = new Vec3(mobEntity.getX(), mobEntity.getEyeY(), mobEntity.getZ());
        Vec3 endPos = new Vec3(targetEntity.getX(), targetEntity.getEyeY(), targetEntity.getZ());

        double followRange = mobEntity.getAttributeValue(Attributes.FOLLOW_RANGE);
        double distance = startPos.distanceTo(endPos);

        // If the distance exceeds the follow range, return false
        if (distance > followRange) {
            return false;
        }






        BlockHitResult hitResult = world.clip(new ClipContext(startPos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mobEntity));

        BlockPos hitPos = hitResult.getBlockPos();
        BlockState blockState = world.getBlockState(hitPos);

        // Check if the block is transparent
        return blockState.propagatesSkylightDown(world,hitPos) || blockState.isAir()||blockState.is(ModBlockTags.TRANSPARENT_FOR_ZOMBIE);
    }
}
