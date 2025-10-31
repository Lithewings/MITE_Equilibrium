package com.equilibrium.entity.mob;

import com.equilibrium.item.ModItems;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.equilibrium.util.XpHashMap.getXpForLevel;

public class PuddingSlimeEntity extends BaseSlimeEntity{
    public PuddingSlimeEntity(EntityType<? extends BaseSlimeEntity> entityType, World world) {
        super(entityType, world);
    }
    private boolean onGroundLastTick;
    @Override
    protected ParticleEffect getParticles() {
        return new ItemStackParticleEffect(ParticleTypes.ITEM, ModItems.PUDDING_SLIME_BALL.getDefaultStack());
    }


    @Override
    //最小尺寸也可以攻击玩家
    protected boolean canAttack() {
        return this.canMoveVoluntarily();
    }


    @Override
    protected int getXpToDrop(){
        int i = this.getSize();
        return getXpForLevel(i);
    }

    //不管什么情况
    @Override
    public void tickMovement() {
        super.tickMovement();
        this.getWorld().getProfiler().push("looting");
        if (!this.getWorld().isClient && this.canPickUpLoot() && this.isAlive() && !this.dead && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
        )
        {
            Vec3i vec3i = this.getItemPickUpRangeExpander();

            //不需要冷却时间,玩家丢出来直接就能被吸走 !itemEntity.cannotPickup()
            for (Entity entity : this.getWorld()
                    .getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox().expand((double)vec3i.getX(), (double)vec3i.getY(), (double)vec3i.getZ()))) {
                if (entity instanceof ItemEntity itemEntity && !itemEntity.isRemoved() && !itemEntity.getStack().isEmpty()  && this.canGather(itemEntity.getStack())) {
                    this.loot(itemEntity);
                }


          }
        }

        this.getWorld().getProfiler().pop();
    }

    @Override
    public void calculateDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.calculateDimensions();
        this.setPosition(d, e, f);
    }



    @Override
    public boolean damage(DamageSource source, float amount) {
        //箭矢只对史莱姆造成0.1的伤害
        if(source.isOf(DamageTypes.ARROW))
            return super.damage(source,0f);
        //
        if( source.isOf(DamageTypes.PLAYER_ATTACK)) {
            PlayerEntity player = (PlayerEntity)  source.getAttacker();
            ItemStack weapon = player.getMainHandStack();
            //以5%的进度腐蚀玩家的武器
            if (isCorruptibleItems.contains(weapon.getItem())) {
                weapon.damage((int) (weapon.getMaxDamage() * 0.05), player, EquipmentSlot.MAINHAND);
                this.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

                // --------------- 新增粒子生成逻辑 ---------------
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    // 获取玩家视线方向的位置（面前1米处）
                    Vec3d playerPos = player.getPos();
                    Vec3d lookVec = player.getRotationVec(1.0F); // 获取玩家视线方向
                    Vec3d particlePos = playerPos.add(lookVec.multiply(1.0)); // 玩家面前1米处

                    // 生成腐蚀粒子
                    serverWorld.spawnParticles(
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
            if (!weapon.hasEnchantments())
                return false;
        }
        //腐蚀盔甲的逻辑在baseSlimeEntity中

        return super.damage(source , amount);
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
    protected void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (isCorruptibleItems.contains(itemStack.getItem())) {
            this.triggerItemPickedUpByEntityCriteria(item);
            this.sendPickup(item, itemStack.getCount());
            //捡起多少,减去多少,若全部捡起来了,就删除这个物品实体
            itemStack.decrement(itemStack.getCount());
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.PLAYERS, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (itemStack.isEmpty()) {
                //捡起物品了自然要删除这个物品类
                item.discard();
            }
        }
    }



    @Override
    public boolean canPickupItem(ItemStack stack) {
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

        if (this.isOnGround() && !this.onGroundLastTick) {
            float f = this.getDimensions(this.getPose()).width();

            float g = f / 2F;



            for (int i = 0; (float)i < f * 16.0F; i++) {
                float h = this.random.nextFloat() * (float) (Math.PI * 2);
                float j = this.random.nextFloat() * 0.5F + 0.5F;
                float k = MathHelper.sin(h) * g * j;
                float l = MathHelper.cos(h) * g * j;
                this.getWorld().addParticle(this.getParticles(), this.getX() + (double)k, this.getY()+Math.clamp(f*this.random.nextFloat(),0,f*0.5), this.getZ() + (double)l, 0.0, 0.0, 0.0);
            }

            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.targetStretch = -0.5F;
        } else if (!this.isOnGround() && this.onGroundLastTick) {
            this.targetStretch = 1.0F;
        }

        this.onGroundLastTick = this.isOnGround();
        this.updateStretch();
    }





    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        Random random = world.getRandom();
        double i = random.nextDouble();
        if(i<0.16)
            this.setSize(1, true);
        else if (i<0.5) {
            this.setSize(2, true);
        }else
            this.setSize(4, true);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }
    private BlockPos findGroundPosition(World world, BlockPos start) {
        // 我们从start开始向下搜索，直到世界底部或者找到合适的地面
        for (int y = start.getY(); start.getY()-y<=32&&y>-64; y--) {
            BlockPos pos = new BlockPos(start.getX(), y, start.getZ());
            if(
                    //3*3
            world.isTopSolid(pos,this)&&
            world.isTopSolid(pos.offset(Direction.Axis.X,1),this)&&
            world.isTopSolid(pos.offset(Direction.Axis.X,-1),this)&&
            world.isTopSolid(pos.offset(Direction.Axis.Z,1),this)&&
            world.isTopSolid(pos.offset(Direction.Axis.Z,-1),this)&&
            world.isTopSolid(pos.offset(Direction.Axis.X,1).offset(Direction.Axis.Z,1),this)&&
            world.isTopSolid(pos.offset(Direction.Axis.X,1).offset(Direction.Axis.Z,-1),this)&&
            world.isTopSolid(pos.offset(Direction.Axis.X,-1).offset(Direction.Axis.Z,1),this)&&
            world.isTopSolid(pos.offset(Direction.Axis.X,-1).offset(Direction.Axis.Z,-1),this)
            )
                return pos.offset(Direction.Axis.Y,1); // 返回这个方块的位置，我们将在其上方生成生物
        }

        // 如果没有找到，返回null
        return null;
    }
    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        //只在地下世界发现悬空生成的情况,奇怪
        if(!this.isOnGround()){
            BlockPos pos = findGroundPosition(this.getWorld(),this.getBlockPos());
            if(pos!=null)
                this.setPosition(pos.getX(),pos.getY(),pos.getZ());
            else
                return false;
        }
        return super.canSpawn(world, spawnReason);

    }

}
