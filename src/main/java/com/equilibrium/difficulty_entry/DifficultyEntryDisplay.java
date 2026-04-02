package com.equilibrium.difficulty_entry;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ALL_BOOLEAN_GAME_RULE_KEYS;

public class DifficultyEntryDisplay {
    //认为所有游戏规则都是布尔型的
    public static Map<String,Boolean> getDifficultyEntryValues(ServerWorld serverWorld){
        Map<String,Boolean> result = new HashMap<>();
        for(GameRules.Key<GameRules.BooleanRule> booleanRuleKey:ALL_BOOLEAN_GAME_RULE_KEYS){
            String ruleName = Text.translatable(booleanRuleKey.getTranslationKey()).getString();
            boolean value = serverWorld.getGameRules().getBoolean(booleanRuleKey);
            result.put(ruleName,value);
        }
        return result;
    }
    public static void showAllValuesToServerPlayer(ServerPlayerEntity player){
        Map<String,Boolean> map = getDifficultyEntryValues(player.getServerWorld());
        map.forEach((name, booleanValue) -> {
            String information = name+": "+booleanValue;
            player.sendMessage(Text.of(information));
        });
        map.values().removeIf(booleanValue -> booleanValue==false);
        int trueNumber = map.size();
        player.sendMessage(Text.of("共"+map.size()+"条基础词条,"+"已开启"+trueNumber+"条"));
    }
}
