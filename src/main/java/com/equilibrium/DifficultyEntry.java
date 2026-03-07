package com.equilibrium;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class DifficultyEntry {
    // 注册一个整数值规则，默认值为 10，归类为 MISC




    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_CRAFTING_TIME_AND_LEVEL =
            GameRuleRegistry.register("enableCraftingTimeAndLevel", GameRules.Category.MISC,GameRuleFactory.createBooleanRule(true));




    public static void initRules(){}

}
