package com.equilibrium.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.equilibrium.OnServerInitialize.MOD_ID;


public class S2CStockChangeGrassColorPacket {

    public static final ResourceLocation STOCK_CHANGE_GRASS_COLOR_PACKET_PACKET_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "grass_color");


    public static Map<BlockPos, Integer> BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();



    public static void registerOnClient() {
        //因为这里只有客户端接收,且信任服务端,故只做客户端的Receiver
        packetReceived();
    }


    public static void registerOnServer() {
        PayloadTypeRegistry.playS2C().register(GrassColorPayload.ID, GrassColorPayload.CODEC);
    }

    private static void packetReceived() {
        ClientPlayNetworking.registerGlobalReceiver(GrassColorPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        //接收包也要做什么?把这个新数据更新到自己的Client环境中
                        BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP.put(payload.pos, payload.polluteLevel);
                    });
                });
    }


    // 定义Payload实现
    public static class GrassColorPayload implements CustomPacketPayload {
        public static final Type<GrassColorPayload> ID =
                new Type<>(STOCK_CHANGE_GRASS_COLOR_PACKET_PACKET_ID);

        public final BlockPos pos;
        public final int polluteLevel;

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }

        public GrassColorPayload(BlockPos pos, int polluteLevel) {
            this.pos = pos;
            this.polluteLevel =polluteLevel;
        }
        public static final StreamCodec<FriendlyByteBuf, GrassColorPayload> CODEC =
                StreamCodec.ofMember(
                        // 编码器
                        (GrassColorPayload payload, FriendlyByteBuf buf) -> {
                            //按顺序编码
                            buf.writeBlockPos(payload.pos);
                            buf.writeVarInt(payload.polluteLevel);
                        },
                        // 解码器
                        (FriendlyByteBuf buf) -> {
                            BlockPos pos = buf.readBlockPos();
                            int polluteLevel = buf.readVarInt();
                            return new GrassColorPayload(pos,polluteLevel);
                        }
                );
        public static Integer getPolluteLevel(BlockPos pos){
            return BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP.getOrDefault(pos, 0);
        };
    }
}

