package com.equilibrium;

import com.equilibrium.block.ModBlockScreenTypesRegister;
import com.equilibrium.block.crafting_table.ModCraftingScreen;
import com.equilibrium.block.crafting_table.ModCraftingScreenHandler;
import com.equilibrium.block.miscellaneous.MiscellaneousBlocks;
import com.equilibrium.block.anvil.adamantium_anvil_block.AdamantiumAnvilScreen;
import com.equilibrium.block.anvil.iron_anvil_block.IronAnvilScreen;
import com.equilibrium.block.anvil.mithril_anvil_block.MithrilAnvilScreen;
import com.equilibrium.block.enchanting_table.ModBlockEntityTypes;
import com.equilibrium.block.enchanting_table.ModEnchantmentScreen;
import com.equilibrium.block.enchanting_table.diamond.DiamondEnchantingTableBlockEntityRenderer;
import com.equilibrium.block.enchanting_table.emerald.EmeraldEnchantingTableBlockEntityRenderer;
import com.equilibrium.item.armor.ArmorItems;
import com.equilibrium.item.food.FoodItems;
import com.equilibrium.network.S2CGameRuleSyncPayloadForBooleanPacket;
import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import com.equilibrium.network.S2CStockChangeGrassColorPacket;
import com.equilibrium.server_and_client.client.command.ClientCommands;
import com.equilibrium.server_and_client.client.render.entity.model.BaseEarthElementalEntityModel;
import com.equilibrium.server_and_client.client.render.entity.renderer.*;
import com.equilibrium.server_and_client.client.render.entity.renderer.elemental.EndRockElementalEntityRenderer;
import com.equilibrium.server_and_client.client.render.entity.renderer.elemental.NetherrackElementalEntityRenderer;
import com.equilibrium.server_and_client.client.render.entity.renderer.elemental.ObsidianElementalEntityRenderer;
import com.equilibrium.server_and_client.client.render.entity.renderer.elemental.StoneElementalEntityRenderer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import static com.equilibrium.entity.ModEntities.*;
import static com.equilibrium.entity.ModEntities.OBSIDIAN_ELEMENTAL;
import static com.equilibrium.util.RenderBeaconBeam.RenderBeaconInit;

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
            MenuScreens.register(ModBlockScreenTypesRegister.MOD_CRAFTING_SCREEN_HANDLER_SCREEN_HANDLER_TYPE, ModCraftingScreen::new);
            // 注册方块实体渲染器
            BlockEntityRenderers.register(ModBlockEntityTypes.EMERALD_ENCHANTING_TABLE_BLOCK_ENTITY_TYPE,
                    EmeraldEnchantingTableBlockEntityRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityTypes.DIAMOND_ENCHANTING_TABLE_BLOCK_ENTITY_TYPE,
                    DiamondEnchantingTableBlockEntityRenderer::new);


            BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), MiscellaneousBlocks.ONION_BLOCK.get());
            BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), MiscellaneousBlocks.BLUEBERRY_BUSH.get());

            ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
                // 判断物品是青金石（Lapis Lazuli）或其他物品
                if (stack.getItem() == Items.LAPIS_LAZULI) {
                    lines.add(Component.literal("25XP").withStyle(ChatFormatting.DARK_GRAY));
                }
                if (stack.getItem() == Items.QUARTZ) {
                    lines.add(Component.literal("50XP").withStyle(ChatFormatting.DARK_GRAY));
                }
                if (stack.getItem() == Items.DIAMOND) {
                    lines.add(Component.literal("500XP").withStyle(ChatFormatting.DARK_GRAY));
                }
                if (stack.getItem() == Items.EMERALD) {
                    lines.add(Component.literal("250XP").withStyle(ChatFormatting.DARK_GRAY));
                }
                if (stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                    lines.add(Component.literal("Regeneration II（00:40）").withStyle(ChatFormatting.BLUE));
                    lines.add(Component.literal("Resistance II（00:40）").withStyle(ChatFormatting.BLUE));
                    lines.add(Component.literal("Fire Resistance（00:40）").withStyle(ChatFormatting.BLUE));

                }
                if (stack.getItem() == Items.GOLDEN_APPLE) {
                    lines.add(Component.literal("Regeneration I（00:20）").withStyle(ChatFormatting.BLUE));
                }
                if (stack.getItem() == ArmorItems.MITHRIL_CHEST_PLATE.get()) {
                    lines.add(Component.literal("Regeneration: Doubles the natural health recovery rate").withStyle(ChatFormatting.BLUE));
                }
            });

            //注册渲染器(渲染器中包含了实体和模型)


            EntityRendererRegistry.register(INVISIBLE_STALKER, InvisibleStalkerEntityRendererTransparent::new);
            EntityRendererRegistry.register(GHOUL, GhoulEntityRenderer::new);
            EntityRendererRegistry.register(SHADOW, ShadowEntityRenderer::new);
            EntityRendererRegistry.register(WIGHT, WightEntityRenderer::new);
            EntityRendererRegistry.register(REVENANT, RevenantEntityRenderer::new);
            EntityRendererRegistry.register(LONG_DEAD, LongDeadEntityRenderer::new);
            EntityRendererRegistry.register(PUDDING, PuddingSlimeEntityRenderer::new);
            EntityRendererRegistry.register(BONE_LORD, BoneLordEntityRenderer::new);
            EntityRendererRegistry.register(WOODEN_SPIDER, WoodenSpiderRenderer::new);
            EntityRendererRegistry.register(FIRE_ELEMENTAL, FireElementalEntityRendererTransparent::new);


            //模型为两足生物,定义了所有关节如头部,手脚腿的部分,如何活动这里借助原版僵尸的逻辑:正常走路,一直举着手移动,攻击时挥手等等
            EntityRendererRegistry.register(STONE_ELEMENTAL, (context) -> {
                ModelPart modelPart = context.bakeLayer(ModelLayers.ZOMBIE);
                return new StoneElementalEntityRenderer(context, new BaseEarthElementalEntityModel<>(modelPart), 0.5f);
            });

            EntityRendererRegistry.register(END_ROCK_ELEMENTAL, (context) -> {
                ModelPart modelPart = context.bakeLayer(ModelLayers.ZOMBIE);
                return new EndRockElementalEntityRenderer(context, new BaseEarthElementalEntityModel<>(modelPart), 0.5f);
            });

            EntityRendererRegistry.register(NETHERROCK_ELEMENTAL, (context) -> {
                ModelPart modelPart = context.bakeLayer(ModelLayers.ZOMBIE);
                return new NetherrackElementalEntityRenderer(context, new BaseEarthElementalEntityModel<>(modelPart), 0.5f);
            });

            EntityRendererRegistry.register(OBSIDIAN_ELEMENTAL, (context) -> {
                ModelPart modelPart = context.bakeLayer(ModelLayers.ZOMBIE);
                return new ObsidianElementalEntityRenderer(context, new BaseEarthElementalEntityModel<>(modelPart), 0.5f);
            });

            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
                ClientCommands.registerClientAllCommands(dispatcher);
            });

            //信标柱渲染
            RenderBeaconInit();
        });
    }
}