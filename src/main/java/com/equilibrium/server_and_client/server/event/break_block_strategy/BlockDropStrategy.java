package com.equilibrium.server_and_client.server.event.break_block_strategy;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockDropStrategy {
    boolean canHandle(BlockState state);
    void handleDrop(World world, PlayerEntity player, BlockPos pos, BlockState state, ItemStack tool);
}