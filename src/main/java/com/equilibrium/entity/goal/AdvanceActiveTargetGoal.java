package com.equilibrium.entity.goal;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.server_and_client.server.moonphase_tasks.MoonPhaseEvent.getMoonType;


/**
 * A target goal that finds a target by entity class when the goal starts.
 */
public class AdvanceActiveTargetGoal<T extends LivingEntity> extends TargetGoal {
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

    public AdvanceActiveTargetGoal(Mob mob, Class<T> targetClass, boolean checkVisibility) {
        this(mob, targetClass, 10, checkVisibility, false, null);
    }

    public AdvanceActiveTargetGoal(Mob mob, Class<T> targetClass, boolean checkVisibility, Predicate<LivingEntity> targetPredicate) {
        this(mob, targetClass, 10, checkVisibility, false, targetPredicate);
    }

    public AdvanceActiveTargetGoal(Mob mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate) {
        this(mob, targetClass, 10, checkVisibility, checkCanNavigate, null);
    }

    public AdvanceActiveTargetGoal(
            Mob mob,
            Class<T> targetClass,
            int reciprocalChance,
            boolean checkVisibility,
            boolean checkCanNavigate,
            @Nullable Predicate<LivingEntity> targetPredicate
    ) {
        super(mob, checkVisibility, checkCanNavigate);

        this.targetClass = targetClass;
        this.reciprocalChance = reducedTickDelay(reciprocalChance);
        this.setFlags(EnumSet.of(Flag.TARGET));
        //僵尸透视泥土等方块的逻辑实现
        this.targetPredicate = AdvanceTargetPredicate.forCombat().range(this.getFollowDistance()).selector(targetPredicate);
    }


    @Override
    public double getFollowDistance(){
        double range = getMoonType(mob.level()).equals("bloodMoon")? 256:32;
        if(mob.level().dimension()== ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(MOD_ID, "underworld")))
            range=range*0.75;
        return range;
    }


    @Override
    public boolean canUse() {
        if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
            return false;
        } else {
            this.findClosestTarget();
            return (this.targetEntity != null && !(this.targetEntity instanceof Cat));
        }
    }

    protected AABB getSearchBox(double distance) {
        return this.mob.getBoundingBox().inflate(distance, 4.0, distance);
    }

    protected void findClosestTarget() {
        if (this.targetClass != Player.class && this.targetClass != ServerPlayer.class) {
            this.targetEntity = this.mob
                    .level()
                    .getNearestEntity(
                            this.mob.level().getEntitiesOfClass(this.targetClass, this.getSearchBox(this.getFollowDistance()), livingEntity -> true),
                            this.targetPredicate,
                            this.mob,
                            this.mob.getX(),
                            this.mob.getEyeY(),
                            this.mob.getZ()
                    );
        } else {
            this.targetEntity = this.mob.level().getNearestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
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
