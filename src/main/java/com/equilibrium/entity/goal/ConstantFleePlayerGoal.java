package com.equilibrium.entity.goal;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Map;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.*;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_ADVANCE_ANIMAL_AI;

public class ConstantFleePlayerGoal extends Goal {
    protected final PathAwareEntity mob;
    protected PlayerEntity targetPlayer;
    protected final float fleeDistance;
    protected final double walkSpeed;
    protected final double runSpeed;
    protected int cooldown = 0;
    protected int panicTicks = 0;
    protected boolean isPanicking = false;
    Map<EntityType<?>,SoundEvent> soundEventMap;

    // 像村民一样，有视线检查和记忆
    private int lastSeenTimer = 0;
    private Vec3d lastSeenPos = null;

    public ConstantFleePlayerGoal(PathAwareEntity mob, float distance, double walkSpeed, double runSpeed) {
        this.mob = mob;
        this.fleeDistance = distance;
        this.walkSpeed = walkSpeed;
        this.runSpeed = runSpeed;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
        this.soundEventMap = Map.of(
                EntityType.COW,SoundEvents.ENTITY_COW_HURT,
                EntityType.CHICKEN,SoundEvents.ENTITY_CHICKEN_HURT,
                EntityType.PIG,SoundEvents.ENTITY_PIG_HURT,
                EntityType.SHEEP,SoundEvents.ENTITY_SHEEP_HURT,
                EntityType.MOOSHROOM,SoundEvents.ENTITY_COW_HURT
        );
    }

    @Override
    public boolean canStart() {
        //高级动物AI:寻路算法方面,
        if(this.mob.getWorld() instanceof ServerWorld serverWorld)
            if(!getGameBooleanRuleFromServer(ENABLE_ADVANCE_ANIMAL_AI,serverWorld.getServer()))
                return false;

        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        // 随机冷却，避免每帧都检查
        cooldown = this.mob.getRandom().nextInt(20);

        // 寻找最近的玩家
        this.targetPlayer = this.mob.getWorld().getClosestPlayer(
                this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                this.fleeDistance, false
        );

        if (this.targetPlayer == null) {
            this.isPanicking = false;
            return false;
        }

        //被玩家打一定逃跑,被怪物打一定逃跑
        if(this.mob.getAttacker() instanceof PlayerEntity || this.mob.getAttacker() instanceof PlayerEntity){
            return true;
        }


        //先检查类型
        if (!this.mob.getWorld().getEntitiesByType(
                this.mob.getType(),
                this.mob.getBoundingBox().expand(12.0),
                ally -> {
                    if (ally == this.mob)
                        return false;

                    // 检查是否为 LivingEntity
                    if (!(ally instanceof LivingEntity livingAlly))
                        return false;

                    // 获取当前水平速度
                    double currentSpeed = ally.getVelocity().horizontalLength();


                    // 计算阈值
                    double threshold = this.mob.getWorld().getTimeOfDay()/24000L >=16? 0.17f:0.1f;

                    return currentSpeed > threshold;
                }
        ).isEmpty()) {
            return true;
        }

        //至少拥有一件皮革装备时可以避免被讨厌
        if (this.targetPlayer.getInventory().armor.stream().anyMatch(itemStack ->
                itemStack.getItem() == Items.LEATHER_HELMET ||
                        itemStack.getItem() == Items.LEATHER_CHESTPLATE ||
                        itemStack.getItem() == Items.LEATHER_LEGGINGS ||
                        itemStack.getItem() == Items.LEATHER_BOOTS)){
            return false;
        }

        //否则必须潜行
        if(this.mob.getWorld().getClosestPlayer(
                this.mob.getX(),
                this.mob.getY(),
                this.mob.getZ(),
                16.0, // 检测范围
                player -> {
                    // 检查速度,没有潜行该生物一定逃跑
                    return !player.isSneaking();
                }
        )!=null)
            return true;





        // 检查玩家是否可见（像村民一样）
        if (!this.mob.canSee(this.targetPlayer)) {
            // 如果之前看到过，还能记得一小段时间
            if (lastSeenTimer > 0) {
                lastSeenTimer--;
                this.isPanicking = true;
                return true;
            }
            return false;
        }

        // 看到玩家，更新最后看到的位置和时间
        this.lastSeenPos = this.targetPlayer.getPos();
        this.lastSeenTimer = 40; // 记住2秒

        this.isPanicking = true;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        if (!this.isPanicking) {
            return false;
        }

        // 恐慌持续一段时间
//        panicTicks++;
//        if (panicTicks > 100) { // 5秒后冷静下来
//            this.isPanicking = false;
//            panicTicks = 0;
//            return false;
//        }

        // 如果还能看到玩家，继续恐慌
        if (this.targetPlayer != null && this.mob.canSee(this.targetPlayer)) {
            this.lastSeenPos = this.targetPlayer.getPos();
            this.lastSeenTimer = 40;
            return true;
        }

        // 看不到玩家但还记得位置e
        if (this.lastSeenTimer > 0) {
            this.lastSeenTimer--;
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        this.panicTicks = 0;
        this.isPanicking = true;

        // 播放恐慌音效,空位用蝙蝠声占位
        this.mob.playSound(soundEventMap.getOrDefault(this.mob.getType(),SoundEvents.ENTITY_BAT_AMBIENT), 1.0F, 1.0F);
    }


    @Override
    public void stop() {
        this.targetPlayer = null;
        this.isPanicking = false;
        this.panicTicks = 0;
        this.lastSeenTimer = 0;
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.targetPlayer == null) {
            return;
        }

        double distanceSquared = this.mob.squaredDistanceTo(this.targetPlayer);

        // 计算逃离方向
        Vec3d awayFromPlayer;
        if (this.lastSeenPos != null) {
            // 逃离最后看到的位置
            awayFromPlayer = this.mob.getPos().subtract(this.lastSeenPos).normalize();
        } else {
            awayFromPlayer = this.mob.getPos().subtract(this.targetPlayer.getPos()).normalize();
        }

        // 设置目标位置
        double targetX = this.mob.getX() + awayFromPlayer.x * 10;
        double targetY = this.mob.getY();
        double targetZ = this.mob.getZ() + awayFromPlayer.z * 10;

        // 设置移动速度
        double currentSpeed = (distanceSquared < 49.0) ? this.runSpeed : this.walkSpeed;
        this.mob.getNavigation().startMovingTo(targetX, targetY, targetZ, currentSpeed);

        // 像村民一样，边跑边回头看
        if (panicTicks % 20 == 0) {
            this.mob.getLookControl().lookAt(this.targetPlayer, 30.0F, 30.0F);
        }
    }
}