package com.equilibrium.block.furnace_and_its_entity;

import com.equilibrium.block.ModBlocksRegistry2;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TheFurnaceEntity extends AbstractFurnaceBlockEntity {
    public TheFurnaceEntity(BlockPos pos, BlockState state) {
        super(FurnaceEntityRegistry.THE_FURNACE.get(), pos, state, RecipeType.SMELTING);
    }

    @Override
    public Component getDefaultName() {
        Block block = this.level.getBlockState(this.getBlockPos()).getBlock();
        if(block == ModBlocksRegistry2.CLAY_FURNACE){
            return Component.translatable("container.clay_furnace");
        }
        if(block == ModBlocksRegistry2.NETHERRACK_FURNACE){
            return Component.translatable("container.netherrack_furnace");
        }
        if(block == ModBlocksRegistry2.OBSIDIAN_FURNACE){
            return Component.translatable("container.obsidian_furnace");
        }
        return Component.translatable("container.furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new FurnaceMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    public int getBurnDuration(ItemStack fuel) {
        //此处world必须判断是否为null，否则熔炉数据无法保存。
        if(this.getLevel() != null){
            Block block = this.level.getBlockState(this.getBlockPos()).getBlock();
            if(block == ModBlocksRegistry2.CLAY_FURNACE){
                return (int) (super.getBurnDuration(fuel));
            }
            if(block == ModBlocksRegistry2.OBSIDIAN_FURNACE){
                return (int) (super.getBurnDuration(fuel) / 5);
            }
            if(block == ModBlocksRegistry2.NETHERRACK_FURNACE){
                return (int) (super.getBurnDuration(fuel) / 10);
            }
        }
        return (int) (super.getBurnDuration(fuel));
    }

}
