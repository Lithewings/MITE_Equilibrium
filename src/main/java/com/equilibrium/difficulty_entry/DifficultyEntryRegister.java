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
            GameRules.register("01enableCraftingTimeAndLevel", BASIC_CATEGORY, GameRules.BooleanValue.create(true, (server, booleanRule) -> onGameRuleChangedForBoolean(server, booleanRule,
                               "01enableCraftingTimeAndLevel")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_SLOW_BREAKING_SPEED =
            GameRules.register("02enableSlowBreakingSpeed", BASIC_CATEGORY, GameRules.BooleanValue.create(true, (server, booleanRule) -> onGameRuleChangedForBoolean(server, booleanRule,
                               "02enableSlowBreakingSpeed")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_CROP_ILLNESS =
            GameRules.register("03enableCropIllness", BASIC_CATEGORY, GameRules.BooleanValue.create(true, (server, booleanRule) -> onGameRuleChangedForBoolean(server, booleanRule,
                               "03enableCropIllness")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_ADVANCE_ANIMAL_AI =
            GameRules.register("04enableAdvanceAnimalAI", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "04enableAdvanceAnimalAI")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_RESTRICT_VILLAGE_GEN =
            GameRules.register("05enableRestrictVillageGen", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "05enableRestrictVillageGen")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_PHYTONUTRIENT =
            GameRules.register("06enablePhytonutrient", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "06enablePhytonutrient")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_BLOOD_MOON_THUNDER =
            GameRules.register("07enableBloodMoonThunder", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "07enableBloodMoonThunder")));

    public static final GameRules.Key<GameRules.BooleanValue> DISABLE_PLAYER_TELEPORT =
            GameRules.register("08disablePlayerTeleport", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "08disablePlayerTeleport")));

    // ---------- Extra 规则 ----------
    public static final GameRules.Key<GameRules.BooleanValue> DISABLE_VILLAGE_AND_PILLAGE =
            GameRules.register("001disableVillageAndPillage", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "001disableVillageAndPillage")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_MORE_SL_DAMAGE =
            GameRules.register("002enableMoreSlDamage", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "002enableMoreSlDamage")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_NO_ANIMALS =
            GameRules.register("003enableNoAnimals", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "003enableNoAnimals")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_MORE_RAIN_WEATHER =
            GameRules.register("004enableMoreRainWeather", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "004enableMoreRainWeather")));

    public static final GameRules.Key<GameRules.BooleanValue> DISABLE_CROP_GROW =
            GameRules.register("005disableCropGrow", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "005disableCropGrow")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_UNIVERSAL_AGGRO =
            GameRules.register("006enableUniversalAggro", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "006enableUniversalAggro")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_ANVIL_LEVEL =
            GameRules.register("007enableAnvilLevel", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "007enableAnvilLevel")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_ADVANCED_ENCHANTING_TABLE =
            GameRules.register("008enableAdvancedEnchantingTable", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "008enableAdvancedEnchantingTable")));

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
            Map.entry("01enableCraftingTimeAndLevel", ENABLE_CRAFTING_TIME_AND_LEVEL),
            Map.entry("02enableSlowBreakingSpeed", ENABLE_SLOW_BREAKING_SPEED),
            Map.entry("03enableCropIllness", ENABLE_CROP_ILLNESS),
            Map.entry("04enableAdvanceAnimalAI", ENABLE_ADVANCE_ANIMAL_AI),
            Map.entry("05enableRestrictVillageGen", ENABLE_RESTRICT_VILLAGE_GEN),
            Map.entry("06enablePhytonutrient", ENABLE_PHYTONUTRIENT),
            Map.entry("07enableBloodMoonThunder", ENABLE_BLOOD_MOON_THUNDER),
            Map.entry("08disablePlayerTeleport", DISABLE_PLAYER_TELEPORT),

            Map.entry("001disableVillageAndPillage", DISABLE_VILLAGE_AND_PILLAGE),
            Map.entry("002enableMoreSlDamage", ENABLE_MORE_SL_DAMAGE),
            Map.entry("003enableNoAnimals", ENABLE_NO_ANIMALS),
            Map.entry("004enableMoreRainWeather", ENABLE_MORE_RAIN_WEATHER),
            Map.entry("005disableCropGrow", DISABLE_CROP_GROW),
            Map.entry("006enableUniversalAggro", ENABLE_UNIVERSAL_AGGRO),
            Map.entry("007enableAnvilLevel", ENABLE_ANVIL_LEVEL),
            Map.entry("008enableAdvancedEnchantingTable", ENABLE_ADVANCED_ENCHANTING_TABLE)
    );

    public static void initGameRules() {

    }
}