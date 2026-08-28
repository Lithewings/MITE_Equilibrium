package com.equilibrium.server_and_client.server.event.break_block_strategy;

import com.equilibrium.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class OreDropStrategy implements BlockDropStrategy {
    private final BlockToItemConverter blockToItemConverter;

    public OreDropStrategy(BlockToItemConverter converter) {
        this.blockToItemConverter = converter;
    }

    @Override
    public boolean canHandle(BlockState state) {
        return state.is(ModBlockTags.ORE);
    }

    @Override
    public void handleDrop(Level world, Player player, BlockPos pos, BlockState state, ItemStack tool) {
        int fortuneLevel = EnchantmentUtils.getFortuneLevel(world, tool);
        int silkTouchLevel = EnchantmentUtils.getSilkTouchLevel(world, tool);


        // 精准采集：掉落矿石方块本身
        if (silkTouchLevel > 0) {
            Item oreItem = state.getBlock().asItem();
            world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    new ItemStack(oreItem)));
            return;
        }

        // 获取对应的掉落物
        Item dropItem = blockToItemConverter.convertBlockToItem(state.getBlock());
        int dropTimes = 1;
        // 特殊矿物掉落次数范围
        if (dropItem == Items.LAPIS_LAZULI || dropItem == Items.REDSTONE || dropItem == Items.GOLD_NUGGET) {
            dropTimes = 4 + world.getRandom().nextInt(4); // 4~7次
        }

        // 时运翻倍判定
        if (world.getRandom().nextInt(10) >= (10 - fortuneLevel)) {
            dropTimes *= 2;
        }

        for (int i = 0; i < dropTimes; i++) {
            world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    new ItemStack(dropItem)));
        }
    }
}