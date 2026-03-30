package com.equilibrium.mixin.player;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BedBlock;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Shadow public abstract @Nullable BlockPos getSpawnPointPosition();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);

    }

    @Shadow private int joinInvulnerabilityTicks = 0;


    //    @Override
//    public void jump(){
//        super.jump();
//        this.sendMessage(Text.of("SpawnPoint is : "+this.getSpawnPointPosition()));
//        this.teleport(this.getServerWorld(),this.getSpawnPointPosition().getX(),this.getSpawnPointPosition().getY(),this.getSpawnPointPosition().getZ(),0,0);
//    }


    @Shadow public abstract ServerWorld getServerWorld();

    @Shadow public abstract void sendMessage(Text message, boolean overlay);

    @Shadow public abstract void sendMessage(Text message);

    @Inject(method = "onDeath",at = @At("HEAD"))
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        StateSaverAndLoader serverState;
        //创建一个持久状态类,传入当前服务器副本,再对这个持久状态类写入数据,当玩家退出时,自动把信息写回磁盘
        serverState = StateSaverAndLoader.getServerState(this.getServer());
        serverState.playerDeathTimes++;
    }






    @Inject(method = "increaseTravelMotionStats",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;addExhaustion(F)V",ordinal = 3), cancellable = true)
    public void increaseTravelMotionStats1(double deltaX, double deltaY, double deltaZ, CallbackInfo ci){
        int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0F);
        this.addExhaustion(0.025F * (float)i * 0.01F);
//        this.sendMessage(Text.of("你正在疾跑"));
        ci.cancel();
    }


    @Inject(method = "increaseTravelMotionStats",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;addExhaustion(F)V",ordinal = 5), cancellable = true)
    public void increaseTravelMotionStats2(double deltaX, double deltaY, double deltaZ, CallbackInfo ci){
        int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0F);
        this.addExhaustion(0.0125F * (float)i * 0.01F);
//        this.sendMessage(Text.of("你正在走路"));
        ci.cancel();
    }




    @Shadow
    public abstract void setSpawnPoint(RegistryKey<World> dimension, @Nullable BlockPos pos, float angle, boolean forced, boolean sendMessage);




    @Inject(method = "trySleep",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSpawnPoint(Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/util/math/BlockPos;FZZ)V"), cancellable = true)
    public void trySleep(BlockPos pos, CallbackInfoReturnable<Either<SleepFailureReason, Unit>> cir) {
        cir.cancel();
        BedBlock block = (BedBlock)this.getWorld().getBlockState(pos).getBlock();
        if(!block.getColor().getName().equals("white"))
            //白色床不设置出生点
            this.setSpawnPoint(this.getWorld().getRegistryKey(), pos, this.getYaw(), false, true);


        if (this.getWorld().isDay()) {
            cir.setReturnValue(Either.left(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW));
        } else {
            if (!this.isCreative()) {
                double d = 8.0;
                double e = 5.0;
                Vec3d vec3d = Vec3d.ofBottomCenter(pos);
                List<HostileEntity> list = this.getWorld()
                        .getEntitiesByClass(
                                HostileEntity.class,
                                new Box(vec3d.getX() - 8.0, vec3d.getY() - 5.0, vec3d.getZ() - 8.0, vec3d.getX() + 8.0, vec3d.getY() + 5.0, vec3d.getZ() + 8.0),
                                entity -> entity.isAngryAt(this)
                        );
                if (!list.isEmpty()) {
                    cir.setReturnValue(Either.left(PlayerEntity.SleepFailureReason.NOT_SAFE));
                }
            }

            Either<PlayerEntity.SleepFailureReason, Unit> either = super.trySleep(pos).ifRight(unit -> {
                this.incrementStat(Stats.SLEEP_IN_BED);
                Criteria.SLEPT_IN_BED.trigger((ServerPlayerEntity)(Object) this);
            });
            if (!this.getServerWorld().isSleepingEnabled()) {
                this.sendMessage(Text.translatable("sleep.not_possible"), true);
            }

            ((ServerWorld)this.getWorld()).updateSleepingPlayers();
            cir.setReturnValue(either);
        }
    }
}
