package com.equilibrium.server_and_client.server.event.break_block_strategy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockDropStrategy {
    /**
     * 判断该策略是否适用于给定的方块状态。
     */
    boolean canHandle(BlockState state);

    /**
     * 处理方块掉落。
     */
    void handleDrop(Level world, Player player, BlockPos pos, BlockState state, ItemStack tool);
}
