package com.equilibrium.server_and_client.server.event;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class SleepChunkLoaderEvents {




    public static final int RADIUS = 8;

    //加载区块
    private static final TicketType<BlockPos> SLEEP_TICKET = TicketType.create(
            MOD_ID + "sleep_ticket", (a, b) -> 0
    );





    public static ConcurrentHashMap< UUID , BlockPos > mapForEachPlayerSleepPos = new ConcurrentHashMap<>();

    //key:玩家id
    //value:一个玩家需要常加载(1.21下需要应用随机刻)的所有区块
    //即每个单独玩家想要常加载的区块映射
    public static ConcurrentHashMap< UUID , Set<ChunkPos> > mapForEachPlayerDemandToLoadChunks = new ConcurrentHashMap<>();


    public static Set<ChunkPos> allPlayersDemandToLoadChunks = Collections.synchronizedSet(new HashSet<>());

    //将所有玩家想要加载的区块混在一起去重
    public static Set<ChunkPos> mergeAllPlayerChunkSets(ConcurrentHashMap< UUID , Set<ChunkPos> > shouldTickTheseChunk) {
        // 流操作遍历所有玩家的Set，扁平化合并为一个全局去重Set
        // Collectors.toSet() 默认为HashSet，自动去重；如需有序可改用 Collectors.toCollection(LinkedHashSet::new)
        return shouldTickTheseChunk.values().stream()
                .filter(Objects::nonNull)
                .flatMap(Set::stream) // 扁平化：将多个Set<ChunkPos>转为单个Stream<ChunkPos>
                .collect(Collectors.toSet()); // 收集为去重的Set
    }


    public static void registerSleepEvents() {
        // 玩家睡觉触发
        EntitySleepEvents.START_SLEEPING.register((entity, bedPos) -> {
            if (entity instanceof ServerPlayer player) {
                //如果此前玩家已经设定了一个睡觉常加载位置,就删除所有历史记录
                if(mapForEachPlayerSleepPos.containsKey(player.getUUID())){
                    //删除加载区块的资格,重新来
                    chunksOutOfTheWorld(mapForEachPlayerSleepPos.get(player.getUUID()),player);
                    mapForEachPlayerSleepPos.remove(player.getUUID());
                    mapForEachPlayerDemandToLoadChunks.remove(player.getUUID());
                    
                }
                //问题:两个玩家拥有重叠的常加载区块,但其中一个退出卸载,会全额将他的区块卸载,导致另一个玩家的常加载区块数变少了





                chunksJoinInTheWorld(bedPos, player);


                player.displayClientMessage(Component.literal("睡眠区域已加载"), false);
                //记录睡眠位置
                mapForEachPlayerSleepPos.put(player.getUUID(),bedPos);

                //需要常加载到随机刻上的所有ChunkPos
                Set<ChunkPos> chunkPosSet = getLoadChunkSet(bedPos,RADIUS);
                //标清楚是哪个玩家请求的ChunkPos集合
                mapForEachPlayerDemandToLoadChunks.put(player.getUUID(),chunkPosSet);
                //进行去重,也是一次更新
                allPlayersDemandToLoadChunks = mergeAllPlayerChunkSets(mapForEachPlayerDemandToLoadChunks);
            }
        });

        // 玩家退出清理：
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {

            if(mapForEachPlayerSleepPos.containsKey(handler.player.getUUID())){
                //删除加载区块的资格
                chunksOutOfTheWorld(mapForEachPlayerSleepPos.get(handler.player.getUUID()),handler.player);
            }
            mapForEachPlayerSleepPos.remove(handler.getPlayer().getUUID());
            mapForEachPlayerDemandToLoadChunks.remove(handler.getPlayer().getUUID());
            allPlayersDemandToLoadChunks = mergeAllPlayerChunkSets(mapForEachPlayerDemandToLoadChunks);
        });
    }








    private static void chunksJoinInTheWorld(BlockPos bedPos, ServerPlayer player) {
        //以下用来先加载区块,再实现常加载逻辑
        ServerChunkCache manager = player.serverLevel().getChunkSource();
        // 添加新票证
        manager.addRegionTicket(
                SLEEP_TICKET,
                new ChunkPos(bedPos),
                RADIUS,
                bedPos
        );
    }

    private static void chunksOutOfTheWorld(BlockPos bedPos, ServerPlayer player) {
        //以下用来先加载区块,再实现常加载逻辑
        ServerChunkCache manager = player.serverLevel().getChunkSource();
        // 删除票证
        manager.removeRegionTicket(
                SLEEP_TICKET,
                new ChunkPos(bedPos),
                RADIUS,
                bedPos
        );
    }


    public static Set<ChunkPos> getLoadChunkSet(BlockPos centerBlockPos, int radius) {

        ChunkPos centerChunk = new ChunkPos(centerBlockPos);
        Set<ChunkPos> actualLoadChunks = new HashSet<>();

        // 步骤2：切比雪夫距离遍历（正方形范围）
        // 遍历x方向：中心x ± radius
        for (int dx = -radius; dx <= radius; dx++) {
            // 遍历z方向：中心z ± radius
            for (int dz = -radius; dz <= radius; dz++) {
                // 核心判定：切比雪夫距离（max(|dx|,|dz|) ≤ radius，此循环天然满足，无需额外判断）
                ChunkPos loadChunk = new ChunkPos(centerChunk.x + dx, centerChunk.z + dz);
                actualLoadChunks.add(loadChunk);
            }
        }
        return actualLoadChunks;
    }





}