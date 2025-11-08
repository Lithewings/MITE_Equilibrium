package com.equilibrium.block;


import com.equilibrium.block.crop.OnionBlock;
import com.equilibrium.block.enchanting_table.EmeraldEnchantingTableBlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static com.equilibrium.MITEequilibrium.MOD_ID;
import static net.minecraft.block.Blocks.createLightLevelFromLitBlockState;
import static net.minecraft.block.Blocks.register;

public class ModBlocks {




    public static final Block ONION_BLOCK = new OnionBlock(
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.DARK_GREEN)
                            .noCollision()
                            .ticksRandomly()
                            .breakInstantly()
                            .sounds(BlockSoundGroup.CROP)
                            .pistonBehavior(PistonBehavior.DESTROY)
    );


    public static final Block EMERALD_ENCHANTING_TABLE = new EmeraldEnchantingTableBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(NoteBlockInstrument.BASEDRUM).luminance(state->7).strength(0.2F, 1200.0F).nonOpaque());


    public static final Block EXAMPLE_BLOCK = new Block(Block.Settings.create().strength(4.0f));


    public static final Block ADAMANTIUM_ORE = new Block(Block.Settings.create().strength(4.0f));
    public static final Block ADAMANTIUM_BLOCK = new Block(Block.Settings.create().strength(4.0f));


    public static final Block ANCIENT_METAL_BLOCK = new Block(Block.Settings.create().strength(3.0f));

    public static final Block COPPER_ORE = new Block(Block.Settings.create().strength(1.0f));
    public static final Block COPPER_BLOCK = new Block(Block.Settings.create().strength(4.0f));

    public static final Block MITHRIL_ORE = new Block(Block.Settings.create().strength(2.0f));
    public static final Block MITHRIL_BLOCK = new Block(Block.Settings.create().strength(4.0f));

    public static final Block SILVER_ORE = new Block(Block.Settings.create().strength(1.0f));
    public static final Block SILVER_BLOCK = new Block(Block.Settings.create().strength(4.0f));

    public static final Block GOLD_ORE = new Block(Block.Settings.create().strength(1.0f));
    public static final Block GOLD_BLOCK = new Block(Block.Settings.create().strength(4.0f));

    //strength中第一个为硬度,第二个为爆炸抗性
//    public static final Block UNDERWORLD_PORTAL =
//            new UnderworldPortalBlock(
//                    AbstractBlock.Settings.create()
//                            .noCollision()
//                            .ticksRandomly()
//                            .strength(-1.0F)
//                            .sounds(BlockSoundGroup.GLASS)
//                            .luminance(state -> 11)
//                            .pistonBehavior(PistonBehavior.BLOCK)
//            );
//    public static final Block OVERWORLD_PORTAL =
//            new UnderworldPortalBlock(
//                    AbstractBlock.Settings.create()
//                            .noCollision()
//                            .ticksRandomly()
//                            .strength(-1.0F)
//                            .sounds(BlockSoundGroup.GLASS)
//                            .luminance(state -> 11)
//                            .pistonBehavior(PistonBehavior.BLOCK)
//            );
    public static void registerModBlocks(){


        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "onion"),ONION_BLOCK);






        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "emerald_enchanting_table"), EMERALD_ENCHANTING_TABLE);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "emerald_enchanting_table"), new BlockItem(EMERALD_ENCHANTING_TABLE, new Item.Settings()));


        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "example_block"), EXAMPLE_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "example_block"), new BlockItem(EXAMPLE_BLOCK, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "adamantium_ore"), ADAMANTIUM_ORE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "adamantium_ore"), new BlockItem(ADAMANTIUM_ORE, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "adamantium_block"), ADAMANTIUM_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "adamantium_block"), new BlockItem(ADAMANTIUM_BLOCK, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "ancient_metal_block"), ANCIENT_METAL_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "ancient_metal_block"), new BlockItem(ANCIENT_METAL_BLOCK, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "copper_ore"), COPPER_ORE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "copper_ore"), new BlockItem(COPPER_ORE, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "copper_block"), COPPER_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "copper_block"), new BlockItem(COPPER_BLOCK, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "mithril_ore"), MITHRIL_ORE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "mithril_ore"), new BlockItem(MITHRIL_ORE, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "mithril_block"), MITHRIL_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "mithril_block"), new BlockItem(MITHRIL_BLOCK, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "silver_ore"), SILVER_ORE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "silver_ore"), new BlockItem(SILVER_ORE, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "silver_block"), SILVER_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "silver_block"), new BlockItem(SILVER_BLOCK, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "gold_ore"), GOLD_ORE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "gold_ore"), new BlockItem(GOLD_ORE, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "gold_block"), GOLD_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "gold_block"), new BlockItem(GOLD_BLOCK, new Item.Settings()));







//        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "underworld_portalblock"), UNDERWORLD_PORTAL);
//        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "underworld_portalblock"), new BlockItem(UNDERWORLD_PORTAL, new Item.Settings()));
//
//        Registry.register(Registries.BLOCK, Identifier.of("miteequilibrium", "overworld_portalblock"), OVERWORLD_PORTAL);
//        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium", "overworld_portalblock"), new BlockItem(OVERWORLD_PORTAL, new Item.Settings()));


    }
}
