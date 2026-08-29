package com.equilibrium.util;


import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public final class EnchantmentUtils {
    private EnchantmentUtils() {}

    public static int getFortuneLevel(World world, ItemStack stack) {
        return EnchantmentHelper.getLevel(world.getRegistryManager()
                .get(RegistryKeys.ENCHANTMENT)
                .getEntry(Enchantments.FORTUNE).get(),stack);
    }

    public static int getSilkTouchLevel(World world, ItemStack stack) {
        return EnchantmentHelper.getLevel(world.getRegistryManager()
                .get(RegistryKeys.ENCHANTMENT)
                .getEntry(Enchantments.SILK_TOUCH).get(),stack);
    }
}