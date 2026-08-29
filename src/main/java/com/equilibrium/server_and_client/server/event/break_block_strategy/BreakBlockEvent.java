package com.equilibrium.server_and_client.server.event.break_block_strategy;


import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.equilibrium.block.reference.BlocksHardnessList.BLOCKS_HARDNESS_HASHMAP;
import static com.equilibrium.block.reference.BlocksHardnessList.getStandardBlockName;

public class BreakBlockEvent implements PlayerBlockBreakEvents.After {
    private final List<BlockDropStrategy> strategies = new ArrayList<>();

    private BreakBlockEvent() {
        BlockToItemConverter converter = new BlockToItemConverter();
        strategies.add(new LeafDropStrategy());
        strategies.add(new GravelDropStrategy());
        strategies.add(new OreDropStrategy(converter));
    }

    //饿汉单例
    private static final BreakBlockEvent INSTANCE = new BreakBlockEvent();
    public static BreakBlockEvent getInstance() {
        return INSTANCE;
    }

    @Override
    public void afterBlockBreak(Level world, Player player, BlockPos pos, BlockState state,
                                @Nullable BlockEntity blockEntity) {
        if (!player.isCreative() && !world.isClientSide()) {
            // 工具耐久消耗（公共逻辑）
            ItemStack tool = player.getMainHandItem();
            int hardness = BLOCKS_HARDNESS_HASHMAP.getOrDefault(
                    getStandardBlockName(state.getBlock()), 0);
            tool.hurtAndBreak(hardness, player, EquipmentSlot.MAINHAND);

            // 选择合适的策略并执行掉落逻辑
            for (BlockDropStrategy strategy : strategies) {
                if (strategy.canHandle(state)) {
                    strategy.handleDrop(world, player, pos, state, tool);
                    break; // 一个方块只匹配一个策略
                }
            }
        }
    }
}