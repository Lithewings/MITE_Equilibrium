package com.equilibrium.entity.mob;

import com.equilibrium.entity.goal.BreakTorchGoal;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.*;
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

public class ShadowEntity extends Zombie {
    //黑色食尸鬼,(应该主动破坏火把)若在主世界,只会在世界最黑暗处且y位置小于0生成


    @Override
    protected int getBaseExperienceReward(){
        return getXpForLevel(2);
    }




    public ShadowEntity(EntityType<? extends Zombie> entityType, Level world) {
        super(entityType, world);
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
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
        this.targetSelector.addGoal(4, new BreakTorchGoal(this));
    }


    @Override
    public boolean canHoldItem(ItemStack stack) {
        return false;
    }





    @Override
    public boolean checkSpawnRules(LevelAccessor world, MobSpawnType spawnReason) {
        if(this.level().dimension()== Level.OVERWORLD && this.getY()>=0)
            return false;
        //若在主世界中,则只在亮度为0的位置生成
        if(world.getMaxLocalRawBrightness(this.blockPosition())>0)
            return false;
        return super.checkSpawnRules(world, spawnReason);
    }


    //不会携带任何护甲和武器,除非是自己捡起的
    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance localDifficulty) {
    }



//
//    /**
//     * @param target
//     */
//    @Override
//    public void onAttacking(Entity target) {
//        super.onAttacking(target);
//        if(target instanceof PlayerEntity player){
//            //玩家减少经验值
//            player.totalExperience-=50;
//            //随机音调
//            player.playSound(SoundEvents.ENTITY_PLAYER_BREATH, 1F, (float)Math.clamp((1+this.getRandom().nextDouble()),1,1.3));
//        }
//    }








    @Override
    public void setBaby(boolean baby) {}

    protected SoundEvent getStepSound() {
        //无声
        return SoundEvents.EMPTY;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return switch (1 + this.getRandom().nextInt(1)) {
            case 1 -> ENTITY_GHOUL_AMBIENT1.get();
            case 2 -> ENTITY_GHOUL_AMBIENT2.get();
            default -> SoundEvents.EMPTY;
        };
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return switch (1 + this.getRandom().nextInt(1)) {
            case 1 -> ENTITY_GHOUL_HURT1.get();
            case 2 -> ENTITY_GHOUL_HURT2.get();
            default -> SoundEvents.EMPTY;
        };
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ENTITY_GHOUL_DEATH.get();
    }


}
