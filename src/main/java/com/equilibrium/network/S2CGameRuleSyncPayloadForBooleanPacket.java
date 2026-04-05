package com.equilibrium.network;

import com.equilibrium.OnServerInitialize;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.GET_ALL_ENTRY_KEY;

public class S2CGameRuleSyncPayloadForBooleanPacket  {
    public static final CustomPayload.Id<S2CGameRuleSyncPayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "game_rule_sync"));



    private static void packetReceived() {
        ClientPlayNetworking.registerGlobalReceiver(ID,
                (payload, context) ->
                        context.client().execute(() -> {
                            //接收包也要做什么?把这个新规则更新到自己的Client环境中
                            if(!GET_ALL_ENTRY_KEY.containsKey(payload.rulesId)){
                                OnServerInitialize.LOGGER.error("This GameRule can not be changed");
                                return; // 重要！
                            }

                            GameRules.Key<GameRules.BooleanRule> booleanRuleKey = GET_ALL_ENTRY_KEY.get(payload.rulesId);
                            ClientWorld clientWorld = context.client().world;

                            clientWorld.getGameRules().get(booleanRuleKey).set(payload.gameRuleBooleanValue,null);

                        }));
    }

    public static void registerOnClient() {
        //因为这里只有客户端接收,且信任服务端,故只做客户端的Receiver
        packetReceived();
    }


    public static void registerOnServer() {
        PayloadTypeRegistry.playS2C().register(ID, S2CGameRuleSyncPayload.CODEC);
    }





    public static class S2CGameRuleSyncPayload implements CustomPayload{


        public final String rulesId;
        public final Boolean gameRuleBooleanValue;

        public S2CGameRuleSyncPayload(String rulesId, Boolean gameRuleValue) {
            this.rulesId = rulesId;
            this.gameRuleBooleanValue = gameRuleValue;
        }


        public static final PacketCodec<PacketByteBuf, S2CGameRuleSyncPayload> CODEC =
                PacketCodec.of(
                        // 编码器
                        (S2CGameRuleSyncPayload payload, PacketByteBuf buf) -> {
                            //按顺序编码
                            buf.writeString(payload.rulesId);
                            buf.writeBoolean(payload.gameRuleBooleanValue);
                        },
                        // 解码器
                        (PacketByteBuf buf) -> {
                            String enableCraftingTimeAndLevel = buf.readString();
                            Boolean gameRuleValue = buf.readBoolean();
                            return new S2CGameRuleSyncPayload(enableCraftingTimeAndLevel, gameRuleValue);
                        }
                );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }



    }







}
