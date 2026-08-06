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
            GameRules.register("enableCraftingTimeAndLevel", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "enableCraftingTimeAndLevel")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_SLOW_BREAKING_SPEED =
            GameRules.register("enableSlowBreakingSpeed", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "enableSlowBreakingSpeed")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_CROP_ILLNESS =
            GameRules.register("enableCropIllness", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "enableCropIllness")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_ADVANCE_ANIMAL_AI =
            GameRules.register("enableAdvanceAnimalAI", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "enableAdvanceAnimalAI")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_RESTRICT_VILLAGE_GEN =
            GameRules.register("enableRestrictVillageGen", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "enableRestrictVillageGen")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_PHYTONUTRIENT =
            GameRules.register("enablePhytonutrient", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "enablePhytonutrient")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_BLOOD_MOON_THUNDER =
            GameRules.register("enableBloodMoonThunder", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "enableBloodMoonThunder")));

    public static final GameRules.Key<GameRules.BooleanValue> DISABLE_PLAYER_TELEPORT =
            GameRules.register("disablePlayerTeleport", BASIC_CATEGORY,
                    GameRules.BooleanValue.create(true, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "disablePlayerTeleport")));

    // ---------- Extra 规则 ----------
    public static final GameRules.Key<GameRules.BooleanValue> DISABLE_VILLAGE_AND_PILLAGE =
            GameRules.register("disableVillageAndPillage", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "disableVillageAndPillage")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_MORE_SL_DAMAGE =
            GameRules.register("enableMoreSlDamage", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "enableMoreSlDamage")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_NO_ANIMALS =
            GameRules.register("enableNoAnimals", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "enableNoAnimals")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_MORE_RAIN_WEATHER =
            GameRules.register("enableMoreRainWeather", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "enableMoreRainWeather")));

    public static final GameRules.Key<GameRules.BooleanValue> DISABLE_CROP_GROW =
            GameRules.register("disableCropGrow", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "disableCropGrow")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_UNIVERSAL_AGGRO =
            GameRules.register("enableUniversalAggro", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "enableUniversalAggro")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_ANVIL_LEVEL =
            GameRules.register("enableAnvilLevel", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "enableAnvilLevel")));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_ADVANCED_ENCHANTING_TABLE =
            GameRules.register("enableAdvancedEnchantingTable", EXTRA_CATEGORY,
                    GameRules.BooleanValue.create(false, (server, booleanRule) ->
                            onGameRuleChangedForBoolean(server, booleanRule, "enableAdvancedEnchantingTable")));

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
    public static final Map<String, GameRules.Key<GameRules.BooleanValue>> GET_ALL_ENTRY_KEY = Map.ofEntries(
            Map.entry("enableCraftingTimeAndLevel", ENABLE_CRAFTING_TIME_AND_LEVEL),
            Map.entry("enableSlowBreakingSpeed", ENABLE_SLOW_BREAKING_SPEED),
            Map.entry("enableCropIllness", ENABLE_CROP_ILLNESS),
            Map.entry("enableAdvanceAnimalAI", ENABLE_ADVANCE_ANIMAL_AI),
            Map.entry("enableRestrictVillageGen", ENABLE_RESTRICT_VILLAGE_GEN),
            Map.entry("enablePhytonutrient", ENABLE_PHYTONUTRIENT),
            Map.entry("enableBloodMoonThunder", ENABLE_BLOOD_MOON_THUNDER),
            Map.entry("disablePlayerTeleport", DISABLE_PLAYER_TELEPORT),

            Map.entry("disableVillageAndPillage", DISABLE_VILLAGE_AND_PILLAGE),
            Map.entry("enableMoreSlDamage", ENABLE_MORE_SL_DAMAGE),
            Map.entry("enableNoAnimals", ENABLE_NO_ANIMALS),
            Map.entry("enableMoreRainWeather", ENABLE_MORE_RAIN_WEATHER),
            Map.entry("disableCropGrow", DISABLE_CROP_GROW),
            Map.entry("enableUniversalAggro", ENABLE_UNIVERSAL_AGGRO),
            Map.entry("enableAnvilLevel", ENABLE_ANVIL_LEVEL),
            Map.entry("enableAdvancedEnchantingTable", ENABLE_ADVANCED_ENCHANTING_TABLE)
    );

    public static void initGameRules() {

    }
}