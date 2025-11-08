package com.equilibrium.mixin.tables;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobSpawnerLogic.class)
public abstract class SpawnerLogicMixin {



    @Unique
    private int totalSpawnedCount = 0;

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void onReadNbt(World world, BlockPos pos, NbtCompound nbt, CallbackInfo ci) {
        this.totalSpawnedCount = nbt.getInt("totalSpawnedCount");
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void onWriteNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        nbt.putInt("totalSpawnedCount", this.totalSpawnedCount);
    }


    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private void checkDeactivated(ServerWorld world, BlockPos pos, CallbackInfo ci) {
        if (totalSpawnedCount >= 10) {
            ci.cancel();  // 如果已停用，取消整个tick
        }
    }

    @Shadow
    private Entity renderedEntity;
    @Shadow
    private double rotation;
    @Shadow
    private double lastRotation;
    @Shadow
    private MobSpawnerEntry spawnEntry;

    @Shadow protected abstract void setSpawnEntry(@Nullable World world, BlockPos pos, MobSpawnerEntry spawnEntry);

    @Inject(method = "clientTick", at = @At("HEAD"), cancellable = true)
    private void onClientTick(World world, BlockPos pos, CallbackInfo ci) {
        // 获取方块实体并检查生成计数
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MobSpawnerBlockEntity spawnerBlockEntity) {
            // 读取NBT数据中的计数
            RegistryWrapper.WrapperLookup registryLookup = world.getRegistryManager();
            NbtCompound nbt = spawnerBlockEntity.createNbt(registryLookup);
            if (nbt.contains("totalSpawnedCount") && nbt.getInt("totalSpawnedCount") >= 10) {
                // 停止所有客户端渲染
                this.lastRotation = 0;
                this.rotation=0;
                ci.cancel();
            }
        }
    }


    @Inject(method = "serverTick",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;syncWorldEvent(ILnet/minecraft/util/math/BlockPos;I)V"))
    private void onEntitySpawned(ServerWorld world, BlockPos pos, CallbackInfo ci) {

        // 增加计数器,成功生成一只怪物
        //退出时、自动保存时会进行一次更新
        this.totalSpawnedCount++;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.markDirty();
        }


        // 强制立即更新
        if (this.totalSpawnedCount >= 10) {
            // 触发方块更新，这会自动同步到所有客户端
            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
            // 标记方块实体需要保存
            BlockEntity blockEntity2 = world.getBlockEntity(pos);
            if (blockEntity2 != null) {
                blockEntity2.markDirty();
            }

        }

    }
}