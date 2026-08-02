package com.equilibrium.entity.goal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Map;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_ADVANCE_ANIMAL_AI;

public class ConstantFleePlayerGoal extends Goal {
    protected final PathfinderMob mob;
    protected Player targetPlayer;
    protected final float fleeDistance;
    protected final double walkSpeed;
    protected final double runSpeed;
    protected int cooldown = 0;
    protected int panicTicks = 0;
    protected boolean isPanicking = false;
    Map<EntityType<?>,SoundEvent> soundEventMap;

    // 像村民一样，有视线检查和记忆
    private int lastSeenTimer = 0;
    private Vec3 lastSeenPos = null;

    public ConstantFleePlayerGoal(PathfinderMob mob, float distance, double walkSpeed, double runSpeed) {
        this.mob = mob;
        this.fleeDistance = distance;
        this.walkSpeed = walkSpeed;
        this.runSpeed = runSpeed;
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.soundEventMap = Map.of(
                EntityType.COW,SoundEvents.COW_HURT,
                EntityType.CHICKEN,SoundEvents.CHICKEN_HURT,
                EntityType.PIG,SoundEvents.PIG_HURT,
                EntityType.SHEEP,SoundEvents.SHEEP_HURT,
                EntityType.MOOSHROOM,SoundEvents.COW_HURT
        );
    }

    @Override
    public boolean canUse() {
        //高级动物AI:寻路算法方面,
        if(this.mob.level() instanceof ServerLevel serverWorld)
            if(!getGameBooleanRuleFromServer(ENABLE_ADVANCE_ANIMAL_AI,serverWorld.getServer()))
                return false;

        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        // 随机冷却，避免每帧都检查
        cooldown = this.mob.getRandom().nextInt(20);

        // 寻找最近的玩家
        this.targetPlayer = this.mob.level().getNearestPlayer(
                this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                this.fleeDistance, false
        );

        if (this.targetPlayer == null) {
            this.isPanicking = false;
            return false;
        }

        //被玩家打一定逃跑,被怪物打一定逃跑
        if(this.mob.getLastHurtByMob() instanceof Player || this.mob.getLastHurtByMob() instanceof Player){
            return true;
        }


        //先检查类型
        if (!this.mob.level().getEntities(
                this.mob.getType(),
                this.mob.getBoundingBox().inflate(12.0),
                ally -> {
                    if (ally == this.mob)
                        return false;

                    // 检查是否为 LivingEntity
                    if (!(ally instanceof LivingEntity livingAlly))
                        return false;

                    // 获取当前水平速度
                    double currentSpeed = ally.getDeltaMovement().horizontalDistance();


                    // 计算阈值
                    double threshold = this.mob.level().getDayTime()/24000L >=16? 0.17f:0.1f;

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
        if(this.mob.level().getNearestPlayer(
                this.mob.getX(),
                this.mob.getY(),
                this.mob.getZ(),
                16.0, // 检测范围
                player -> {
                    // 检查速度,没有潜行该生物一定逃跑
                    return !player.isShiftKeyDown();
                }
        )!=null)
            return true;





        // 检查玩家是否可见（像村民一样）
        if (!this.mob.hasLineOfSight(this.targetPlayer)) {
            // 如果之前看到过，还能记得一小段时间
            if (lastSeenTimer > 0) {
                lastSeenTimer--;
                this.isPanicking = true;
                return true;
            }
            return false;
        }

        // 看到玩家，更新最后看到的位置和时间
        this.lastSeenPos = this.targetPlayer.position();
        this.lastSeenTimer = 40; // 记住2秒

        this.isPanicking = true;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
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
        if (this.targetPlayer != null && this.mob.hasLineOfSight(this.targetPlayer)) {
            this.lastSeenPos = this.targetPlayer.position();
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
        this.mob.playSound(soundEventMap.getOrDefault(this.mob.getType(),SoundEvents.BAT_AMBIENT), 1.0F, 1.0F);
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

        double distanceSquared = this.mob.distanceToSqr(this.targetPlayer);

        // 计算逃离方向
        Vec3 awayFromPlayer;
        if (this.lastSeenPos != null) {
            // 逃离最后看到的位置
            awayFromPlayer = this.mob.position().subtract(this.lastSeenPos).normalize();
        } else {
            awayFromPlayer = this.mob.position().subtract(this.targetPlayer.position()).normalize();
        }

        // 设置目标位置
        double targetX = this.mob.getX() + awayFromPlayer.x * 10;
        double targetY = this.mob.getY();
        double targetZ = this.mob.getZ() + awayFromPlayer.z * 10;

        // 设置移动速度
        double currentSpeed = (distanceSquared < 49.0) ? this.runSpeed : this.walkSpeed;
        this.mob.getNavigation().moveTo(targetX, targetY, targetZ, currentSpeed);

        // 像村民一样，边跑边回头看
        if (panicTicks % 20 == 0) {
            this.mob.getLookControl().setLookAt(this.targetPlayer, 30.0F, 30.0F);
        }
    }
}