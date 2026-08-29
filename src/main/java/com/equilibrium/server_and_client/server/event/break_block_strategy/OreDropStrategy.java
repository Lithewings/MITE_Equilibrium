package com.equilibrium.server_and_client.server.event.break_block_strategy;

import com.equilibrium.tags.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.equilibrium.util.EnchantmentUtils.getFortuneLevel;
import static com.equilibrium.util.EnchantmentUtils.getSilkTouchLevel;

public class OreDropStrategy implements BlockDropStrategy {
    private final BlockToItemConverter blockToItemConverter;

    public OreDropStrategy(BlockToItemConverter converter) {
        this.blockToItemConverter = converter;
    }

    @Override
    public boolean canHandle(BlockState state) {
        return state.isIn(ModBlockTags.ORE);
    }

    @Override
    public void handleDrop(World world, PlayerEntity player, BlockPos pos, BlockState state, ItemStack tool) {
        int fortuneLevel =getFortuneLevel(world,tool);
        int silkTouchLevel = getSilkTouchLevel(world,tool);

        // 精准采集掉落方块本身
        if (silkTouchLevel > 0) {
            Item oreItem = state.getBlock().asItem();
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    new ItemStack(oreItem)));
            return;
        }

        Item dropItem = blockToItemConverter.convertBlockToItem(state.getBlock());
        int dropTimes = 1;
        if (dropItem == Items.LAPIS_LAZULI || dropItem == Items.REDSTONE || dropItem == Items.GOLD_NUGGET) {
            dropTimes = 4 + world.getRandom().nextInt(4); // 4~7次
        }

        // 时运翻倍判定
        if (world.getRandom().nextInt(10) >= (10 - fortuneLevel)) {
            dropTimes *= 2;
        }

        for (int i = 0; i < dropTimes; i++) {
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    new ItemStack(dropItem)));
        }
    }
}