package com.equilibrium.block.crafting_table;


import com.equilibrium.block.ModBlocksRegistry2;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TheCraftingTableBlock extends CraftingTableBlock {
    public static  Text TITLE = Text.translatable("container.crafting");
    public static  Text TITLE1 = Text.translatable("container.flint_crafting");
    public static  Text TITLE2 = Text.translatable("container.copper_crafting");
    public static  Text TITLE_SILVER = Text.translatable("container.silver_crafting");
    public static  Text TITLE3 = Text.translatable("container.iron_crafting");
    public static  Text TITLE4 = Text.translatable("container.diamond_crafting");
    public static  Text TITLE5 = Text.translatable("container.netherite_crafting");

    public TheCraftingTableBlock(Settings settings) {
        super(settings);
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        Block block = state.getBlock();
        if(block == ModBlocksRegistry2.FLINT_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE1);

        }else if(block == ModBlocksRegistry2.COPPER_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE2);
        }else if(block == ModBlocksRegistry2.SILVER_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE_SILVER);
        }else if(block == ModBlocksRegistry2.IRON_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE3);
        }else if(block == ModBlocksRegistry2.DIAMOND_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE4);
        }else if(block == ModBlocksRegistry2.NETHERITE_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE5);
        }else {
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE);
        }
    }
}
