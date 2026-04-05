package com.equilibrium.difficulty_entry;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ALL_BASIC_ENTRY_KEYS;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ALL_EXTRA_ENTRY_KEYS;

public class DifficultyEntryDisplay {
    //认为所有游戏规则都是布尔型的
    public static Map<String,Boolean> getBasicEntryValues(ServerWorld serverWorld){
        return getStringBooleanMap(serverWorld, ALL_BASIC_ENTRY_KEYS);
    }


    public static Map<String,Boolean> getExtraEntryValues(ServerWorld serverWorld){
        return getStringBooleanMap(serverWorld, ALL_EXTRA_ENTRY_KEYS);
    }

    private static Map<String, Boolean> getStringBooleanMap(ServerWorld serverWorld, Set<GameRules.Key<GameRules.BooleanRule>> allEntryKeys) {
        Map<String,Boolean> result = new HashMap<>();
        for(GameRules.Key<GameRules.BooleanRule> booleanRuleKey: allEntryKeys){
            String ruleName = Text.translatable(booleanRuleKey.getTranslationKey()).getString();
            boolean value = serverWorld.getGameRules().getBoolean(booleanRuleKey);
            result.put(ruleName,value);
        }
        return result;
    }


    public static void showAllValuesToServerPlayer(ServerPlayerEntity player,boolean isBasic){
        if(isBasic){
            Map<String,Boolean> map = getBasicEntryValues(player.getServerWorld());
            map.forEach((name, booleanValue) -> {
                String information = name+": "+booleanValue;
                player.sendMessage(Text.of(information));
            });
            int allNumber = map.size();
            map.values().removeIf(booleanValue -> booleanValue==false);
            int trueNumber = map.size();
            player.sendMessage(Text.of("共"+allNumber+"条基础词条,"+"已开启"+trueNumber+"条"));
        }
        else {
            Map<String, Boolean> map = getExtraEntryValues(player.getServerWorld());
            map.forEach((name, booleanValue) -> {
                String information = name + ": " + booleanValue;
                player.sendMessage(Text.of(information));
            });
            int allNumber = map.size();
            map.values().removeIf(booleanValue -> booleanValue == false);
            int trueNumber = map.size();
            player.sendMessage(Text.of("共" + allNumber + "条进阶词条," + "已开启" + trueNumber + "条"));
        }
    }
}
