package com.equilibrium.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.equilibrium.MITEequilibrium.MOD_ID;


public class C2SClickTimesPacket {

    public static final Identifier CLICK_TIMES_PAYLOAD_ID = Identifier.of(MOD_ID, "right_click_times");

    public static final Map<UUID, Integer> playerClickTimes = new ConcurrentHashMap<>();


    public static void registerOnServer() {
        PayloadTypeRegistry.playC2S().register(C2SClickTimesPacket.ClickTimesPayload.ID, C2SClickTimesPacket.ClickTimesPayload.CODEC);
        packetReceive();
    }


    private static void packetReceive() {
        ServerPlayNetworking.registerGlobalReceiver(ClickTimesPayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    UUID playerId = player.getUuid();
                    int timesToAdd = payload.getTimes(); // 获取要增加的次数
                    context.server().execute(() -> {
                        playerClickTimes.put(playerId,timesToAdd);
                    });
                });
    }


    // 定义Payload实现
    public static class ClickTimesPayload implements CustomPayload {
        public static final CustomPayload.Id<ClickTimesPayload> ID =
                new CustomPayload.Id<>(CLICK_TIMES_PAYLOAD_ID);

        private final int times; // 应该是final

        public ClickTimesPayload(int times) {
            this.times = times;
        }

        public int getTimes() {
            return times;
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }

        public static final PacketCodec<PacketByteBuf, ClickTimesPayload> CODEC =
                PacketCodec.of(
                        // 编码器
                        (ClickTimesPayload payload, PacketByteBuf buf) -> {
                            buf.writeVarInt(payload.times); // 使用 writeVarInt 而不是 writeInt
                        },
                        // 解码器
                        (PacketByteBuf buf) -> {
                            int times = buf.readVarInt(); // 使用 readVarInt 而不是 readInt
                            return new ClickTimesPayload(times);
                        }
                );
    }

    // 客户端发送包的方法
    public static void sendClickTimes(int times) {
        ClientPlayNetworking.send(new ClickTimesPayload(times));
    }

    public static int getClickTimes(PlayerEntity player) {
        return playerClickTimes.getOrDefault(player.getUuid(), 0);
    }

    public static void removePlayerData(PlayerEntity player) {
        playerClickTimes.remove(player.getUuid());
    }

    // 可选：直接设置玩家的点击次数（如果需要）
    public static void setClickTimes(PlayerEntity player, int times) {
        playerClickTimes.put(player.getUuid(), times);
    }
}

