package com.equilibrium.difficulty_entry;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.network.S2CGameRuleSyncPayloadForBooleanPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.GET_RULE_KEY;

public class DifficultyEntryUtil {





    //回调函数,在游戏规则发生变化时调用
    public static void onGameRuleChangedForBoolean(MinecraftServer server, GameRules.BooleanRule booleanRule, String ruleId){
        // 仅在服务端执行，向所有在线玩家发送针对单个规则的同步包
        // 构造一个game_rule->value的键值对
        S2CGameRuleSyncPayloadForBooleanPacket.S2CGameRuleSyncPayload payload = new S2CGameRuleSyncPayloadForBooleanPacket.S2CGameRuleSyncPayload(ruleId,booleanRule.get());
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, payload);
        }
        OnServerInitialize.LOGGER.info("GameRule changed callback: "+ruleId);
    }

    //PlayerManagerMixin中进行了调用
    public static void onPlayerConnectSynchronizingGameRulesForBoolean(ServerPlayerEntity serverPlayerEntity){
        // 仅在服务端执行，为这名登录的玩家发送所有规则的同步包
        // 构造一个game_rule->value的键值对
        for(String ruleId : GET_RULE_KEY.keySet()){

            S2CGameRuleSyncPayloadForBooleanPacket.S2CGameRuleSyncPayload payload = new S2CGameRuleSyncPayloadForBooleanPacket.S2CGameRuleSyncPayload(ruleId,serverPlayerEntity.getWorld().getGameRules().get(GET_RULE_KEY.get(ruleId)).get());
            ServerPlayNetworking.send(serverPlayerEntity, payload);
        }
        OnServerInitialize.LOGGER.info("A player is connecting, synchronizing all game rules.");
    }
}
