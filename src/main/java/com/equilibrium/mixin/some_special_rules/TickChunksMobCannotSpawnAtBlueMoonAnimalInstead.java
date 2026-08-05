package com.equilibrium.mixin.some_special_rules;

import com.equilibrium.server_and_client.server.moonphase_tasks.WorldMoonPhasesSelector;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Objects;

import static com.equilibrium.server_and_client.server.event.SleepChunkLoaderEvents.allPlayersDemandToLoadChunks;

@Mixin(ServerChunkCache.class)
public abstract class TickChunksMobCannotSpawnAtBlueMoonAnimalInstead extends ChunkSource {

    @Shadow
    @Final
    ServerLevel level;

    @Shadow
    @Final
    public ChunkMap chunkMap;

    @Shadow
    private boolean spawnEnemies;
    @Shadow
    private boolean spawnFriendlies;


    @Inject(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;shuffle(Ljava/util/List;Lnet/minecraft/util/RandomSource;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void tickChunks(CallbackInfo ci, long l, long m, ProfilerFiller profiler, List<ServerChunkCache.ChunkAndHolder> list, int i, NaturalSpawner.SpawnState info, boolean bl) {
        ci.cancel();
        //随机刻问题定位:chunk刻被加速了16倍率,现在所有randomTick也加速了16倍,20260118
        //请把所有作物生长速度减慢到原来的1/16
//        int j = 16 * this.world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
        int j = this.level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        boolean bl2 = this.level.getLevelData().getGameTime() % 400L == 0L;
        for (ServerChunkCache.ChunkAndHolder chunkWithHolder : list) {
            LevelChunk worldChunk2 = chunkWithHolder.chunk();
            ChunkPos chunkPos = worldChunk2.getPos();
            if (
                    (allPlayersDemandToLoadChunks.contains(chunkPos))
                    ||
                    (this.level.isNaturalSpawningAllowed(chunkPos) && this.chunkMap.anyPlayerCloseEnoughForSpawning(chunkPos))
            ){

                worldChunk2.incrementInhabitedTime(m);
                if (bl && (this.spawnEnemies || this.spawnFriendlies) && this.level.getWorldBorder().isWithinBounds(chunkPos)) {
                    //蓝月不刷怪
                    if(Objects.equals(WorldMoonPhasesSelector.calculateMoonType(this.level), "blueMoon"))
                        NaturalSpawner.spawnForChunk(this.level, worldChunk2, info, this.spawnFriendlies, false, bl2);
                    else
                        NaturalSpawner.spawnForChunk(this.level, worldChunk2, info, false, this.spawnEnemies, bl2);

                }
                if (this.level.shouldTickBlocksAt(chunkPos.toLong())) {
                    this.level.tickChunk(worldChunk2, j);
                }
            }
        }




        profiler.popPush("customSpawners");
        if (bl) {
            this.level.tickCustomSpawners(this.spawnEnemies, this.spawnFriendlies);
        }
        profiler.popPush("broadcast");
        list.forEach(chunk -> chunk.holder().broadcastChanges(chunk.chunk()));
        profiler.pop();
        profiler.pop();

    }


}















