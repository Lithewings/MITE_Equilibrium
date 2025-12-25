package com.equilibrium.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.equilibrium.MITEequilibrium.MOD_ID;

public class S2CCowIllnessTextureBooleanPacket {
    public static final Identifier PACKET_ID = Identifier.of(MOD_ID, "cow_appearance");

    public static final Map<Integer, Boolean> SICK_COWS = new ConcurrentHashMap<>();

    public static void register() {
        PayloadTypeRegistry.playS2C().register(S2CCowIllnessTextureBooleanPacket.CowAppearancePayload.ID, CowAppearancePayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(S2CCowIllnessTextureBooleanPacket.CowAppearancePayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        //tips:
                        //当牛状态改变时发包Server->Client
                        //收到包要如何做?
                        //这个payload里有信息,把它解包,记录id和illness
                        //若已康复,删除病单,默认getOrDefault找不到该id默认没病
                        if (payload.isIllness) {
                            SICK_COWS.put(payload.entityId, true);
                        } else {
                            SICK_COWS.remove(payload.entityId);
                        }
                    });
                });
    }

    // 定义Payload实现
    public static class CowAppearancePayload implements CustomPayload {
        public static final CustomPayload.Id<S2CCowIllnessTextureBooleanPacket.CowAppearancePayload> ID =
                new CustomPayload.Id<>(PACKET_ID);

        int entityId;
        boolean isIllness;

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }

        public CowAppearancePayload(int entityId,boolean isIllness) {
            this.entityId = entityId;
            this.isIllness = isIllness;
        }
        public static final PacketCodec<PacketByteBuf, S2CCowIllnessTextureBooleanPacket.CowAppearancePayload> CODEC =
                PacketCodec.of(
                        // 编码器
                        (S2CCowIllnessTextureBooleanPacket.CowAppearancePayload payload, PacketByteBuf buf) -> {
                            //按顺序编码
                            buf.writeVarInt(payload.entityId);
                            buf.writeBoolean(payload.isIllness);
                        },
                        // 解码器
                        (PacketByteBuf buf) -> {
                            int entityId = buf.readVarInt();
                            boolean isIllness =buf.readBoolean();// 使用 readVarInt 而不是 readInt
                            return new CowAppearancePayload(entityId,isIllness);
                        }
                );
        public static boolean isIllness(int entityId){
            return SICK_COWS.getOrDefault(entityId,false);
        };
    }
}

