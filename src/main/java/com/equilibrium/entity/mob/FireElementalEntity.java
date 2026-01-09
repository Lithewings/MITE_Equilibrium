package com.equilibrium.entity.mob;

import com.equilibrium.entity.goal.LavaPreference;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.event.sound.SoundEventRegistry.ENTITY_INVISIBLE_STALKER_DEATH;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

public class FireElementalEntity extends HostileEntity {


    public FireElementalEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.addStatusEffect(new StatusEffectInstance(
                StatusEffects.FIRE_RESISTANCE,  // 防火效果
                Integer.MAX_VALUE,              // 无限持续时间
                0,                              // 等级0
                false,                          // 不显示粒子
                false                           // 不显示图标
        ));
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData =  super.initialize(world, difficulty, spawnReason, entityData);
        BlockPos pos =this.getBlockPos();
        while (world.getBlockState(pos).isAir() && pos.getY() > -64) {
            pos = pos.down();
        }
        this.setPosition(pos.getX(),pos.getY(),pos.getZ());
        return entityData;
    }

    @Override
    protected int getXpToDrop(){
        return getXpForLevel(1);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(3, new SwimGoal(this));
        this.goalSelector.add(4, new LavaPreference(this, 1.5));
        this.goalSelector.add(5, new GoToWalkTargetGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLOCK_FIRE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_FIRE_EXTINGUISH;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_FIRE_EXTINGUISH;
    }





    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
    }











    public static boolean canSpawn(EntityType<FireElementalEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return true;
    }



    int count = 0;

    @Override
    public boolean damage(DamageSource source, float amount) {


        if(source.getSource() instanceof SnowballEntity){
            return super.damage(this.getDamageSources().generic(), 10);
        }
        return super.damage(source, amount);



    }

    @Override
    public boolean hurtByWater() {
        return true;
    }

    @Override
    public void onAttacking(Entity target) {
        target.setOnFireFor(16);
        super.onAttacking(target);
    }

    @Override
    public void tick() {






        if (this.getWorld().getBlockState(this.getBlockPos()).isOf(Blocks.WATER)) {
            super.damage(this.getDamageSources().generic(),20);
        }
        count++;
        if (count > 20) {
            if (!this.getWorld().getBlockState(this.getBlockPos()).isOf(Blocks.LAVA)) {
                this.setHealth(this.getHealth() - 1);
            }
            else{
                this.heal(1);
            }
            if(this.isAlive())
                this.setFireTicks(24000);
            else{
                this.extinguish();
            }
            count=0;
        }
        super.tick();
    }
}
