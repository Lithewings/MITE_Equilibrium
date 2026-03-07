package com.equilibrium.mixin.some_special_rules;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static com.equilibrium.DifficultyEntry.ENABLE_CRAFTING_TIME_AND_LEVEL;


@Mixin(EditGameRulesScreen.RuleListWidget.class)
public class EditGameRulesScreenMixin {

    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;entrySet()Ljava/util/Set;"
            )
    )
    public void  filterGameRules(EditGameRulesScreen editGameRulesScreen, GameRules gameRules, CallbackInfo ci, @Local Map<GameRules.Category, Map<GameRules.Key<?>, EditGameRulesScreen.AbstractRuleWidget>> map){


        map.remove(GameRules.Category.PLAYER);
        map.remove(GameRules.Category.CHAT);
        map.remove(GameRules.Category.MOBS);
        map.remove(GameRules.Category.DROPS);
        map.remove(GameRules.Category.SPAWNING);
        map.remove(GameRules.Category.UPDATES);

        map.get(GameRules.Category.MISC).keySet().removeIf(key -> !key.equals(ENABLE_CRAFTING_TIME_AND_LEVEL));







    }
}
