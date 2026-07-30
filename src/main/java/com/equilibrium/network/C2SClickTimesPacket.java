package com.equilibrium.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.equilibrium.OnServerInitialize.MOD_ID;


public class C2SClickTimesPacket {

    public static final ResourceLocation CLICK_TIMES_PAYLOAD_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "right_click_times");

    public static final Map<UUID, Integer> playerClickTimes = new ConcurrentHashMap<>();


    public static void registerOnServer() {
        PayloadTypeRegistry.playC2S().register(ClickTimesPayload.ID, ClickTimesPayload.CODEC);
        packetReceive();
    }


    private static void packetReceive() {
        ServerPlayNetworking.registerGlobalReceiver(ClickTimesPayload.ID,
                (payload, context) -> {
                    ServerPlayer player = context.player();
                    UUID playerId = player.getUUID();
                    int timesToAdd = payload.getTimes(); // 获取要增加的次数
                    context.server().execute(() -> {
                        playerClickTimes.put(playerId,timesToAdd);
                    });
                });
    }


    // 定义Payload实现
    public static class ClickTimesPayload implements CustomPacketPayload {
        public static final Type<ClickTimesPayload> ID =
                new Type<>(CLICK_TIMES_PAYLOAD_ID);

        private final int times; // 应该是final

        public ClickTimesPayload(int times) {
            this.times = times;
        }

        public int getTimes() {
            return times;
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }

        public static final StreamCodec<FriendlyByteBuf, ClickTimesPayload> CODEC =
                StreamCodec.ofMember(
                        // 编码器
                        (ClickTimesPayload payload, FriendlyByteBuf buf) -> {
                            buf.writeVarInt(payload.times); // 使用 writeVarInt 而不是 writeInt
                        },
                        // 解码器
                        (FriendlyByteBuf buf) -> {
                            int times = buf.readVarInt(); // 使用 readVarInt 而不是 readInt
                            return new ClickTimesPayload(times);
                        }
                );
    }

    // 客户端发送包的方法
    public static void sendClickTimes(int times) {
        ClientPlayNetworking.send(new ClickTimesPayload(times));
    }

    public static int getClickTimes(Player player) {
        return playerClickTimes.getOrDefault(player.getUUID(), 0);
    }

    public static void removePlayerData(Player player) {
        playerClickTimes.remove(player.getUUID());
    }

    // 可选：直接设置玩家的点击次数（如果需要）
    public static void setClickTimes(Player player, int times) {
        playerClickTimes.put(player.getUUID(), times);
    }
}

