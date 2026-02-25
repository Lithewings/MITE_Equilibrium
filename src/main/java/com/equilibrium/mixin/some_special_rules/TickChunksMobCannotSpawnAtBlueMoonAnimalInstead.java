package com.equilibrium.mixin.some_special_rules;

import com.equilibrium.event.moon_event.WorldMoonPhasesSelector;
import net.minecraft.server.world.*;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.*;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Objects;

import static com.equilibrium.event.SleepChunkLoader.allPlayersDemandToLoadChunks;

@Mixin(ServerChunkManager.class)
public abstract class TickChunksMobCannotSpawnAtBlueMoonAnimalInstead extends ChunkManager {

    @Shadow
    @Final
    ServerWorld world;

    @Shadow
    @Final
    public ServerChunkLoadingManager chunkLoadingManager;
    @Shadow
    private long lastMobSpawningTime;
    @Shadow
    private boolean spawnMonsters;
    @Shadow
    private boolean spawnAnimals;
    @Shadow
    @Final
    private ChunkTicketManager ticketManager;
    @Shadow
    private SpawnHelper.Info spawnInfo;


    @Shadow public abstract <T> void addTicket(ChunkTicketType<T> ticketType, ChunkPos pos, int radius, T argument);

    @Inject(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;shuffle(Ljava/util/List;Lnet/minecraft/util/math/random/Random;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void tickChunks(CallbackInfo ci, long l, long m, Profiler profiler, List<ServerChunkManager.ChunkWithHolder> list, int i, SpawnHelper.Info info, boolean bl) {
        ci.cancel();
        //随机刻问题定位:chunk刻被加速了16倍率,现在所有randomTick也加速了16倍,20260118
        //请把所有作物生长速度减慢到原来的1/16
//        int j = 16 * this.world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
        int j = this.world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
        boolean bl2 = this.world.getLevelProperties().getTime() % 400L == 0L;
        for (ServerChunkManager.ChunkWithHolder chunkWithHolder : list) {
            WorldChunk worldChunk2 = chunkWithHolder.chunk;
            ChunkPos chunkPos = worldChunk2.getPos();
            if (
                    (allPlayersDemandToLoadChunks.contains(chunkPos))
                    ||
                    (this.world.shouldTick(chunkPos) && this.chunkLoadingManager.shouldTick(chunkPos))
            ){

                worldChunk2.increaseInhabitedTime(m);
                if (bl && (this.spawnMonsters || this.spawnAnimals) && this.world.getWorldBorder().contains(chunkPos)) {
                    //蓝月不刷怪
                    if(Objects.equals(WorldMoonPhasesSelector.calculateMoonType(this.world), "blueMoon"))
                        SpawnHelper.spawn(this.world, worldChunk2, info, this.spawnAnimals, false, bl2);
                    else
                        SpawnHelper.spawn(this.world, worldChunk2, info, false, this.spawnMonsters, bl2);

                }
                if (this.world.shouldTickBlocksInChunk(chunkPos.toLong())) {
                    this.world.tickChunk(worldChunk2, j);
                }
            }
        }




        profiler.swap("customSpawners");
        if (bl) {
            this.world.tickSpawners(this.spawnMonsters, this.spawnAnimals);
        }
        profiler.swap("broadcast");
        list.forEach(chunk -> chunk.holder.flushUpdates(chunk.chunk));
        profiler.pop();
        profiler.pop();

    }


}















