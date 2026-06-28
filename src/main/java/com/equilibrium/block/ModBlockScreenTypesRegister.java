package com.equilibrium.block;

import com.equilibrium.block.anvil_block.IronAnvilBlock.IronAnvilScreenHandler;
import com.equilibrium.block.anvil_block.IronAnvilScreenHandler2;
import com.equilibrium.block.enchanting_table.ModEnchantmentScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class ModBlockScreenTypesRegister {
    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }


    public static final ScreenHandlerType<ModEnchantmentScreenHandler> EMERALD_ENCHANTING_TABLE = register("miteequilibrium:emerald_enchantment", ModEnchantmentScreenHandler::new);
    public static final ScreenHandlerType<IronAnvilScreenHandler> IRON_ANVIL_SCREEN_TYPE = register("miteequilibrium:iron_anvil",IronAnvilScreenHandler::new);


    public static void registerScreenHandlers() {
        // 空方法，注册已经在静态初始化中完成
    }
}