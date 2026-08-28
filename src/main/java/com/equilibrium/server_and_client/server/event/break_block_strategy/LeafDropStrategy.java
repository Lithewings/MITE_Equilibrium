package com.equilibrium.server_and_client.server.event.break_block_strategy;

import com.equilibrium.server_and_client.server.event.break_block_strategy.BlockDropStrategy;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class LeafDropStrategy implements BlockDropStrategy {
    @Override
    public boolean canHandle(BlockState state) {
        return state.is(BlockTags.LEAVES);
    }

    @Override
    public void handleDrop(Level world, Player player, BlockPos pos, BlockState state, ItemStack tool) {
        int fortuneLevel = EnchantmentUtils.getFortuneLevel(world, tool);

        // 时运降低掉落木棍的阈值
        int threshold = 100 - fortuneLevel * 30;
        if (world.getRandom().nextInt(threshold) <= 10) {
            ItemEntity drop = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    new ItemStack(Items.STICK));
            world.addFreshEntity(drop);
        }
    }
}