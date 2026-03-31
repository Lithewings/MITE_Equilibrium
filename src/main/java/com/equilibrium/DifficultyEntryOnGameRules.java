package com.equilibrium;

import com.equilibrium.network.S2CGameRuleSyncPayloadForBooleanPacket;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

import java.util.*;

public class DifficultyEntryOnGameRules {
    //默认所有规则均为布尔


    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_CRAFTING_TIME_AND_LEVEL =
            GameRuleRegistry.register("enableCraftingTimeAndLevel", GameRules.Category.MISC,GameRuleFactory.createBooleanRule(true,(server, booleanRule) -> {
                onGameRuleChangedForBoolean(server,booleanRule,"enableCraftingTimeAndLevel");
            }));

    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_SLOW_BREAKING_SPEED =
            GameRuleRegistry.register("enableSlowBreakingSpeed", GameRules.Category.MISC,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"enableSlowBreakingSpeed");
            }));

    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_CROP_ILLNESS =
            GameRuleRegistry.register("enableCropIllness", GameRules.Category.MISC,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"enableCropIllness");
            }));
    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_ADVANCE_ANIMAL_AI =
            GameRuleRegistry.register("enableAdvanceAnimalAI", GameRules.Category.MISC,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"enableAdvanceAnimalAI");
            }));

    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_RESTRICT_VILLAGE_GEN =
            GameRuleRegistry.register("enableRestrictVillageGen", GameRules.Category.MISC,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"enableRestrictVillageGen");
            }));

    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_PHYTONUTRIENT =
            GameRuleRegistry.register("enablePhytonutrient", GameRules.Category.MISC,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"enablePhytonutrient");
            }));
    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_BLOOD_MOON_THUNDER =
            GameRuleRegistry.register("enableBloodMoonThunder", GameRules.Category.MISC,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"enableBloodMoonThunder");
            }));

    public static final GameRules.Key<GameRules.BooleanRule> DISABLE_PLAYER_TELEPORT =
            GameRuleRegistry.register("disablePlayerTeleport", GameRules.Category.MISC,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"disablePlayerTeleport");
            }));

    public static Set<GameRules.Key<GameRules.BooleanRule>> ALL_BOOLEAN_GAME_RULE_KEYS =
            Set.of(ENABLE_CROP_ILLNESS,
                    ENABLE_CRAFTING_TIME_AND_LEVEL,
                    ENABLE_SLOW_BREAKING_SPEED,
                    ENABLE_ADVANCE_ANIMAL_AI,
                    ENABLE_RESTRICT_VILLAGE_GEN,
                    ENABLE_PHYTONUTRIENT,
                    ENABLE_BLOOD_MOON_THUNDER,
                    DISABLE_PLAYER_TELEPORT
            );

    //id字典
    public static Map<String, GameRules.Key<GameRules.BooleanRule>> GET_RULE_KEY = Map.of(
            "enableCraftingTimeAndLevel", ENABLE_CRAFTING_TIME_AND_LEVEL,
            "enableSlowBreakingSpeed", ENABLE_SLOW_BREAKING_SPEED,
            "enableCropIllness", ENABLE_CROP_ILLNESS,
            "enableAdvanceAnimalAI",ENABLE_ADVANCE_ANIMAL_AI,
            "enableRestrictVillageGen",ENABLE_RESTRICT_VILLAGE_GEN,
            "enablePhytonutrient",ENABLE_PHYTONUTRIENT,
            "enableBloodMoonThunder",ENABLE_BLOOD_MOON_THUNDER,
            "disablePlayerTeleport",DISABLE_PLAYER_TELEPORT

    );


    //——————————————————————————————————————————————————————————————————————————

    public static void onGameRuleChangedForBoolean(MinecraftServer server,GameRules.BooleanRule booleanRule,String ruleId){
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

    //默认均从客户端世界的读取值
    //T为继承至Rule类的子类,有BooleanRules类型或IntegerRules类型
    public static <T extends GameRules.Rule<T>> T getGameRuleInstanceFromClient(GameRules.Key<T> key){

        if (MinecraftClient.getInstance().world instanceof ClientWorld clientWorld) {
            return clientWorld.getGameRules().get(key);
        }
        OnServerInitialize.LOGGER.info("Maybe the clientWorld is initializing, please wait.");
        return null;
    }
    //引入了同步机制,如果某些位置获得server上下文时,用client上下文也可以拿到:
    public static boolean getGameBooleanRuleFromClient(GameRules.Key<GameRules.BooleanRule> key){
        GameRules.BooleanRule rule = getGameRuleInstanceFromClient(key);
        if(rule==null)
            return false;
        return rule.get();
    }
    //首选检查serverWorld中的游戏规则
    public static boolean getGameBooleanRuleFromServer(GameRules.Key<GameRules.BooleanRule> key, MinecraftServer server){
        GameRules.BooleanRule rule = server.getGameRules().get(key);
        return rule.get();
    }
    //如果上下文均不能获取到客户端和服务端信息,那就不要引入这个机制

    public static void initGameRules(){
    }

}
