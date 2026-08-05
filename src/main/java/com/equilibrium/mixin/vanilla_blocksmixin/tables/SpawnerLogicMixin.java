package com.equilibrium.mixin.vanilla_blocksmixin.tables;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseSpawner.class)
public abstract class SpawnerLogicMixin {



    @Unique
    private int totalSpawnedCount = 0;

    @Inject(method = "load", at = @At("TAIL"))
    private void onReadNbt(Level world, BlockPos pos, CompoundTag nbt, CallbackInfo ci) {
        this.totalSpawnedCount = nbt.getInt("totalSpawnedCount");
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void onWriteNbt(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        nbt.putInt("totalSpawnedCount", this.totalSpawnedCount);
    }


    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private void checkDeactivated(ServerLevel world, BlockPos pos, CallbackInfo ci) {
        if (totalSpawnedCount >= 10) {
            ci.cancel();  // 如果已停用，取消整个tick
        }
    }


    @Shadow
    private double spin;
    @Shadow
    private double oSpin;

    @Inject(method = "clientTick", at = @At("HEAD"), cancellable = true)
    private void onClientTick(Level world, BlockPos pos, CallbackInfo ci) {
        // 获取方块实体并检查生成计数
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SpawnerBlockEntity spawnerBlockEntity) {
            // 读取NBT数据中的计数
            HolderLookup.Provider registryLookup = world.registryAccess();
            CompoundTag nbt = spawnerBlockEntity.saveWithoutMetadata(registryLookup);
            if (nbt.contains("totalSpawnedCount") && nbt.getInt("totalSpawnedCount") >= 10) {
                // 停止所有客户端渲染
                this.oSpin = 0;
                this.spin=0;
                ci.cancel();
            }
        }
    }


    @Inject(method = "serverTick",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;levelEvent(ILnet/minecraft/core/BlockPos;I)V"))
    private void onEntitySpawned(ServerLevel world, BlockPos pos, CallbackInfo ci) {

        // 增加计数器,成功生成一只怪物
        //退出时、自动保存时会进行一次更新
        this.totalSpawnedCount++;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.setChanged();
        }


        // 强制立即更新
        if (this.totalSpawnedCount >= 10) {
            // 触发方块更新，这会自动同步到所有客户端
            world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), Block.UPDATE_ALL);
            // 标记方块实体需要保存
            BlockEntity blockEntity2 = world.getBlockEntity(pos);
            if (blockEntity2 != null) {
                blockEntity2.setChanged();
            }

        }

    }
}