package com.equilibrium.difficulty_entry;

import com.equilibrium.OnServerInitialize;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import java.util.Map;
import java.util.Set;

import static com.equilibrium.difficulty_entry.DifficultyEntryUtil.onGameRuleChangedForBoolean;

public class DifficultyEntryRegister {

    private static final Identifier COLUMN_BASIC_ID = Identifier.of(OnServerInitialize.MOD_ID,"basic");
    private static final Identifier COLUMN_EXTRA_ID = Identifier.of(OnServerInitialize.MOD_ID,"extra");
    private static final Text BASIC_ENTRIES = Text.literal("Basic Entries").formatted(Formatting.YELLOW).formatted(Formatting.BOLD);
    private static final Text EXTRA_ENTRIES = Text.literal("Extra Entries").formatted(Formatting.YELLOW).formatted(Formatting.BOLD);

    private static final CustomGameRuleCategory DEFAULT_GAMERULE_CATEGORY = new CustomGameRuleCategory(COLUMN_BASIC_ID, BASIC_ENTRIES);

    private static final CustomGameRuleCategory EXTRA_GAMERULE_CATEGORY = new CustomGameRuleCategory(COLUMN_EXTRA_ID, EXTRA_ENTRIES);

    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_CRAFTING_TIME_AND_LEVEL =
            GameRuleRegistry.register("enableCraftingTimeAndLevel", DEFAULT_GAMERULE_CATEGORY, GameRuleFactory.createBooleanRule(true,(server, booleanRule) -> {
                onGameRuleChangedForBoolean(server,booleanRule,"enableCraftingTimeAndLevel");
            }));

    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_SLOW_BREAKING_SPEED =
            GameRuleRegistry.register("enableSlowBreakingSpeed", DEFAULT_GAMERULE_CATEGORY,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"enableSlowBreakingSpeed");
            }));

    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_CROP_ILLNESS =
            GameRuleRegistry.register("enableCropIllness", DEFAULT_GAMERULE_CATEGORY,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"enableCropIllness");
            }));
    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_ADVANCE_ANIMAL_AI =
            GameRuleRegistry.register("enableAdvanceAnimalAI", DEFAULT_GAMERULE_CATEGORY,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"enableAdvanceAnimalAI");
            }));

    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_RESTRICT_VILLAGE_GEN =
            GameRuleRegistry.register("enableRestrictVillageGen", DEFAULT_GAMERULE_CATEGORY,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"enableRestrictVillageGen");
            }));

    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_PHYTONUTRIENT =
            GameRuleRegistry.register("enablePhytonutrient", DEFAULT_GAMERULE_CATEGORY,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"enablePhytonutrient");
            }));
    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_BLOOD_MOON_THUNDER =
            GameRuleRegistry.register("enableBloodMoonThunder", DEFAULT_GAMERULE_CATEGORY,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"enableBloodMoonThunder");
            }));

    public static final GameRules.Key<GameRules.BooleanRule> DISABLE_PLAYER_TELEPORT =
            GameRuleRegistry.register("disablePlayerTeleport", DEFAULT_GAMERULE_CATEGORY,GameRuleFactory.createBooleanRule(true,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"disablePlayerTeleport");
            }));


    public static final GameRules.Key<GameRules.BooleanRule> DISABLE_VILLAGE_AND_PILLAGE =
            GameRuleRegistry.register("disableVillageAndPillage", EXTRA_GAMERULE_CATEGORY,GameRuleFactory.createBooleanRule(false,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"disableVillageAndPillage");
            }));

    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_MORE_SL_DAMAGE =
            GameRuleRegistry.register("enableMoreSlDamage", EXTRA_GAMERULE_CATEGORY,GameRuleFactory.createBooleanRule(false,(server, booleanRule)->{
                onGameRuleChangedForBoolean(server,booleanRule,"enableMoreSlDamage");
            }));


    public static Set<GameRules.Key<GameRules.BooleanRule>> ALL_EXTRA_ENTRY_KEYS =
            Set.of(DISABLE_VILLAGE_AND_PILLAGE,
                    ENABLE_MORE_SL_DAMAGE
            );

    public static Set<GameRules.Key<GameRules.BooleanRule>> ALL_BASIC_ENTRY_KEYS =
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
    public static Map<String, GameRules.Key<GameRules.BooleanRule>> GET_ALL_ENTRY_KEY = Map.of(
            "enableCraftingTimeAndLevel", ENABLE_CRAFTING_TIME_AND_LEVEL,
            "enableSlowBreakingSpeed", ENABLE_SLOW_BREAKING_SPEED,
            "enableCropIllness", ENABLE_CROP_ILLNESS,
            "enableAdvanceAnimalAI",ENABLE_ADVANCE_ANIMAL_AI,
            "enableRestrictVillageGen",ENABLE_RESTRICT_VILLAGE_GEN,
            "enablePhytonutrient",ENABLE_PHYTONUTRIENT,
            "enableBloodMoonThunder",ENABLE_BLOOD_MOON_THUNDER,
            "disablePlayerTeleport",DISABLE_PLAYER_TELEPORT,

            "disableVillageAndPillage",DISABLE_VILLAGE_AND_PILLAGE,
            "enableMoreSlDamage",ENABLE_MORE_SL_DAMAGE
    );





    public static void initGameRules(){
    }

}
