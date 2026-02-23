package com.equilibrium.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import static com.equilibrium.event.SoundEventRegistry.*;
import static com.equilibrium.util.XpHashMap.getXpForLevel;
import static net.minecraft.sound.SoundCategory.HOSTILE;

public class WightEntity extends ZombieEntity{
    //白色食尸鬼,会扣除玩家经验值
    public WightEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }







    @Override
    protected int getXpToDrop(){
        return getXpForLevel(2);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initThisCustomGoals();
    }
    @Override
    protected boolean canConvertInWater() {
        return false;
    }

    protected void initThisCustomGoals() {
        this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
    }

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        if (this.getWorld().getRegistryKey() == World.OVERWORLD && this.getY() >= 0)
            return false;
    return super.canSpawn(world, spawnReason);
}


    //不会携带任何护甲和武器,除非是自己捡起的
    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
    }







    @Override
    public void onAttacking(Entity target) {
        super.onAttacking(target);
        if (target instanceof PlayerEntity player) {
            player.addExperience(-50);
            //随机音调
            player.getWorld().playSound(this, BlockPos.ofFloored(this.getPos()), SoundEvents.ENTITY_PLAYER_BREATH,HOSTILE, 1F, (float) Math.clamp((1 + this.getRandom().nextDouble()), 1, 1.3));
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
    public boolean canPickupItem(ItemStack stack) {
        return false;
    }





    @Override
    public void setBaby(boolean baby) {}

    protected SoundEvent getStepSound() {
        //无声
        return SoundEvents.INTENTIONALLY_EMPTY;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return switch (1 + this.getRandom().nextInt(1)) {
            case 1 -> ENTITY_WIGHT_AMBIENT1;
            case 2 -> ENTITY_WIGHT_AMBIENT2;
            default -> SoundEvents.INTENTIONALLY_EMPTY;
        };
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return switch (1 + this.getRandom().nextInt(1)) {
            case 1 -> ENTITY_WIGHT_HURT1;
            case 2 -> ENTITY_WIGHT_HURT2;
            default -> SoundEvents.INTENTIONALLY_EMPTY;
        };
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ENTITY_WIGHT_DEATH;
    }

}
