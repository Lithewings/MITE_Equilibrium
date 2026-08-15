package com.equilibrium.block.crafting_table;


import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import static com.equilibrium.block.UseBlockActionUtil.isTableBlocked;

public class TheCraftingTableBlock extends Block {
    public static  Component TITLE = Component.translatable("container.crafting");
    public static  Component TITLE1 = Component.translatable("container.flint_crafting");
    public static  Component TITLE2 = Component.translatable("container.copper_crafting");
    public static  Component TITLE_SILVER = Component.translatable("container.silver_crafting");
    public static  Component TITLE3 = Component.translatable("container.iron_crafting");
    public static  Component TITLE4 = Component.translatable("container.mithril_crafting");
    public static  Component TITLE5 = Component.translatable("container.adamantium_crafting");

    public TheCraftingTableBlock(Properties settings) {
        super(settings);
    }

    public static final MapCodec<CraftingTableBlock> CODEC = simpleCodec(CraftingTableBlock::new);




    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        //对于工作台(非原版)，如果正上方有方块阻挡，玩家就无法与其交互
        if(isTableBlocked(world, pos, player))
            return InteractionResult.PASS;


        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(world, pos));
            player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return InteractionResult.CONSUME;
        }

    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        Block block = state.getBlock();
        if(block == CraftingTableBlocks.FLINT_CRAFTING_TABLE.get()){
            return new SimpleMenuProvider((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ContainerLevelAccess.create(world, pos));
            }, TITLE1);

        }else if(block == CraftingTableBlocks.COPPER_CRAFTING_TABLE.get()){
            return new SimpleMenuProvider((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ContainerLevelAccess.create(world, pos));
            }, TITLE2);
        }else if(block == CraftingTableBlocks.SILVER_CRAFTING_TABLE.get()){
            return new SimpleMenuProvider((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ContainerLevelAccess.create(world, pos));
            }, TITLE_SILVER);
        }else if(block == CraftingTableBlocks.IRON_CRAFTING_TABLE.get()){
            return new SimpleMenuProvider((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ContainerLevelAccess.create(world, pos));
            }, TITLE3);
        }else if(block == CraftingTableBlocks.MITHRIL_CRAFTING_TABLE.get()){
            return new SimpleMenuProvider((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ContainerLevelAccess.create(world, pos));
            }, TITLE4);
        }else if(block == CraftingTableBlocks.ADAMANTIUM_CRAFTING_TABLE.get()){
            return new SimpleMenuProvider((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ContainerLevelAccess.create(world, pos));
            }, TITLE5);
        }else {
            return new SimpleMenuProvider((syncId, inventory, player) -> {
                return new ModCraftingScreenHandler(syncId, inventory, ContainerLevelAccess.create(world, pos));
            }, TITLE);
        }
    }
}
