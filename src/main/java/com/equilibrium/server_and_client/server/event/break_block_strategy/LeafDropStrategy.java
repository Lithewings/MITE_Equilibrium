package com.equilibrium.server_and_client.server.event.break_block_strategy;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

import static com.equilibrium.util.EnchantmentUtils.getFortuneLevel;
import static com.equilibrium.util.EnchantmentUtils.getSilkTouchLevel;

public class LeafDropStrategy implements BlockDropStrategy {
    @Override
    public boolean canHandle(BlockState state) {
        return state.isIn(BlockTags.LEAVES);
    }

    @Override
    public void handleDrop(World world, PlayerEntity player, BlockPos pos, BlockState state, ItemStack tool) {
        int fortuneLevel =getFortuneLevel(world,tool);
        int silkTouchLevel = getSilkTouchLevel(world,tool);


        int threshold = 100 - fortuneLevel * 30;
        if (world.getRandom().nextInt(threshold) <= 10) {
            ItemEntity drop = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    new ItemStack(Items.STICK));
            world.spawnEntity(drop);
        }
    }
}