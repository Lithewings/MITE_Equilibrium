package com.equilibrium.server_and_client.server.event.break_block_strategy;

import com.equilibrium.item.material.MaterialItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class GravelDropStrategy implements BlockDropStrategy {
    // 保底计数器：连续不掉落沙砾的次数，达到12次后强制掉落
    private static int guarantee = 0;

    @Override
    public boolean canHandle(BlockState state) {
        return state.getBlock() == Blocks.GRAVEL;
    }

    @Override
    public void handleDrop(Level world, Player player, BlockPos pos, BlockState state, ItemStack tool) {
        int fortuneLevel = EnchantmentUtils.getFortuneLevel(world, tool);
        int silkTouchLevel = EnchantmentUtils.getSilkTouchLevel(world, tool);

        // 精准采集：直接掉落沙砾
        if (silkTouchLevel > 0) {
            world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    new ItemStack(Blocks.GRAVEL)));
            return;
        }

        // 判断是否掉落沙砾（受时运影响，保底机制）
        int gravelDropChance = 75 - fortuneLevel * 15;
        if (world.getRandom().nextInt(100) < gravelDropChance && guarantee < 12) {
            guarantee++;
            world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    new ItemStack(Blocks.GRAVEL)));
            return;
        } else {
            guarantee = 0; // 重置保底计数器
        }

        // 掉落其他物品（概率表）
        int roll = world.getRandom().nextInt(1000);
        ItemStack dropStack;
        if (roll == 0) {
            dropStack = new ItemStack(Items.REDSTONE);          // 0.1%
        } else if (roll <= 100) {
            dropStack = new ItemStack(MaterialItems.SILVER_NUGGET.get()); // 10%
        } else if (roll <= 240) {
            dropStack = new ItemStack(Items.FLINT);              // 14%
        } else if (roll <= 400) {
            dropStack = new ItemStack(MaterialItems.COPPER_NUGGET.get()); // 16%
        } else {
            dropStack = new ItemStack(MaterialItems.FLINT.get()); // 59.9%
        }
        world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, dropStack));
    }
}