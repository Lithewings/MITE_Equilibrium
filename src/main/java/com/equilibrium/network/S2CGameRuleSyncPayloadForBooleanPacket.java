package com.equilibrium.network;

import com.equilibrium.OnServerInitialize;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.GET_ALL_ENTRY_KEY;

public class S2CGameRuleSyncPayloadForBooleanPacket  {
    public static final CustomPacketPayload.Type<S2CGameRuleSyncPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "game_rule_sync"));



    private static void packetReceived() {
        ClientPlayNetworking.registerGlobalReceiver(ID,
                (payload, context) ->
                        context.client().execute(() -> {
                            //接收包也要做什么?把这个新规则更新到自己的Client环境中
                            if(!GET_ALL_ENTRY_KEY.containsKey(payload.rulesId)){
                                OnServerInitialize.LOGGER.error("This GameRule can not be changed");
                                return; // 重要！
                            }

                            GameRules.Key<GameRules.BooleanValue> booleanRuleKey = GET_ALL_ENTRY_KEY.get(payload.rulesId);
                            ClientLevel clientWorld = context.client().level;

                            clientWorld.getGameRules().getRule(booleanRuleKey).set(payload.gameRuleBooleanValue,null);

                        }));
    }

    public static void registerOnClient() {
        //因为这里只有客户端接收,且信任服务端,故只做客户端的Receiver
        packetReceived();
    }


    public static void registerOnServer() {
        PayloadTypeRegistry.playS2C().register(ID, S2CGameRuleSyncPayload.CODEC);
    }





    public static class S2CGameRuleSyncPayload implements CustomPacketPayload{


        public final String rulesId;
        public final Boolean gameRuleBooleanValue;

        public S2CGameRuleSyncPayload(String rulesId, Boolean gameRuleValue) {
            this.rulesId = rulesId;
            this.gameRuleBooleanValue = gameRuleValue;
        }


        public static final StreamCodec<FriendlyByteBuf, S2CGameRuleSyncPayload> CODEC =
                StreamCodec.ofMember(
                        // 编码器
                        (S2CGameRuleSyncPayload payload, FriendlyByteBuf buf) -> {
                            //按顺序编码
                            buf.writeUtf(payload.rulesId);
                            buf.writeBoolean(payload.gameRuleBooleanValue);
                        },
                        // 解码器
                        (FriendlyByteBuf buf) -> {
                            String enableCraftingTimeAndLevel = buf.readUtf();
                            Boolean gameRuleValue = buf.readBoolean();
                            return new S2CGameRuleSyncPayload(enableCraftingTimeAndLevel, gameRuleValue);
                        }
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }



    }







}
