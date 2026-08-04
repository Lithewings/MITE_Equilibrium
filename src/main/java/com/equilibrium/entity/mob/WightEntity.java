package com.equilibrium.entity.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import static com.equilibrium.server_and_client.server.SoundEventRegistry.*;
import static com.equilibrium.util.XpHashMap.getXpForLevel;
import static net.minecraft.sounds.SoundSource.HOSTILE;

public class WightEntity extends Zombie{
    //白色食尸鬼,会扣除玩家经验值
    public WightEntity(EntityType<? extends Zombie> entityType, Level world) {
        super(entityType, world);
    }







    @Override
    protected int getBaseExperienceReward(){
        return getXpForLevel(2);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.initThisCustomGoals();
    }
    @Override
    protected boolean convertsInWater() {
        return false;
    }

    protected void initThisCustomGoals() {
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor world, MobSpawnType spawnReason) {
        if (this.level().dimension() == Level.OVERWORLD && this.getY() >= 0)
            return false;
    return super.checkSpawnRules(world, spawnReason);
}


    //不会携带任何护甲和武器,除非是自己捡起的
    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance localDifficulty) {
    }







    @Override
    public void setLastHurtMob(Entity target) {
        super.setLastHurtMob(target);
        if (target instanceof Player player) {
            player.giveExperiencePoints(-50);
            //随机音调
            player.level().playSound(this, BlockPos.containing(this.position()), SoundEvents.PLAYER_BREATH,HOSTILE, 1F, (float) Math.clamp((1 + this.getRandom().nextDouble()), 1, 1.3));
        }
    }



//    @Override
//    public void onAttacking(Entity target) {
//        if(target instanceof ServerPlayerEntity player){
//            //玩家减少经验值
//            player.totalExperience-=50;
//            //随机音调
//            //只有对服务端玩家播放声音才能听到
//            player.playSound(SoundEvents.ENTITY_PLAYER_BREATH, 1F, (float)Math.clamp((1+this.getRandom().nextDouble()),1,1.3));
//        }
//        super.onAttacking(target);
//
//    }


    @Override
    public boolean canHoldItem(ItemStack stack) {
        return false;
    }





    @Override
    public void setBaby(boolean baby) {}

    protected SoundEvent getStepSound() {
        //无声
        return SoundEvents.EMPTY;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return switch (1 + this.getRandom().nextInt(1)) {
            case 1 -> ENTITY_WIGHT_AMBIENT1.get();
            case 2 -> ENTITY_WIGHT_AMBIENT2.get();
            default -> SoundEvents.EMPTY;
        };
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return switch (1 + this.getRandom().nextInt(1)) {
            case 1 -> ENTITY_WIGHT_HURT1.get();
            case 2 -> ENTITY_WIGHT_HURT2.get();
            default -> SoundEvents.EMPTY;
        };
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ENTITY_WIGHT_DEATH.get();
    }

}
