package com.equilibrium.entity.mob;

import com.equilibrium.entity.goal.BreakTorchGoal;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
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

import static com.equilibrium.entity.utilForEntity.forPlayerIsEnchantedItemCauseDamage;
import static com.equilibrium.server_and_client.server.SoundEventRegistry.*;
import static com.equilibrium.util.XpHashMap.getXpForLevel;
import static net.minecraft.world.effect.MobEffects.BLINDNESS;

public class InvisibleStalkerEntity extends Zombie {
    public InvisibleStalkerEntity(EntityType<? extends Zombie> entityType, Level world) {
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

    //免疫非附魔武器伤害(除此之外,还有黑色史莱姆、凋零骷髅、烈焰人免疫非附魔武器伤害)


    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.getEntity() instanceof Player
                ? !forPlayerIsEnchantedItemCauseDamage(damageSource)
                : super.isInvulnerableTo(damageSource);
    }

    protected void initThisCustomGoals() {
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
        this.targetSelector.addGoal(4, new BreakTorchGoal(this));

    }

    @Override
    //影子潜伏者若生成在主世界,则只能在y<=0的高度生成,且必须在全黑环境下
    public boolean checkSpawnRules(LevelAccessor world, MobSpawnType spawnReason) {
        if(this.level().dimension()== Level.OVERWORLD && this.getY()>=0 && world.getMaxLocalRawBrightness(this.blockPosition())>0)
            return false;
        return super.checkSpawnRules(world, spawnReason);
    }



    @Override
    protected int getBaseExperienceReward(){
        return getXpForLevel(2);
    }




    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance localDifficulty) {
    }

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
        return switch (1 + this.getRandom().nextInt(2)) {
            case 1 -> ENTITY_INVISIBLE_STALKER_AMBIENT1.get();
            case 2 -> ENTITY_INVISIBLE_STALKER_AMBIENT2.get();
            case 3 -> ENTITY_INVISIBLE_STALKER_AMBIENT3.get();
            default -> SoundEvents.EMPTY;
        };
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return switch (1 + this.getRandom().nextInt(1)) {
            case 1 -> ENTITY_INVISIBLE_STALKER_HURT1.get();
            case 2 -> ENTITY_INVISIBLE_STALKER_HURT2.get();
            default -> SoundEvents.EMPTY;
        };
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ENTITY_INVISIBLE_STALKER_DEATH.get();
    }

    @Override
    public void setLastHurtMob(Entity target) {
        super.setLastHurtMob(target);
        if(target instanceof Player player){
            player.addEffect(new MobEffectInstance(BLINDNESS,100,2));
        }
    }


}
