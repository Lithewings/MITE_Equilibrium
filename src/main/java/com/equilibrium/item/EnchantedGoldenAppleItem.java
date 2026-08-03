package com.equilibrium.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class EnchantedGoldenAppleItem extends Item {

    public static final FoodProperties GOLDEN_APPLE = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(1.2F)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 800, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0), 1.0F)
            .alwaysEdible()
            .build();

    public EnchantedGoldenAppleItem(Properties properties) {
        super(properties.food(GOLDEN_APPLE));
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);
        player.giveExperiencePoints(-200);
    }
}