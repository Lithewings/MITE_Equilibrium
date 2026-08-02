package com.equilibrium.block;

import com.equilibrium.block.anvil_block.adamantium_anvil_block.AdamantiumAnvilBlock;
import com.equilibrium.block.anvil_block.iron_anvil_block.IronAnvilBlock;
import com.equilibrium.block.anvil_block.mithril_anvil_block.MithrilAnvilBlock;
import com.equilibrium.block.crop_blocks.OnionBlock;
import com.equilibrium.block.enchanting_table.diamond.DiamondEnchantingTableBlock;
import com.equilibrium.block.enchanting_table.emerald.EmeraldEnchantingTableBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import static com.equilibrium.OnServerInitialize.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class ModBlocksRegistry {

    // 保留 Block 类型，但不在这里实例化
    public static Block ONION_BLOCK;
    public static Block IRON_ANVIL;
    public static Block MITHRIL_ANVIL;
    public static Block ADAMANTIUM_ANVIL;
    public static Block EMERALD_ENCHANTING_TABLE;
    public static Block DIAMOND_ENCHANTING_TABLE;
    public static Block EXAMPLE_BLOCK;
    public static Block ADAMANTIUM_ORE;
    public static Block ADAMANTIUM_BLOCK;
    public static Block ANCIENT_METAL_BLOCK;
    public static Block COPPER_ORE;
    public static Block COPPER_BLOCK;
    public static Block MITHRIL_ORE;
    public static Block MITHRIL_BLOCK;
    public static Block SILVER_ORE;
    public static Block SILVER_BLOCK;
    public static Block GOLD_ORE;
    public static Block GOLD_BLOCK;
    public static Block MUNDANE_GRAVEL;

    @SubscribeEvent
    public static void registerBlocks(RegisterEvent event) {
        // 仅处理方块注册
        event.register(BuiltInRegistries.BLOCK.key(), helper -> {
            // 创建方块实例并注册
            ONION_BLOCK = new OnionBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).noCollission().randomTicks()
                    .instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, "onion"), ONION_BLOCK);

            IRON_ANVIL = new IronAnvilBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(0.2F, 1200.0F)
                    .sound(SoundType.ANVIL).pushReaction(PushReaction.BLOCK));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, "iron_anvil"), IRON_ANVIL);

            MITHRIL_ANVIL = new MithrilAnvilBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(0.2F, 1200.0F)
                    .sound(SoundType.ANVIL).pushReaction(PushReaction.BLOCK));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, "mithril_anvil"), MITHRIL_ANVIL);

            ADAMANTIUM_ANVIL = new AdamantiumAnvilBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(0.2F, 1200.0F)
                    .sound(SoundType.ANVIL).pushReaction(PushReaction.BLOCK));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, "adamantium_anvil"), ADAMANTIUM_ANVIL);

            EMERALD_ENCHANTING_TABLE = new EmeraldEnchantingTableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM)
                    .lightLevel(state -> 7).strength(0.01F, 1200.0F).noOcclusion());
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, "emerald_enchanting_table"), EMERALD_ENCHANTING_TABLE);

            DIAMOND_ENCHANTING_TABLE = new DiamondEnchantingTableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM)
                    .lightLevel(state -> 7).strength(0.01F, 1200.0F).noOcclusion());
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, "diamond_enchanting_table"), DIAMOND_ENCHANTING_TABLE);

            EXAMPLE_BLOCK = new Block(BlockBehaviour.Properties.of().strength(4.0f));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "example_block"), EXAMPLE_BLOCK);

            ADAMANTIUM_ORE = new Block(BlockBehaviour.Properties.of().strength(4.0f));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "adamantium_ore"), ADAMANTIUM_ORE);

            ADAMANTIUM_BLOCK = new Block(BlockBehaviour.Properties.of().strength(4.0f));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "adamantium_block"), ADAMANTIUM_BLOCK);

            ANCIENT_METAL_BLOCK = new Block(BlockBehaviour.Properties.of().strength(3.0f));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "ancient_metal_block"), ANCIENT_METAL_BLOCK);

            COPPER_ORE = new Block(BlockBehaviour.Properties.of().strength(1.0f));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "copper_ore"), COPPER_ORE);

            COPPER_BLOCK = new Block(BlockBehaviour.Properties.of().strength(4.0f));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "copper_block"), COPPER_BLOCK);

            MITHRIL_ORE = new Block(BlockBehaviour.Properties.of().strength(4.0f));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "mithril_ore"), MITHRIL_ORE);

            MITHRIL_BLOCK = new Block(BlockBehaviour.Properties.of().strength(4.0f));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "mithril_block"), MITHRIL_BLOCK);

            SILVER_ORE = new Block(BlockBehaviour.Properties.of().strength(1.0f));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "silver_ore"), SILVER_ORE);

            SILVER_BLOCK = new Block(BlockBehaviour.Properties.of().strength(4.0f));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "silver_block"), SILVER_BLOCK);

            GOLD_ORE = new Block(BlockBehaviour.Properties.of().strength(1.0f));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gold_ore"), GOLD_ORE);

            GOLD_BLOCK = new Block(BlockBehaviour.Properties.of().strength(4.0f));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gold_block"), GOLD_BLOCK);

            MUNDANE_GRAVEL = new ColoredFallingBlock(new ColorRGBA(-8356741),
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                            .instrument(NoteBlockInstrument.SNARE).strength(0.6F).sound(SoundType.GRAVEL));
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, "mundane_gravel"), MUNDANE_GRAVEL);
        });

        // 注册对应的物品（BlockItem），注意此时方块已赋值，可直接引用
        event.register(BuiltInRegistries.ITEM.key(), helper -> {
            //在food中已注册
//            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "onion"), new BlockItem(ONION_BLOCK, new Item.Properties()));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "iron_anvil"), new BlockItem(IRON_ANVIL, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "mithril_anvil"), new BlockItem(MITHRIL_ANVIL, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "adamantium_anvil"), new BlockItem(ADAMANTIUM_ANVIL, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "emerald_enchanting_table"), new BlockItem(EMERALD_ENCHANTING_TABLE, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "diamond_enchanting_table"), new BlockItem(DIAMOND_ENCHANTING_TABLE, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "example_block"), new BlockItem(EXAMPLE_BLOCK, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "adamantium_ore"), new BlockItem(ADAMANTIUM_ORE, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "adamantium_block"), new BlockItem(ADAMANTIUM_BLOCK, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "ancient_metal_block"), new BlockItem(ANCIENT_METAL_BLOCK, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "copper_ore"), new BlockItem(COPPER_ORE, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "copper_block"), new BlockItem(COPPER_BLOCK, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "mithril_ore"), new BlockItem(MITHRIL_ORE, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "mithril_block"), new BlockItem(MITHRIL_BLOCK, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "silver_ore"), new BlockItem(SILVER_ORE, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "silver_block"), new BlockItem(SILVER_BLOCK, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gold_ore"), new BlockItem(GOLD_ORE, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gold_block"), new BlockItem(GOLD_BLOCK, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "mundane_gravel"), new BlockItem(MUNDANE_GRAVEL, new Item.Properties().stacksTo(16)));
        });
    }
}