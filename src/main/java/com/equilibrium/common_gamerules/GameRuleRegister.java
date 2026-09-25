package com.equilibrium.common_gamerules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;

import static com.equilibrium.common_gamerules.GameRuleUtil.onGameRuleChangedForBoolean;


public class GameRuleRegister {

    public static final String FOG_WEATHER = "isFogWeatherNow";
    public static final GameRules.Key<GameRules.BooleanValue> IS_FOG_WEATHER_NOW =
            GameRuleRegistry.register(FOG_WEATHER, GameRules.Category.MISC,
                    GameRuleFactory.createBooleanRule(false,
                            (server, booleanRule) -> onGameRuleChangedForBoolean(server, booleanRule, FOG_WEATHER)));

    public static void initGameRules() {
    }
}
