package com.equilibrium;


import com.equilibrium.block.ModBlocks;
import com.equilibrium.block.enchanting_table.*;
import com.equilibrium.client.render.entity.renderer.*;
import com.equilibrium.item.Armors;
import com.equilibrium.util.MyCommands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


import static com.equilibrium.entity.ModEntities.*;


public class MITEequilibriumClient implements ClientModInitializer {




    @Override
    public void onInitializeClient() {

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.ONION_BLOCK);

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

//        registerScreen();

        BlockEntityRendererFactories.register(ModBlockEntityTypes.ENCHANTING_TABLE_BLOCK_ENTITY_TYPE, ModEnchantingTableBlockEntityRenderer::new);
        //注册渲染器(渲染器中包含了实体和模型)


        EntityRendererRegistry.register(INVISIBLE_STALKER, InvisibleStalkerEntityRenderer::new);
        EntityRendererRegistry.register(GHOUL, GhoulEntityRenderer::new);
        EntityRendererRegistry.register(SHADOW, ShadowEntityRenderer::new);
        EntityRendererRegistry.register(WIGHT, WightEntityRenderer::new);

        EntityRendererRegistry.register(LONG_DEAD, LongDeadEntityRenderer::new);
        EntityRendererRegistry.register(PUDDING, PuddingSlimeEntityRenderer::new);


        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            MyCommands.registerClientAllCommands(dispatcher);
        });

    }


}
