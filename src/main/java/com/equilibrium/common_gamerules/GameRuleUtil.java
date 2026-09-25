package com.equilibrium.common_gamerules;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.network.S2CGameRuleBooleanSimplePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;

import static com.equilibrium.common_gamerules.GameRuleSynMap.GET_ALL_RULES;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.GET_ALL_ENTRY_KEY;

public class GameRuleUtil {

    //回调函数,在游戏规则发生变化时调用
    public static void onGameRuleChangedForBoolean(MinecraftServer server, GameRules.BooleanValue booleanRule,String ruleId){
        // 仅在服务端执行，向所有在线玩家发送针对单个规则的同步包
        // 构造一个game_rule->value的键值对
        if(server==null)
            return;
        S2CGameRuleBooleanSimplePacket.S2CGameRuleSyncPayload payload = new S2CGameRuleBooleanSimplePacket.S2CGameRuleSyncPayload(ruleId,booleanRule.get());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
        OnServerInitialize.LOGGER.info("GameRule changed callback: "+ruleId);
    }

    public static void synchronizeAllBooleanGameRulesTo(ServerPlayer serverPlayerEntity){
        // 仅在服务端执行，为这名玩家发送所有规则的同步包
        // 构造一个game_rule->value的键值对
        for(String ruleId : GET_ALL_RULES.keySet()){
            //普通游戏规则同步:
            S2CGameRuleBooleanSimplePacket.S2CGameRuleSyncPayload payload = new S2CGameRuleBooleanSimplePacket.S2CGameRuleSyncPayload(ruleId,serverPlayerEntity.level().getGameRules().getRule(GET_ALL_RULES.get(ruleId)).get());
            ServerPlayNetworking.send(serverPlayerEntity, payload);
        }
        OnServerInitialize.LOGGER.info("Synchronizing all game rules to: " + serverPlayerEntity.getName().getString());
    }
}
