package com.equilibrium.entity.goal;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.EnumSet;
import java.util.function.Predicate;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_UNIVERSAL_AGGRO;


public class EndermanAlwaysAngryAtPlayerGoal<T extends LivingEntity> extends TargetGoal {
    protected final Class<T> targetClass;
    /**
     * The reciprocal of chance to actually search for a target on every tick
     * when this goal is not started. This is also the average number of ticks
     * between each search (as in a poisson distribution).
     */
    protected final int reciprocalChance;
    @Nullable
    protected LivingEntity targetEntity;
    protected TargetingConditions targetPredicate;


    public EndermanAlwaysAngryAtPlayerGoal(Mob mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate) {
        this(mob, targetClass, 10, checkVisibility, checkCanNavigate, null);
    }

    public EndermanAlwaysAngryAtPlayerGoal(
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
        this.targetPredicate = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(targetPredicate);
    }



    @Unique
    public boolean shouldAlwaysAngryAtPlayer(){
        boolean b1 = getGameBooleanRuleFromServer(ENABLE_UNIVERSAL_AGGRO,this.mob.getServer());
        if(b1)
            return true;
        StateSaverAndLoader stateSaverAndLoader;
        stateSaverAndLoader = StateSaverAndLoader.getServerState(this.mob.getServer());
        return stateSaverAndLoader.playerDeathTimes>=30;
    }



    @Unique
    private boolean shouldAngryBecauseOfPlayerHoldEnderPearl(){
        return this.targetEntity instanceof Player player && player.getInventory().contains(Items.ENDER_PEARL.getDefaultInstance());
    }






    @Override
    //server环境
    public boolean canUse() {
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

    public void setTargetEntity(@Nullable LivingEntity targetEntity) {
        this.targetEntity = targetEntity;
    }
}
