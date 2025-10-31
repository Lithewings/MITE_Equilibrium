package com.equilibrium.entity.mob;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import static com.equilibrium.event.sound.SoundEventRegistry.*;
import static com.equilibrium.util.XpHashMap.getXpForLevel;
import static net.minecraft.entity.effect.StatusEffects.SLOWNESS;

public class GhoulEntity extends ZombieEntity {
    //食尸鬼
    public GhoulEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
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

    private BlockPos findGroundPosition(World world, BlockPos start) {
        // 我们从start开始向下搜索，直到世界底部或者找到合适的地面
        for (int y = start.getY(); start.getY()-y<=32&&y>-64; y--) {
            BlockPos pos = new BlockPos(start.getX(), y, start.getZ());
            if(world.isTopSolid(pos,this))
                return pos.offset(Direction.Axis.Y,1); // 返回这个方块的位置，我们将在其上方生成生物
        }
        // 如果没有找到，返回null
        return null;
    }

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        if(this.getWorld().getRegistryKey()== World.OVERWORLD && this.getY()>=40)
            return false;

        if(!this.isOnGround()){
            BlockPos pos = findGroundPosition(this.getWorld(),this.getBlockPos());
            if(pos!=null)
                this.setPosition(pos.getX(),pos.getY(),pos.getZ());
            else
                return false;
        }

        return super.canSpawn(world, spawnReason);
    }

    @Override
    protected int getXpToDrop(){
        return getXpForLevel(2);
    }

    //不会携带任何护甲和武器,除非是自己捡起的
    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
    }

    @Override
    public boolean canPickupItem(ItemStack stack) {
        return false;
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
    public void onAttacking(Entity target) {
        super.onAttacking(target);
        if(target instanceof PlayerEntity player){
            player.addStatusEffect(new StatusEffectInstance(SLOWNESS,100,2));
        }
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
            case 1 -> ENTITY_GHOUL_AMBIENT1;
            case 2 -> ENTITY_GHOUL_AMBIENT2;
            default -> SoundEvents.INTENTIONALLY_EMPTY;
        };
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return switch (1 + this.getRandom().nextInt(1)) {
            case 1 -> ENTITY_GHOUL_HURT1;
            case 2 -> ENTITY_GHOUL_HURT2;
            default -> SoundEvents.INTENTIONALLY_EMPTY;
        };
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ENTITY_GHOUL_DEATH;
    }






}
