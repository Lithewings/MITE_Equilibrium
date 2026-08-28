package com.equilibrium.difficulty_entry;

import net.minecraft.world.level.GameRules;

import java.util.Map;
import java.util.Set;

import static com.equilibrium.difficulty_entry.DifficultyEntryUtil.onGameRuleChangedForBoolean;

public class DifficultyEntryRegister {

    private static final GameRules.Category BASIC_CATEGORY = GameRules.Category.MISC;
    private static final GameRules.Category EXTRA_CATEGORY = GameRules.Category.MISC;

    // ---------- Basic 规则 ----------
    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_CRAFTING_TIME_AND_LEVEL =
            GameRules.register("basicEnableCraftingTimeAndLevel", BASIC_CATEGORY, GameRules.BooleanValue.create(true, (server, booleanRule) -> onGameRuleChangedForBoolean(server, booleanRule,
                               "basicEnableCraftingTimeAndLevel")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_SLOW_BREAKING_SPEED =
            GameRules.register("basicEnableSlowBreakingSpeed", BASIC_CATEGORY, GameRules.BooleanValue.create(true, (server, booleanRule) -> onGameRuleChangedForBoolean(server, booleanRule,
                               "basicEnableSlowBreakingSpeed")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_CROP_ILLNESS =
            GameRules.register("basicEnableCropIllness", BASIC_CATEGORY, GameRules.BooleanValue.create(true, (server, booleanRule) -> onGameRuleChangedForBoolean(server, booleanRule,
                               "basicEnableCropIllness")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_ADVANCE_ANIMAL_AI =
            GameRules.register("basicEnableAdvanceAnimalAI", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "basicEnableAdvanceAnimalAI")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_RESTRICT_VILLAGE_GEN =
            GameRules.register("basicEnableRestrictVillageGen", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "basicEnableRestrictVillageGen")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_PHYTONUTRIENT =
            GameRules.register("basicEnablePhytonutrient", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "basicEnablePhytonutrient")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_BLOOD_MOON_THUNDER =
            GameRules.register("basicEnableBloodMoonThunder", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "basicEnableBloodMoonThunder")));

    public static final GameRules.Key<GameRules.BooleanValue> DISABLE_PLAYER_TELEPORT =
            GameRules.register("basicDisablePlayerTeleport", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "basicDisablePlayerTeleport")));

    // ---------- Extra 规则 ----------
    public static final GameRules.Key<GameRules.BooleanValue> DISABLE_VILLAGE_AND_PILLAGE =
            GameRules.register("extraDisableVillageAndPillage", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "extraDisableVillageAndPillage")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_MORE_SL_DAMAGE =
            GameRules.register("extraEnableMoreSlDamage", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "extraEnableMoreSlDamage")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_NO_ANIMALS =
            GameRules.register("extraEnableNoAnimals", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "extraEnableNoAnimals")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_MORE_RAIN_WEATHER =
            GameRules.register("extraEnableMoreRainWeather", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "extraEnableMoreRainWeather")));

    public static final GameRules.Key<GameRules.BooleanValue> DISABLE_CROP_GROW =
            GameRules.register("extraDisableCropGrow", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "extraDisableCropGrow")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_UNIVERSAL_AGGRO =
            GameRules.register("extraEnableUniversalAggro", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "extraEnableUniversalAggro")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_ANVIL_LEVEL =
            GameRules.register("extraEnableAnvilLevel", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "extraEnableAnvilLevel")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_ADVANCED_ENCHANTING_TABLE =
            GameRules.register("extraEnableAdvancedEnchantingTable", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "extraEnableAdvancedEnchantingTable")));

    // ---------- 分组集合 ----------
    public static final Set<GameRules.Key<GameRules.BooleanValue>> ALL_EXTRA_ENTRY_KEYS =
            Set.of(DISABLE_VILLAGE_AND_PILLAGE,
                    ENABLE_MORE_SL_DAMAGE,
                    ENABLE_NO_ANIMALS,
                    ENABLE_MORE_RAIN_WEATHER,
                    DISABLE_CROP_GROW,
                    ENABLE_UNIVERSAL_AGGRO,
                    ENABLE_ANVIL_LEVEL,
                    ENABLE_ADVANCED_ENCHANTING_TABLE
            );

    public static final Set<GameRules.Key<GameRules.BooleanValue>> ALL_BASIC_ENTRY_KEYS =
            Set.of(ENABLE_CROP_ILLNESS,
                    ENABLE_CRAFTING_TIME_AND_LEVEL,
                    ENABLE_SLOW_BREAKING_SPEED,
                    ENABLE_ADVANCE_ANIMAL_AI,
                    ENABLE_RESTRICT_VILLAGE_GEN,
                    ENABLE_PHYTONUTRIENT,
                    ENABLE_BLOOD_MOON_THUNDER,
                    DISABLE_PLAYER_TELEPORT
            );

    // ---------- 名称 -> Key 映射 ----------
    // 供客户端网络通信同步数据使用
    public static final Map<String, GameRules.Key<GameRules.BooleanValue>> GET_ALL_ENTRY_KEY = Map.ofEntries(
            Map.entry("basicEnableCraftingTimeAndLevel", ENABLE_CRAFTING_TIME_AND_LEVEL),
            Map.entry("basicEnableSlowBreakingSpeed", ENABLE_SLOW_BREAKING_SPEED),
            Map.entry("basicEnableCropIllness", ENABLE_CROP_ILLNESS),
            Map.entry("basicEnableAdvanceAnimalAI", ENABLE_ADVANCE_ANIMAL_AI),
            Map.entry("basicEnableRestrictVillageGen", ENABLE_RESTRICT_VILLAGE_GEN),
            Map.entry("basicEnablePhytonutrient", ENABLE_PHYTONUTRIENT),
            Map.entry("basicEnableBloodMoonThunder", ENABLE_BLOOD_MOON_THUNDER),
            Map.entry("basicDisablePlayerTeleport", DISABLE_PLAYER_TELEPORT),

            Map.entry("extraDisableVillageAndPillage", DISABLE_VILLAGE_AND_PILLAGE),
            Map.entry("extraEnableMoreSlDamage", ENABLE_MORE_SL_DAMAGE),
            Map.entry("extraEnableNoAnimals", ENABLE_NO_ANIMALS),
            Map.entry("extraEnableMoreRainWeather", ENABLE_MORE_RAIN_WEATHER),
            Map.entry("extraDisableCropGrow", DISABLE_CROP_GROW),
            Map.entry("extraEnableUniversalAggro", ENABLE_UNIVERSAL_AGGRO),
            Map.entry("extraEnableAnvilLevel", ENABLE_ANVIL_LEVEL),
            Map.entry("extraEnableAdvancedEnchantingTable", ENABLE_ADVANCED_ENCHANTING_TABLE)
    );

    public static void initGameRules() {

    }
}