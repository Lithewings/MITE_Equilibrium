package com.equilibrium.difficulty_entry;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ALL_BASIC_ENTRY_KEYS;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ALL_EXTRA_ENTRY_KEYS;

public class DifficultyEntryDisplay {
    //认为所有游戏规则都是布尔型的
    public static Map<String,Boolean> getBasicEntryValues(ServerLevel serverWorld){
        return getStringBooleanMap(serverWorld, ALL_BASIC_ENTRY_KEYS);
    }


    public static Map<String,Boolean> getExtraEntryValues(ServerLevel serverWorld){
        return getStringBooleanMap(serverWorld, ALL_EXTRA_ENTRY_KEYS);
    }

    private static Map<String, Boolean> getStringBooleanMap(ServerLevel serverWorld, Set<GameRules.Key<GameRules.BooleanValue>> allEntryKeys) {
        Map<String,Boolean> result = new HashMap<>();
        for(GameRules.Key<GameRules.BooleanValue> booleanRuleKey: allEntryKeys){
            String ruleName = Component.translatable(booleanRuleKey.getDescriptionId()).getString();
            boolean value = serverWorld.getGameRules().getBoolean(booleanRuleKey);
            result.put(ruleName,value);
        }
        return result;
    }


    public static void showAllValuesToServerPlayer(ServerPlayer player,boolean isBasic){
        if(isBasic){
            Map<String,Boolean> map = getBasicEntryValues(player.serverLevel());
            map.forEach((name, booleanValue) -> {
                String information = name+": "+booleanValue;
                player.sendSystemMessage(Component.nullToEmpty(information));
            });
            int allNumber = map.size();
            map.values().removeIf(booleanValue -> booleanValue==false);
            int trueNumber = map.size();
            player.sendSystemMessage(Component.translatable("difficulty.basic.entry.message", allNumber, trueNumber));
        }
        else {
            Map<String, Boolean> map = getExtraEntryValues(player.serverLevel());
            map.forEach((name, booleanValue) -> {
                String information = name + ": " + booleanValue;
                player.sendSystemMessage(Component.nullToEmpty(information));
            });
            int allNumber = map.size();
            map.values().removeIf(booleanValue -> booleanValue == false);
            int trueNumber = map.size();
            player.sendSystemMessage(Component.translatable("difficulty.extra.entry.message", allNumber, trueNumber));
        }
    }
}
