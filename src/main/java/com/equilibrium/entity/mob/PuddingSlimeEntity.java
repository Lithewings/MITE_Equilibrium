package com.equilibrium.entity.mob;

import com.equilibrium.item.OtherItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.entity.utilForEntity.forPlayerIsEnchantedItemCauseDamage;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

public class PuddingSlimeEntity extends BaseSlimeEntity{
    public PuddingSlimeEntity(EntityType<? extends BaseSlimeEntity> entityType, Level world) {
        super(entityType, world);
    }
    private boolean onGroundLastTick;
    @Override
    protected ParticleOptions getParticles() {
        return new ItemParticleOption(ParticleTypes.ITEM, OtherItems.PUDDING_SLIME_BALL.getDefaultInstance());
    }


    @Override
    //最小尺寸也可以攻击玩家
    protected boolean canAttack() {
        return this.isEffectiveAi();
    }





    @Override
    protected int getBaseExperienceReward(){
        int i = this.getSize();
        return getXpForLevel(i);
    }

    //不管什么情况
    @Override
    public void aiStep() {
        super.aiStep();
        this.level().getProfiler().push("looting");
        if (!this.level().isClientSide && this.canPickUpLoot() && this.isAlive() && !this.dead && this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
        )
        {
            Vec3i vec3i = this.getPickupReach();

            //不需要冷却时间,玩家丢出来直接就能被吸走 !itemEntity.cannotPickup()
            for (Entity entity : this.level()
                    .getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate((double)vec3i.getX(), (double)vec3i.getY(), (double)vec3i.getZ()))) {
                if (entity instanceof ItemEntity itemEntity && !itemEntity.isRemoved() && !itemEntity.getItem().isEmpty()  && this.wantsToPickUp(itemEntity.getItem())) {
                    this.pickUpItem(itemEntity);
                }


          }
        }

        this.level().getProfiler().pop();
    }

    @Override
    public void refreshDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.refreshDimensions();
        this.setPos(d, e, f);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.getEntity() instanceof Player
                ? !forPlayerIsEnchantedItemCauseDamage(damageSource)
                : super.isInvulnerableTo(damageSource);
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
        //箭矢只对史莱姆造成0.1的伤害
        if(source.is(DamageTypes.ARROW))
            return super.hurt(source,0f);
        //
        if( source.is(DamageTypes.PLAYER_ATTACK)) {
            Player player = (Player)  source.getEntity();
            ItemStack weapon = player.getMainHandItem();
            //以5%的进度腐蚀玩家的武器
            if (isCorruptibleItems.contains(weapon.getItem())) {
                weapon.hurtAndBreak((int) (weapon.getMaxDamage() * 0.05), player, EquipmentSlot.MAINHAND);
                this.playSound(SoundEvents.LAVA_EXTINGUISH, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

                // --------------- 新增粒子生成逻辑 ---------------
                if (this.level() instanceof ServerLevel serverWorld) {
                    // 获取玩家视线方向的位置（面前1米处）
                    Vec3 playerPos = player.position();
                    Vec3 lookVec = player.getViewVector(1.0F); // 获取玩家视线方向
                    Vec3 particlePos = playerPos.add(lookVec.scale(1.0)); // 玩家面前1米处

                    // 生成腐蚀粒子
                    serverWorld.sendParticles(
                            ParticleTypes.POOF, // 粒子类型:烟雾
                            particlePos.x,      // X坐标
                            particlePos.y + 1.0,// Y坐标（玩家眼睛高度）
                            particlePos.z,      // Z坐标
                            8,                  // 粒子数量
                            0.2,                // X方向偏移范围
                            0.2,                // Y方向偏移范围
                            0.2,                // Z方向偏移范围
                            0.05                // 粒子速度
                    );
                }





            }
            if (!weapon.isEnchanted())
                return false;
        }
        //腐蚀盔甲的逻辑在baseSlimeEntity中

        return super.hurt(source , amount);
    }


