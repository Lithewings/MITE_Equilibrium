package com.equilibrium.block;

import com.equilibrium.block.anvil_block.adamantium_anvil_block.AdamantiumScreenHandler;
import com.equilibrium.block.anvil_block.iron_anvil_block.IronAnvilScreenHandler;
import com.equilibrium.block.anvil_block.mithril_anvil_block.MithrilAnvilScreenHandler;
import com.equilibrium.block.enchanting_table.ModEnchantmentScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class ModBlockScreenTypesRegister {
    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }


    public static final ScreenHandlerType<ModEnchantmentScreenHandler> EMERALD_ENCHANTING_TABLE = register("miteequilibrium:emerald_enchantment", (syncId, playerInventory)->new ModEnchantmentScreenHandler(syncId,playerInventory,12));
    public static final ScreenHandlerType<IronAnvilScreenHandler> IRON_ANVIL_SCREEN_TYPE = register("miteequilibrium:iron_anvil",IronAnvilScreenHandler::new);
    public static final ScreenHandlerType<MithrilAnvilScreenHandler> MITHRIL_ANVIL_SCREEN_TYPE = register("miteequilibrium:mithril_anvil", MithrilAnvilScreenHandler::new);
    public static final ScreenHandlerType<AdamantiumScreenHandler> ADAMANTIUM_ANVIL_SCREEN_TYPE = register("miteequilibrium:adamantium_anvil", AdamantiumScreenHandler::new);


    public static void registerScreenHandlers() {
        // 空方法，注册已经在静态初始化中完成
    }
}