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
    // 保底计数器：全服务器共享，记录连续掉落沙砾的次数
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

        // 判断是否掉落沙砾（线程安全地更新保底计数器）
        int gravelDropChance = 75 - fortuneLevel * 15;
        boolean dropGravel = shouldDropGravel(world, gravelDropChance);
        if (dropGravel) {
            world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    new ItemStack(Blocks.GRAVEL)));
            return;
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
        } else if (roll <= 455) {
            dropStack = new ItemStack(MaterialItems.COPPER_NUGGET.get());// 21.5%
        } else {
            dropStack = new ItemStack(MaterialItems.FLINT.get());// 54.4%
        }
        world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, dropStack));
    }

    /**
     * 线程安全地判断是否应该掉落沙砾，并更新全局保底计数器。
     *
     * @param level 来自世界实例）
     * @param chance 掉落沙砾的基础概率（百分比）
     * @return true 表示掉落沙砾，false 表示不掉落（可能触发保底）
     */
    private static synchronized boolean shouldDropGravel(Level level, int chance) {
        // 保底触发：连续掉落沙砾达到 12 次后，本次强制不掉落
        if (guarantee >= 12) {
            guarantee = 0;
            return false;
        }
        // 正常随机判断
        if (level.getRandom().nextInt(100) < chance) {
            guarantee++;
            return true;
        } else {
            guarantee = 0;
            return false;
        }
    }
}