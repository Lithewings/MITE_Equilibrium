package com.equilibrium;

import com.equilibrium.block.ModBlockScreenTypesRegister;
import com.equilibrium.block.ModBlocksRegistry;
import com.equilibrium.block.anvil_block.adamantium_anvil_block.AdamantiumAnvilScreen;
import com.equilibrium.block.anvil_block.iron_anvil_block.IronAnvilScreen;
import com.equilibrium.block.anvil_block.mithril_anvil_block.MithrilAnvilScreen;
import com.equilibrium.block.enchanting_table.ModBlockEntityTypes;
import com.equilibrium.block.enchanting_table.ModEnchantmentScreen;
import com.equilibrium.block.enchanting_table.diamond.DiamondEnchantingTableBlockEntityRenderer;
import com.equilibrium.block.enchanting_table.emerald.EmeraldEnchantingTableBlockEntityRenderer;
import com.equilibrium.network.S2CGameRuleSyncPayloadForBooleanPacket;
import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import com.equilibrium.network.S2CStockChangeGrassColorPacket;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = OnServerInitialize.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = OnServerInitialize.MOD_ID, value = Dist.CLIENT)
public class OnClientInitialize {
    public OnClientInitialize(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        //S->C,发包、接收
        S2CStockChangeGrassColorPacket.registerOnClient();
        S2CIllnessTextureBooleanPacket.registerOnClient();
        S2CGameRuleSyncPayloadForBooleanPacket.registerOnClient();


    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 绑定菜单与屏幕
            MenuScreens.register(ModBlockScreenTypesRegister.EMERALD_ENCHANTING_TABLE, ModEnchantmentScreen::new);
            MenuScreens.register(ModBlockScreenTypesRegister.DIAMOND_ENCHANTING_TABLE, ModEnchantmentScreen::new);
            MenuScreens.register(ModBlockScreenTypesRegister.IRON_ANVIL_SCREEN_TYPE, IronAnvilScreen::new);
            MenuScreens.register(ModBlockScreenTypesRegister.MITHRIL_ANVIL_SCREEN_TYPE, MithrilAnvilScreen::new);
            MenuScreens.register(ModBlockScreenTypesRegister.ADAMANTIUM_ANVIL_SCREEN_TYPE, AdamantiumAnvilScreen::new);

            // 注册方块实体渲染器
            BlockEntityRenderers.register(ModBlockEntityTypes.EMERALD_ENCHANTING_TABLE_BLOCK_ENTITY_TYPE,
                    EmeraldEnchantingTableBlockEntityRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityTypes.DIAMOND_ENCHANTING_TABLE_BLOCK_ENTITY_TYPE,
                    DiamondEnchantingTableBlockEntityRenderer::new);


            BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), ModBlocksRegistry.ONION_BLOCK);




        });
    }
}