package com.equilibrium.difficulty_entry;

import com.equilibrium.OnServerInitialize;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;

import java.util.Map;
import java.util.Set;

import static com.equilibrium.difficulty_entry.DifficultyEntryUtil.onGameRuleChangedForBoolean;

public class DifficultyEntryRegister {

    private static final ResourceLocation COLUMN_BASIC_ID = ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "basic");
    private static final ResourceLocation COLUMN_EXTRA_ID = ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "extra");
    private static final Component BASIC_ENTRIES = Component.literal("Basic Entries").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD);
    private static final Component EXTRA_ENTRIES = Component.literal("Extra Entries").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD);

    private static final CustomGameRuleCategory DEFAULT_GAMERULE_CATEGORY = new CustomGameRuleCategory(COLUMN_BASIC_ID, BASIC_ENTRIES);

    private static final CustomGameRuleCategory EXTRA_GAMERULE_CATEGORY = new CustomGameRuleCategory(COLUMN_EXTRA_ID, EXTRA_ENTRIES);

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_CRAFTING_TIME_AND_LEVEL =
            GameRuleRegistry.register("enableCraftingTimeAndLevel", DEFAULT_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(true, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "enableCraftingTimeAndLevel");
            }));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_SLOW_BREAKING_SPEED =
            GameRuleRegistry.register("enableSlowBreakingSpeed", DEFAULT_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(true, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "enableSlowBreakingSpeed");
            }));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_CROP_ILLNESS =
            GameRuleRegistry.register("enableCropIllness", DEFAULT_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(true, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "enableCropIllness");
            }));
    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_ADVANCE_ANIMAL_AI =
            GameRuleRegistry.register("enableAdvanceAnimalAI", DEFAULT_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(true, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "enableAdvanceAnimalAI");
            }));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_RESTRICT_VILLAGE_GEN =
            GameRuleRegistry.register("enableRestrictVillageGen", DEFAULT_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(true, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "enableRestrictVillageGen");
            }));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_PHYTONUTRIENT =
            GameRuleRegistry.register("enablePhytonutrient", DEFAULT_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(true, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "enablePhytonutrient");
            }));
    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_BLOOD_MOON_THUNDER =
            GameRuleRegistry.register("enableBloodMoonThunder", DEFAULT_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(true, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "enableBloodMoonThunder");
            }));

    public static final GameRules.Key<GameRules.BooleanValue> DISABLE_PLAYER_TELEPORT =
            GameRuleRegistry.register("disablePlayerTeleport", DEFAULT_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(true, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "disablePlayerTeleport");
            }));


    public static final GameRules.Key<GameRules.BooleanValue> DISABLE_VILLAGE_AND_PILLAGE =
            GameRuleRegistry.register("disableVillageAndPillage", EXTRA_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(false, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "disableVillageAndPillage");
            }));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_MORE_SL_DAMAGE =
            GameRuleRegistry.register("enableMoreSlDamage", EXTRA_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(false, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "enableMoreSlDamage");
            }));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_NO_ANIMALS =
            GameRuleRegistry.register("enableNoAnimals", EXTRA_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(false, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "enableNoAnimals");
            }));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_MORE_RAIN_WEATHER =
            GameRuleRegistry.register("enableMoreRainWeather", EXTRA_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(false, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "enableMoreRainWeather");
            }));

    public static final GameRules.Key<GameRules.BooleanValue> DISABLE_CROP_GROW =
            GameRuleRegistry.register("disableCropGrow", EXTRA_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(false, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "disableCropGrow");
            }));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_UNIVERSAL_AGGRO =
            GameRuleRegistry.register("enableUniversalAggro", EXTRA_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(false, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "enableUniversalAggro");
            }));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_ANVIL_LEVEL =
            GameRuleRegistry.register("enableAnvilLevel", EXTRA_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(false, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "enableAnvilLevel");
            }));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_ADVANCED_ENCHANTING_TABLE =
            GameRuleRegistry.register("enableAdvancedEnchantingTable", EXTRA_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(false, (server, booleanRule) -> {
                onGameRuleChangedForBoolean(server, booleanRule, "enableAdvancedEnchantingTable");
            }));




    public static Set<GameRules.Key<GameRules.BooleanValue>> ALL_EXTRA_ENTRY_KEYS =
            Set.of(DISABLE_VILLAGE_AND_PILLAGE,
                    ENABLE_MORE_SL_DAMAGE,
                    ENABLE_NO_ANIMALS,
                    ENABLE_MORE_RAIN_WEATHER,
                    DISABLE_CROP_GROW,
                    ENABLE_UNIVERSAL_AGGRO,
                    ENABLE_ANVIL_LEVEL,
                    ENABLE_ADVANCED_ENCHANTING_TABLE
            );


    public static Set<GameRules.Key<GameRules.BooleanValue>> ALL_BASIC_ENTRY_KEYS =
            Set.of(ENABLE_CROP_ILLNESS,
                    ENABLE_CRAFTING_TIME_AND_LEVEL,
                    ENABLE_SLOW_BREAKING_SPEED,
                    ENABLE_ADVANCE_ANIMAL_AI,
                    ENABLE_RESTRICT_VILLAGE_GEN,
                    ENABLE_PHYTONUTRIENT,
                    ENABLE_BLOOD_MOON_THUNDER,
                    DISABLE_PLAYER_TELEPORT
            );


    //id字典,用于将服务端的规则同步到客户端上去
    public static Map<String, GameRules.Key<GameRules.BooleanValue>> GET_ALL_ENTRY_KEY = Map.ofEntries(
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
