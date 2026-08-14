package com.equilibrium.block.crafting_table;


import com.equilibrium.block.ModBlocksRegistry2;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.equilibrium.block.UseBlockActionUtil.isTableBlocked;

public class TheCraftingTableBlock extends Block {
    public static  Text TITLE = Text.translatable("container.crafting");
    public static  Text TITLE1 = Text.translatable("container.flint_crafting");
    public static  Text TITLE2 = Text.translatable("container.copper_crafting");
    public static  Text TITLE_SILVER = Text.translatable("container.silver_crafting");
    public static  Text TITLE3 = Text.translatable("container.iron_crafting");
    public static  Text TITLE4 = Text.translatable("container.mithril_crafting");
    public static  Text TITLE5 = Text.translatable("container.adamantium_crafting");

    public TheCraftingTableBlock(Settings settings) {
        super(settings);
    }

    public static final MapCodec<CraftingTableBlock> CODEC = createCodec(CraftingTableBlock::new);




    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        //对于工作台(非原版)，如果正上方有方块阻挡，玩家就无法与其交互
        if(isTableBlocked(world, pos, player))
            return ActionResult.PASS;


        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return ActionResult.CONSUME;
        }

    }

    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        Block block = state.getBlock();
        if(block == ModBlocksRegistry2.FLINT_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE1);

        }else if(block == ModBlocksRegistry2.COPPER_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE2);
        }else if(block == ModBlocksRegistry2.SILVER_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE_SILVER);
        }else if(block == ModBlocksRegistry2.IRON_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE3);
        }else if(block == ModBlocksRegistry2.MITHRIL_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE4);
        }else if(block == ModBlocksRegistry2.ADAMANTIUM_CRAFTING_TABLE){
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE5);
        }else {
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
            }, TITLE);
        }
    }
}
