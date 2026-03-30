package com.equilibrium;


import com.equilibrium.block.ModBlocksRegistry;
import com.equilibrium.block.enchanting_table.*;
import com.equilibrium.server_and_client.client.render.entity.model.BaseEarthElementalEntityModel;
import com.equilibrium.server_and_client.client.render.entity.renderer.*;
import com.equilibrium.server_and_client.client.render.entity.renderer.elemental.EndRockElementalEntityRenderer;
import com.equilibrium.server_and_client.client.render.entity.renderer.elemental.NetherrackElementalEntityRenderer;
import com.equilibrium.server_and_client.client.render.entity.renderer.elemental.ObsidianElementalEntityRenderer;
import com.equilibrium.server_and_client.client.render.entity.renderer.elemental.StoneElementalEntityRenderer;
import com.equilibrium.item.Armors;
import com.equilibrium.network.S2CGameRuleSyncPayloadForBooleanPacket;
import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import com.equilibrium.network.S2CStockChangeGrassColorPacket;
import com.equilibrium.server_and_client.client.command.ClientCommands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


import static com.equilibrium.entity.ModEntities.*;

import static com.equilibrium.util.RenderBeaconBeam.RenderBeaconInit;


public class OnClientInitialize implements ClientModInitializer {




//    @Override
    public void onInitializeClient() {




        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocksRegistry.ONION_BLOCK);
        RenderBeaconInit();





        //S->C,发包、接收
        S2CStockChangeGrassColorPacket.registerOnClient();
        S2CIllnessTextureBooleanPacket.registerOnClient();
        S2CGameRuleSyncPayloadForBooleanPacket.registerOnClient();

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            // 判断物品是青金石（Lapis Lazuli）或其他物品
            if (stack.getItem() == Items.LAPIS_LAZULI) {
                lines.add(Text.literal("25XP").formatted(Formatting.DARK_GRAY));
            }
            if (stack.getItem() == Items.QUARTZ) {
                lines.add(Text.literal("50XP").formatted(Formatting.DARK_GRAY));
            }
            if (stack.getItem() == Items.DIAMOND) {
                lines.add(Text.literal("500XP").formatted(Formatting.DARK_GRAY));
            }
            if (stack.getItem() == Items.EMERALD) {
                lines.add(Text.literal("250XP").formatted(Formatting.DARK_GRAY));
            }
            if (stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                lines.add(Text.literal("Regeneration II（00:40）").formatted(Formatting.BLUE));
                lines.add(Text.literal("Resistance II（00:40）").formatted(Formatting.BLUE));
                lines.add(Text.literal("Fire Resistance（00:40）").formatted(Formatting.BLUE));

            }
            if (stack.getItem() == Items.GOLDEN_APPLE) {
                lines.add(Text.literal("Regeneration I（00:20）").formatted(Formatting.BLUE));
            }
            if (stack.getItem() == Armors.MITHRIL_CHEST_PLATE) {
                lines.add(Text.literal("Regeneration: Doubles the natural health recovery rate").formatted(Formatting.BLUE));
            }
        });


        HandledScreens.register(ModScreenTypes.EMERALD_ENCHANTING_TABLE, ModEnchantmentScreen::new);



        BlockEntityRendererFactories.register(ModBlockEntityTypes.ENCHANTING_TABLE_BLOCK_ENTITY_TYPE, ModEnchantingTableBlockEntityRenderer::new);
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
            ModelPart modelPart = context.getPart(EntityModelLayers.ZOMBIE);
            return new StoneElementalEntityRenderer(context, new BaseEarthElementalEntityModel<>(modelPart), 0.5f);
        });

        EntityRendererRegistry.register(END_ROCK_ELEMENTAL, (context) -> {
            ModelPart modelPart = context.getPart(EntityModelLayers.ZOMBIE);
            return new EndRockElementalEntityRenderer(context, new BaseEarthElementalEntityModel<>(modelPart), 0.5f);
        });

        EntityRendererRegistry.register(NETHERROCK_ELEMENTAL, (context) -> {
            ModelPart modelPart = context.getPart(EntityModelLayers.ZOMBIE);
            return new NetherrackElementalEntityRenderer(context, new BaseEarthElementalEntityModel<>(modelPart), 0.5f);
        });

        EntityRendererRegistry.register(OBSIDIAN_ELEMENTAL, (context) -> {
            ModelPart modelPart = context.getPart(EntityModelLayers.ZOMBIE);
            return new ObsidianElementalEntityRenderer(context, new BaseEarthElementalEntityModel<>(modelPart), 0.5f);
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ClientCommands.registerClientAllCommands(dispatcher);
        });

    }


}
