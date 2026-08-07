package com.equilibrium.block.miscellaneous;

import com.equilibrium.block.crop_blocks.BlueberryBushBlock;
import com.equilibrium.block.crop_blocks.OnionBlock;
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
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class MiscellaneousBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredBlock<OnionBlock> ONION_BLOCK = BLOCKS.register("onion",
            () -> new OnionBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).noCollission().randomTicks()
                    .instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<BlueberryBushBlock> BLUEBERRY_BUSH = BLOCKS.register("blueberry_bush",
            () -> new BlueberryBushBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .sound(SoundType.SWEET_BERRY_BUSH)
                    .strength(0.2f)
                    .noOcclusion()
                    .isViewBlocking((s, l, p) -> false)
                    .isSuffocating((s, l, p) -> false)
            ));

    public static final DeferredItem<BlockItem> BLUEBERRY_BUSH_ITEM = ITEMS.register("blueberry_bush",
            () -> new BlockItem(BLUEBERRY_BUSH.get(), new Item.Properties().stacksTo(16)));

    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(4.0f)));

    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block",
            () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties().stacksTo(16)));

    public static final DeferredBlock<ColoredFallingBlock> MUNDANE_GRAVEL = BLOCKS.register("mundane_gravel",
            () -> new ColoredFallingBlock(new ColorRGBA(-8356741),
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                            .instrument(NoteBlockInstrument.SNARE).strength(0.6F).sound(SoundType.GRAVEL)));

    public static final DeferredItem<BlockItem> MUNDANE_GRAVEL_ITEM = ITEMS.register("mundane_gravel",
            () -> new BlockItem(MUNDANE_GRAVEL.get(), new Item.Properties().stacksTo(16)));
}