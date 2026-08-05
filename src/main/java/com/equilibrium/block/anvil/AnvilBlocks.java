package com.equilibrium.block.anvil;

import com.equilibrium.block.anvil.adamantium_anvil_block.AdamantiumAnvilBlock;
import com.equilibrium.block.anvil.iron_anvil_block.IronAnvilBlock;
import com.equilibrium.block.anvil.mithril_anvil_block.MithrilAnvilBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class AnvilBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredBlock<IronAnvilBlock> IRON_ANVIL = BLOCKS.register("iron_anvil",
            () -> new IronAnvilBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(0.2F, 1200.0F)
                    .sound(SoundType.ANVIL).pushReaction(PushReaction.BLOCK)));

    public static final DeferredBlock<MithrilAnvilBlock> MITHRIL_ANVIL = BLOCKS.register("mithril_anvil",
            () -> new MithrilAnvilBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(0.2F, 1200.0F)
                    .sound(SoundType.ANVIL).pushReaction(PushReaction.BLOCK)));

    public static final DeferredBlock<AdamantiumAnvilBlock> ADAMANTIUM_ANVIL = BLOCKS.register("adamantium_anvil",
            () -> new AdamantiumAnvilBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(0.2F, 1200.0F)
                    .sound(SoundType.ANVIL).pushReaction(PushReaction.BLOCK)));

    public static final DeferredItem<BlockItem> IRON_ANVIL_ITEM = ITEMS.register("iron_anvil",
            () -> new BlockItem(IRON_ANVIL.get(), new Item.Properties().stacksTo(16)));

    public static final DeferredItem<BlockItem> MITHRIL_ANVIL_ITEM = ITEMS.register("mithril_anvil",
            () -> new BlockItem(MITHRIL_ANVIL.get(), new Item.Properties().stacksTo(16)));

    public static final DeferredItem<BlockItem> ADAMANTIUM_ANVIL_ITEM = ITEMS.register("adamantium_anvil",
            () -> new BlockItem(ADAMANTIUM_ANVIL.get(), new Item.Properties().stacksTo(16)));
}