//    @Override
//    public boolean isInvulnerableTo(DamageSource damageSource) {
//        if(damageSource.isOf(DamageTypes.ARROW))
//            //永远受箭矢伤害,但damage中定义了它无法造成有效伤害,但是会造常产生击退等效果
//            return false;
//
//
//        return super.isInvulnerableTo(damageSource);
//    }

    @Override
    protected void pickUpItem(ItemEntity item) {
        ItemStack itemStack = item.getItem();
        if (isCorruptibleItems.contains(itemStack.getItem())) {
            this.onItemPickup(item);
            this.take(item, itemStack.getCount());
            //捡起多少,减去多少,若全部捡起来了,就删除这个物品实体
            itemStack.shrink(itemStack.getCount());
            this.level().playSound(null, this.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (itemStack.isEmpty()) {
                //捡起物品了自然要删除这个物品类
                item.discard();
            }
        }
    }



    @Override
    public boolean canHoldItem(ItemStack stack) {
        return true;
    }
    //捡起物品的条件之一:

    @Override
    public boolean canPickUpLoot() {
        return true;
    }






    @Override
    public void tick() {
        this.stretch = this.stretch + (this.targetStretch - this.stretch) * 0.5F;
        this.lastStretch = this.stretch;
        super.tick();

        if (this.onGround() && !this.onGroundLastTick) {
            float f = this.getDimensions(this.getPose()).width();

            float g = f / 2F;



            for (int i = 0; (float)i < f * 16.0F; i++) {
                float h = this.random.nextFloat() * (float) (Math.PI * 2);
                float j = this.random.nextFloat() * 0.5F + 0.5F;
                float k = Mth.sin(h) * g * j;
                float l = Mth.cos(h) * g * j;
                this.level().addParticle(this.getParticles(), this.getX() + (double)k, this.getY()+Math.clamp(f*this.random.nextFloat(),0,f*0.5), this.getZ() + (double)l, 0.0, 0.0, 0.0);
            }

            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.targetStretch = -0.5F;
        } else if (!this.onGround() && this.onGroundLastTick) {
            this.targetStretch = 1.0F;
        }

        this.onGroundLastTick = this.onGround();
        this.updateStretch();
    }





    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        RandomSource random = world.getRandom();
        double i = random.nextDouble();
        if(i<0.16)
            this.setSize(1, true);
        else if (i<0.5) {
            this.setSize(2, true);
        }else
            this.setSize(4, true);
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }
    private BlockPos findGroundPosition(Level world, BlockPos start) {
        // 我们从start开始向下搜索，直到世界底部或者找到合适的地面
        for (int y = start.getY(); start.getY()-y<=32&&y>-64; y--) {
            BlockPos pos = new BlockPos(start.getX(), y, start.getZ());
            if(
                    //3*3
            world.loadedAndEntityCanStandOn(pos,this)&&
            world.loadedAndEntityCanStandOn(pos.relative(Direction.Axis.X,1),this)&&
            world.loadedAndEntityCanStandOn(pos.relative(Direction.Axis.X,-1),this)&&
            world.loadedAndEntityCanStandOn(pos.relative(Direction.Axis.Z,1),this)&&
            world.loadedAndEntityCanStandOn(pos.relative(Direction.Axis.Z,-1),this)&&
            world.loadedAndEntityCanStandOn(pos.relative(Direction.Axis.X,1).relative(Direction.Axis.Z,1),this)&&
            world.loadedAndEntityCanStandOn(pos.relative(Direction.Axis.X,1).relative(Direction.Axis.Z,-1),this)&&
            world.loadedAndEntityCanStandOn(pos.relative(Direction.Axis.X,-1).relative(Direction.Axis.Z,1),this)&&
            world.loadedAndEntityCanStandOn(pos.relative(Direction.Axis.X,-1).relative(Direction.Axis.Z,-1),this)
            )
                return pos.relative(Direction.Axis.Y,1); // 返回这个方块的位置，我们将在其上方生成生物
        }

        // 如果没有找到，返回null
        return null;
    }
//    @Override
//    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
//        //只在地下世界发现悬空生成的情况,奇怪
//        if(!this.isOnGround()){
//            BlockPos pos = findGroundPosition(this.getWorld(),this.getBlockPos());
//            if(pos!=null && world.getLightLevel(pos)<7) {
//                this.setPosition(pos.getX(), pos.getY(), pos.getZ());
//            }
//            else
//                return false;
//        }
//        return super.canSpawn(world, spawnReason);
//
//    }

    public static boolean canPuddingSpawn(EntityType<PuddingSlimeEntity> type, LevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        if (MobSpawnType.isSpawner(spawnReason)) {
            return Slime.checkMobSpawnRules(type, world, spawnReason, pos, random);
        }
        if (world.getDifficulty() != Difficulty.PEACEFUL) {
            boolean bl;
            if (spawnReason == MobSpawnType.SPAWNER) {
                return Slime.checkMobSpawnRules(type, world, spawnReason, pos, random);
            }
            if (!(world instanceof WorldGenLevel)) {
                return false;
            }
            if (pos.getY() > 144) {
                return Slime.checkMobSpawnRules(type, world, spawnReason, pos, random);
            }
            if (pos.getY() < 0) {
                return Slime.checkMobSpawnRules(type, world, spawnReason, pos, random);
            }
        }
        return false;
    }

}
