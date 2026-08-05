package com.equilibrium.block.enchanting_table;

import com.equilibrium.block.enchanting_table.diamond.DiamondEnchantingTableBlock;
import com.equilibrium.block.enchanting_table.emerald.EmeraldEnchantingTableBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class EnchantingTableBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredBlock<EmeraldEnchantingTableBlock> EMERALD_ENCHANTING_TABLE = BLOCKS.register("emerald_enchanting_table",
            () -> new EmeraldEnchantingTableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM)
                    .lightLevel(state -> 7).strength(0.01F, 1200.0F).noOcclusion()));

    public static final DeferredBlock<DiamondEnchantingTableBlock> DIAMOND_ENCHANTING_TABLE = BLOCKS.register("diamond_enchanting_table",
            () -> new DiamondEnchantingTableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM)
                    .lightLevel(state -> 7).strength(0.01F, 1200.0F).noOcclusion()));

    public static final DeferredItem<BlockItem> EMERALD_ENCHANTING_TABLE_ITEM = ITEMS.register("emerald_enchanting_table",
            () -> new BlockItem(EMERALD_ENCHANTING_TABLE.get(), new Item.Properties().stacksTo(16)));

    public static final DeferredItem<BlockItem> DIAMOND_ENCHANTING_TABLE_ITEM = ITEMS.register("diamond_enchanting_table",
            () -> new BlockItem(DIAMOND_ENCHANTING_TABLE.get(), new Item.Properties().stacksTo(16)));
}
