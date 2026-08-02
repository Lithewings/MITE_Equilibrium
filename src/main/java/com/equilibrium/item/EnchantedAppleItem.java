package com.equilibrium.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnchantedAppleItem extends Item {
    public EnchantedAppleItem(Properties settings) {
        super(settings);
    }
    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        super.onCraftedPostProcess(stack, world);
        player.giveExperiencePoints(-200);
//        player.sendMessage(Text.of("ItemStack is"+stack));
    }


    public static final FoodProperties GOLDEN_APPLE = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(1.2F)
            .effect(new MobEffectInstance(MobEffects.REGENERATION, 800, 1), 1.0F)
            .effect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0), 1.0F)
            .alwaysEdible()
            .build();

}
