package com.equilibrium.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.equilibrium.OnServerInitialize.MOD_ID;


public class S2CIllnessTextureBooleanPacket {

    public static final Identifier ILLNESS_APPEARANCE_PAYLOAD_ID = Identifier.of(MOD_ID, "illness_appearance");

    public static final Map<Integer, Boolean> SICK_ENTITY = new ConcurrentHashMap<>();




    public static void registerOnClient() {
        packetReceive();
    }

    public static void registerOnServer() {
        PayloadTypeRegistry.playS2C().register(S2CIllnessTextureBooleanPacket.IllnessAppearancePayload.ID, S2CIllnessTextureBooleanPacket.IllnessAppearancePayload.CODEC);
    }


    private static void packetReceive() {
        ClientPlayNetworking.registerGlobalReceiver(IllnessAppearancePayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        //tips:
                        //当牛状态改变时发包Server->Client
                        //收到包要如何做?
                        //这个payload里有信息,把它解包,记录id和illness
                        //若已康复,删除病单,默认getOrDefault找不到该id默认没病
                        if (payload.isIllness) {
                            SICK_ENTITY.put(payload.entityId, true);
                        } else {
                            SICK_ENTITY.remove(payload.entityId);
                        }
                    });
                });
    }


    // 定义Payload实现
    public static class IllnessAppearancePayload implements CustomPayload {
        public static final CustomPayload.Id<IllnessAppearancePayload> ID =
                new CustomPayload.Id<>(ILLNESS_APPEARANCE_PAYLOAD_ID);

        int entityId;
        boolean isIllness;

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }

        public IllnessAppearancePayload(int entityId, boolean isIllness) {
            this.entityId = entityId;
            this.isIllness = isIllness;
        }
        public static final PacketCodec<PacketByteBuf, IllnessAppearancePayload> CODEC =
                PacketCodec.of(
                        // 编码器
                        (IllnessAppearancePayload payload, PacketByteBuf buf) -> {
                            //按顺序编码
                            buf.writeVarInt(payload.entityId);
                            buf.writeBoolean(payload.isIllness);
                        },
                        // 解码器
                        (PacketByteBuf buf) -> {
                            int entityId = buf.readVarInt();
                            boolean isIllness =buf.readBoolean();
                            return new IllnessAppearancePayload(entityId,isIllness);
                        }
                );
        public static boolean isIllness(int entityId){
            return SICK_ENTITY.getOrDefault(entityId,false);
        };
    }
}

