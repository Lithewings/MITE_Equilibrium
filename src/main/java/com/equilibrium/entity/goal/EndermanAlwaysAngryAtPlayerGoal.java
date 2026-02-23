package com.equilibrium.entity.goal;

import com.equilibrium.persistent_state.StateSaverAndLoader;
import com.equilibrium.util.ServerInfoRecorder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.EnumSet;
import java.util.function.Predicate;




public class EndermanAlwaysAngryAtPlayerGoal<T extends LivingEntity> extends TrackTargetGoal {
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
    protected TargetPredicate targetPredicate;

    public EndermanAlwaysAngryAtPlayerGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility) {
        this(mob, targetClass, 10, checkVisibility, false, null);
    }

    public EndermanAlwaysAngryAtPlayerGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, Predicate<LivingEntity> targetPredicate) {
        this(mob, targetClass, 10, checkVisibility, false, targetPredicate);
    }

    public EndermanAlwaysAngryAtPlayerGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate) {
        this(mob, targetClass, 10, checkVisibility, checkCanNavigate, null);
    }

    public EndermanAlwaysAngryAtPlayerGoal(
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
        this.targetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate(targetPredicate);
    }



    @Unique
    public boolean shouldAlwaysAngryAtPlayer(){
        StateSaverAndLoader stateSaverAndLoader;
        stateSaverAndLoader = StateSaverAndLoader.getServerState(this.mob.getServer());
        return stateSaverAndLoader.playerDeathTimes>=30;
    }



    @Unique
    private boolean shouldAngryBecauseOfPlayerHoldEnderPearl(){
        return this.targetEntity instanceof PlayerEntity player && player.getInventory().contains(Items.ENDER_PEARL.getDefaultStack());
    }






    @Override
    public boolean canStart() {
        if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
            return false;
        } else {
            this.findClosestTarget();
            boolean shouldSetAngryAtPlayer = shouldAngryBecauseOfPlayerHoldEnderPearl()||shouldAlwaysAngryAtPlayer();
            if(this.targetEntity == null)
                return false;
            else if(shouldSetAngryAtPlayer)
                return true;
            else
                //这个goal不应该施加别的生物进去
                return false;

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

    public void setTargetEntity(@Nullable LivingEntity targetEntity) {
        this.targetEntity = targetEntity;
    }
}
