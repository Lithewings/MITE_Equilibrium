package com.equilibrium.block.enchanting_table;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenTypes {
    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }
    public static final ScreenHandlerType<ModEnchantmentScreenHandler> EMERALD_ENCHANTING_TABLE = register("miteequilibrium:emerald_enchantment", ModEnchantmentScreenHandler::new);
    public static void registerScreenHandlers() {
        // 空方法，注册已经在静态初始化中完成
    }
}