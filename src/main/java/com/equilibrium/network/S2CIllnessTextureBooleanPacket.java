package com.equilibrium.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.equilibrium.OnServerInitialize.MOD_ID;


public class S2CIllnessTextureBooleanPacket {

    public static final ResourceLocation ILLNESS_APPEARANCE_PAYLOAD_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "illness_appearance");

    public static final Map<Integer, Boolean> SICK_ENTITY = new ConcurrentHashMap<>();




    public static void registerOnClient() {
        packetReceive();
    }

    public static void registerOnServer() {
        PayloadTypeRegistry.playS2C().register(IllnessAppearancePayload.ID, IllnessAppearancePayload.CODEC);
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
    public static class IllnessAppearancePayload implements CustomPacketPayload {
        public static final Type<IllnessAppearancePayload> ID =
                new Type<>(ILLNESS_APPEARANCE_PAYLOAD_ID);

        int entityId;
        boolean isIllness;

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }

        public IllnessAppearancePayload(int entityId, boolean isIllness) {
            this.entityId = entityId;
            this.isIllness = isIllness;
        }
        public static final StreamCodec<FriendlyByteBuf, IllnessAppearancePayload> CODEC =
                StreamCodec.ofMember(
                        // 编码器
                        (IllnessAppearancePayload payload, FriendlyByteBuf buf) -> {
                            //按顺序编码
                            buf.writeVarInt(payload.entityId);
                            buf.writeBoolean(payload.isIllness);
                        },
                        // 解码器
                        (FriendlyByteBuf buf) -> {
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

