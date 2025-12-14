package com.equilibrium.block;


import com.equilibrium.craft_time_register.BlockInit;
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
    public static  Text TITLE3 = Text.translatable("container.iron_crafting");
    public static  Text TITLE4 = Text.translatable("container.diamond_crafting");
    public static  Text TITLE5 = Text.translatable("container.netherite_crafting");
    public static  Text TITLE6 = Text.translatable("container.silver_crafting");

    public TheCraftingTableBlock(Settings settings) {
        super(settings);
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        Block block = state.getBlock();
        if(block == BlockInit.FLINT_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE1);

        }else if(block == BlockInit.COPPER_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE2);
        }else if(block == BlockInit.SILVER_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE6);
        }else if(block == BlockInit.IRON_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE3);
        }else if(block == BlockInit.DIAMOND_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE4);
        }else if(block == BlockInit.NETHERITE_CRAFTING_TABLE){
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
