package com.equilibrium.mixin.player;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {

    public ServerPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);

    }

    @Shadow private int spawnInvulnerableTime = 0;


    //    @Override
//    public void jump(){
//        super.jump();
//        this.sendMessage(Text.of("SpawnPoint is : "+this.getSpawnPointPosition()));
//        this.teleport(this.getServerWorld(),this.getSpawnPointPosition().getX(),this.getSpawnPointPosition().getY(),this.getSpawnPointPosition().getZ(),0,0);
//    }


    @Inject(method = "die",at = @At("HEAD"))
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        StateSaverAndLoader serverState;
        //创建一个持久状态类,传入当前服务器副本,再对这个持久状态类写入数据,当玩家退出时,自动把信息写回磁盘
        serverState = StateSaverAndLoader.getServerState(this.getServer());
        serverState.playerDeathTimes++;
        Collection<MobEffectInstance> effects =  this.getActiveEffects();
    }






    @Inject(method = "checkMovementStatistics",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V",ordinal = 3), cancellable = true)
    public void increaseTravelMotionStats1(double deltaX, double deltaY, double deltaZ, CallbackInfo ci){
        int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0F);
        this.causeFoodExhaustion(0.025F * (float)i * 0.01F);
//        this.sendMessage(Text.of("你正在疾跑"));
        ci.cancel();
    }


    @Inject(method = "checkMovementStatistics",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V",ordinal = 5), cancellable = true)
    public void increaseTravelMotionStats2(double deltaX, double deltaY, double deltaZ, CallbackInfo ci){
        int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0F);
        this.causeFoodExhaustion(0.0125F * (float)i * 0.01F);
//        this.sendMessage(Text.of("你正在走路"));
        ci.cancel();
    }




    @Shadow
    protected abstract boolean bedInRange(BlockPos pos, Direction direction);
    @Shadow
    protected abstract boolean bedBlocked(BlockPos pos, Direction direction);
    @Shadow
    public abstract void setRespawnPosition(ResourceKey<Level> dimension, @javax.annotation.Nullable BlockPos position, float angle, boolean forced, boolean sendMessage);
    @Shadow
    public abstract ServerLevel serverLevel();

    /**
     * 覆写 NeoForge 的 startSleepInBed，加入床颜色判断：
     * 只有当床不是白色时，才设置重生点。
     * 其他逻辑完全保留 NeoForge 原版（包括 CanPlayerSleepEvent）。
     */
    @Overwrite
    public Either<BedSleepingProblem, Unit> startSleepInBed(BlockPos at) {
        // 原版 NeoForge 的事件预检 lambda
        var vanillaResult = ((Supplier<Either<BedSleepingProblem, Unit>>) () -> {
            // Guard：防模组床无 FACING 属性
            if (!this.level().getBlockState(at).hasProperty(HorizontalDirectionalBlock.FACING)) {
                return Either.right(Unit.INSTANCE);
            }

            Direction direction = this.level().getBlockState(at).getValue(HorizontalDirectionalBlock.FACING);
            if (this.isSleeping() || !this.isAlive()) {
                return Either.left(Player.BedSleepingProblem.OTHER_PROBLEM);
            } else if (!this.level().dimensionType().natural()) {
                return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_HERE);
            } else if (!this.bedInRange(at, direction)) {
                return Either.left(Player.BedSleepingProblem.TOO_FAR_AWAY);
            } else if (this.bedBlocked(at, direction)) {
                return Either.left(Player.BedSleepingProblem.OBSTRUCTED);
            } else {
                // ========== 修改点 ==========
                // 检查床颜色，只有非白色才设置重生点
                if (this.level().getBlockState(at).getBlock() instanceof BedBlock bedBlock) {
                    if (!bedBlock.getColor().getName().equals("white")) {
                        this.setRespawnPosition(this.level().dimension(), at, this.getYRot(), false, true);
                    }
                    // 白色床：跳过设置出生点
                } else {
                    // 非床方块（极端情况）仍按原版处理
                    this.setRespawnPosition(this.level().dimension(), at, this.getYRot(), false, true);
                }
                // ========== 修改结束 ==========

                if (this.level().isDay()) {
                    return Either.left(BedSleepingProblem.NOT_POSSIBLE_NOW);
                } else {
                    if (!this.isCreative()) {
                        double d0 = 8.0;
                        double d1 = 5.0;
                        Vec3 vec3 = Vec3.atBottomCenterOf(at);
                        List<Monster> list = this.level()
                                .getEntitiesOfClass(
                                        Monster.class,
                                        new AABB(vec3.x() - 8.0, vec3.y() - 5.0, vec3.z() - 8.0, vec3.x() + 8.0, vec3.y() + 5.0, vec3.z() + 8.0),
                                        p_9062_ -> p_9062_.isPreventingPlayerRest(this)
                                );
                        if (!list.isEmpty()) {
                            return Either.left(BedSleepingProblem.NOT_SAFE);
                        }
                    }
                }
            }
            return Either.right(Unit.INSTANCE);
        }).get();

        // NeoForge 事件触发
        vanillaResult = net.neoforged.neoforge.event.EventHooks.canPlayerStartSleeping((ServerPlayer) (Object)this, at, vanillaResult);
        if (vanillaResult.left().isPresent()) {
            return vanillaResult;
        }

        // 原版睡眠后续逻辑
        {
            Either<BedSleepingProblem, Unit> either = super.startSleepInBed(at).ifRight(p_9029_ -> {
                this.awardStat(Stats.SLEEP_IN_BED);
                CriteriaTriggers.SLEPT_IN_BED.trigger((ServerPlayer) (Object)this);
            });
            if (!this.serverLevel().canSleepThroughNights()) {
                this.displayClientMessage(Component.translatable("sleep.not_possible"), true);
            }
            ((ServerLevel) this.level()).updateSleepingPlayerList();
            return either;
        }
    }
}
