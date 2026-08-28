package com.equilibrium.server_and_client.server.event.break_block_strategy;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public final class EnchantmentUtils {
    private EnchantmentUtils() {}

    public static int getFortuneLevel(Level world, ItemStack stack) {
        return EnchantmentHelper.getTagEnchantmentLevel(
                world.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                        .getHolder(Enchantments.FORTUNE).orElseThrow(),
                stack);
    }

    public static int getSilkTouchLevel(Level world, ItemStack stack) {
        return EnchantmentHelper.getTagEnchantmentLevel(
                world.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                        .getHolder(Enchantments.SILK_TOUCH).orElseThrow(),
                stack);
    }
}