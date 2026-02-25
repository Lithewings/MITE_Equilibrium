package com.equilibrium.entity.goal;

import java.util.EnumSet;
import java.util.function.Predicate;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.event.moon_event.MoonPhaseEvent.getMoonType;


/**
 * A target goal that finds a target by entity class when the goal starts.
 */
public class AdvanceActiveTargetGoal<T extends LivingEntity> extends TrackTargetGoal {
    private static final int DEFAULT_RECIPROCAL_CHANCE = 10;
    protected final Class<T> targetClass;
    /**
     * The reciprocal of chance to actually search for a target on every tick
     * when this goal is not started. This is also the average number of ticks
     * between each search (as in a poisson distribution).
     */
    protected final int reciprocalChance;
    @Nullable
    protected LivingEntity targetEntity;
    protected AdvanceTargetPredicate targetPredicate;

    public AdvanceActiveTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility) {
        this(mob, targetClass, 10, checkVisibility, false, null);
    }

    public AdvanceActiveTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, Predicate<LivingEntity> targetPredicate) {
        this(mob, targetClass, 10, checkVisibility, false, targetPredicate);
    }

    public AdvanceActiveTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate) {
        this(mob, targetClass, 10, checkVisibility, checkCanNavigate, null);
    }

    public AdvanceActiveTargetGoal(
            MobEntity mob,
            Class<T> targetClass,
            int reciprocalChance,
            boolean checkVisibility,
            boolean checkCanNavigate,
            @Nullable Predicate<LivingEntity> targetPredicate
    ) {
        super(mob, checkVisibility, checkCanNavigate);

        this.targetClass = targetClass;
        this.reciprocalChance = toGoalTicks(reciprocalChance);
        this.setControls(EnumSet.of(Goal.Control.TARGET));
        //僵尸透视泥土等方块的逻辑实现
        this.targetPredicate = AdvanceTargetPredicate.createAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate(targetPredicate);
    }


    @Override
    public double getFollowRange(){
        double range = getMoonType(mob.getWorld()).equals("bloodMoon")? 256:32;
        if(mob.getWorld().getRegistryKey()== RegistryKey.of(RegistryKeys.WORLD, Identifier.of(MOD_ID, "underworld")))
            range=range*0.75;
        return range;
    }


    @Override
    public boolean canStart() {
        if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
            return false;
        } else {
            this.findClosestTarget();
            return (this.targetEntity != null && !(this.targetEntity instanceof CatEntity));
        }
    }

    protected Box getSearchBox(double distance) {
        return this.mob.getBoundingBox().expand(distance, 4.0, distance);
    }

    protected void findClosestTarget() {
        if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
            this.targetEntity = this.mob
                    .getWorld()
                    .getClosestEntity(
                            this.mob.getWorld().getEntitiesByClass(this.targetClass, this.getSearchBox(this.getFollowRange()), livingEntity -> true),
                            this.targetPredicate,
                            this.mob,
                            this.mob.getX(),
                            this.mob.getEyeY(),
                            this.mob.getZ()
                    );
        } else {
            this.targetEntity = this.mob.getWorld().getClosestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        }
    }

    @Override
    public void start() {
        this.mob.setTarget(this.targetEntity);
        super.start();
    }
    //请在每个怪物的构造函数中各自实现
//    @Override
//    public double getFollowRange() {
//        //其他维度下的怪物追踪距离被大幅减小
//        return this.mob.getWorld().getRegistryKey()== World.OVERWORLD ? this.mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE): 0.25* (this.mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE));
//    }
//



    public void setTargetEntity(@Nullable LivingEntity targetEntity) {
        this.targetEntity = targetEntity;
    }
}
