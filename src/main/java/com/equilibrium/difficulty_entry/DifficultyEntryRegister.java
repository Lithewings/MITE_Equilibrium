package com.equilibrium.difficulty_entry;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

import java.util.Map;
import java.util.Set;

import static com.equilibrium.difficulty_entry.DifficultyEntryUtil.onGameRuleChangedForBoolean;

public class DifficultyEntryRegister {
    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_CRAFTING_TIME_AND_LEVEL =
            GameRuleRegistry.register("enableCraftingTimeAndLevel", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true,(server, booleanRule) -> {
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
    public static void initGameRules(){
    }

}
