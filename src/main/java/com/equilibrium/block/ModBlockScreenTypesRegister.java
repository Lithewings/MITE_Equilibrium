package com.equilibrium.block;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.block.anvil.adamantium_anvil_block.AdamantiumAnvilScreenHandler;
import com.equilibrium.block.anvil.iron_anvil_block.IronAnvilScreenHandler;
import com.equilibrium.block.anvil.mithril_anvil_block.MithrilAnvilScreenHandler;
import com.equilibrium.block.enchanting_table.ModEnchantmentScreenHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = OnServerInitialize.MOD_ID)
public class ModBlockScreenTypesRegister {

    // 保留字段声明，但不初始化
    public static MenuType<ModEnchantmentScreenHandler> EMERALD_ENCHANTING_TABLE;
    public static MenuType<ModEnchantmentScreenHandler> DIAMOND_ENCHANTING_TABLE;
    public static MenuType<IronAnvilScreenHandler> IRON_ANVIL_SCREEN_TYPE;
    public static MenuType<MithrilAnvilScreenHandler> MITHRIL_ANVIL_SCREEN_TYPE;
    public static MenuType<AdamantiumAnvilScreenHandler> ADAMANTIUM_ANVIL_SCREEN_TYPE;

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(BuiltInRegistries.MENU.key(), helper -> {
            // 注册菜单类型
            EMERALD_ENCHANTING_TABLE = Registry.register(
                    BuiltInRegistries.MENU,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "emerald_enchantment"),
                    new MenuType<>((syncId, playerInventory) -> new ModEnchantmentScreenHandler(syncId, playerInventory, 12), FeatureFlags.VANILLA_SET)
            );

            DIAMOND_ENCHANTING_TABLE = Registry.register(
                    BuiltInRegistries.MENU,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "diamond_enchantment"),
                    new MenuType<>((syncId, playerInventory) -> new ModEnchantmentScreenHandler(syncId, playerInventory, 24), FeatureFlags.VANILLA_SET)
            );

            IRON_ANVIL_SCREEN_TYPE = Registry.register(
                    BuiltInRegistries.MENU,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "iron_anvil"),
                    new MenuType<>(IronAnvilScreenHandler::new, FeatureFlags.VANILLA_SET)
            );

            MITHRIL_ANVIL_SCREEN_TYPE = Registry.register(
                    BuiltInRegistries.MENU,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "mithril_anvil"),
                    new MenuType<>(MithrilAnvilScreenHandler::new, FeatureFlags.VANILLA_SET)
            );

            ADAMANTIUM_ANVIL_SCREEN_TYPE = Registry.register(
                    BuiltInRegistries.MENU,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "adamantium_anvil"),
                    new MenuType<>(AdamantiumAnvilScreenHandler::new, FeatureFlags.VANILLA_SET)
            );
        });
    }
}