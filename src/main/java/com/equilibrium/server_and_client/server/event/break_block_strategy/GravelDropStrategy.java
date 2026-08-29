package com.equilibrium.server_and_client.server.event.break_block_strategy;

import com.equilibrium.item.Metal;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

import static com.equilibrium.util.EnchantmentUtils.getFortuneLevel;
import static com.equilibrium.util.EnchantmentUtils.getSilkTouchLevel;

public class GravelDropStrategy implements BlockDropStrategy {
    // 保底计数器：全服务器共享，线程安全
    private static int guarantee = 0;

    @Override
    public boolean canHandle(BlockState state) {
        return state.getBlock() == Blocks.GRAVEL;
    }

    @Override
    public void handleDrop(World world, PlayerEntity player, BlockPos pos, BlockState state, ItemStack tool) {
        int fortuneLevel =getFortuneLevel(world,tool);
        int silkTouchLevel = getSilkTouchLevel(world,tool);

        // 精准采集直接掉落沙砾
        if (silkTouchLevel > 0) {
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    new ItemStack(Items.GRAVEL)));
            return;
        }

        // 判断是否掉落沙砾（线程安全更新保底计数器）
        int gravelDropChance = 75 - fortuneLevel * 15;
        boolean dropGravel = shouldDropGravel(world, gravelDropChance);
        if (dropGravel) {
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    new ItemStack(Blocks.GRAVEL)));
            return;
        }

        // 掉落其他物品
        int roll = world.getRandom().nextInt(1000);
        ItemStack dropStack;
        if (roll == 0) {
            dropStack = new ItemStack(Items.REDSTONE);          // 0.1%
        } else if (roll <= 100) {
            dropStack = new ItemStack(Metal.silver_nugget);     // 10%
        } else if (roll <= 240) {
            dropStack = new ItemStack(Items.FLINT);             // 14%
        } else if (roll <= 455) {
            dropStack = new ItemStack(Metal.copper_nugget);     // 21.5%
        } else {
            dropStack = new ItemStack(Metal.FLINT);             // 54.4%
        }
        world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, dropStack));
    }

    /**
     * 线程安全地判断是否掉落沙砾，并更新保底计数器。
     */
    private static synchronized boolean shouldDropGravel(World world, int chance) {
        if (guarantee >= 12) {
            guarantee = 0;
            return false;
        }
        if (world.getRandom().nextInt(100) < chance) {
            guarantee++;
            return true;
        } else {
            guarantee = 0;
            return false;
        }
    }
}