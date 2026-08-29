package com.equilibrium.server_and_client.server.event.break_block_strategy;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.equilibrium.block.reference.BlocksHardnessList.BLOCKS_HARDNESS_HASHMAP;
import static com.equilibrium.block.reference.BlocksHardnessList.getStandardBlockName;

public class BreakBlockEvent implements PlayerBlockBreakEvents.After {
    // 饿汉式单例
    private static final BreakBlockEvent INSTANCE = new BreakBlockEvent();

    private final List<BlockDropStrategy> strategies = new ArrayList<>();

    private BreakBlockEvent() {
        BlockToItemConverter converter = new BlockToItemConverter();
        strategies.add(new LeafDropStrategy());
        strategies.add(new GravelDropStrategy());
        strategies.add(new OreDropStrategy(converter));
    }

    public static BreakBlockEvent getInstance() {
        return INSTANCE;
    }

    @Override
    public void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state,
                                @Nullable BlockEntity blockEntity) {
        if (player.isCreative() || world.isClient()) {
            return;
        }

        // 工具耐久消耗（公共逻辑）
        ItemStack tool = player.getMainHandStack();
        int hardness = BLOCKS_HARDNESS_HASHMAP.getOrDefault(getStandardBlockName(state.getBlock()), 0);
        tool.damage(hardness, player, EquipmentSlot.MAINHAND);

        // 选择合适的策略并执行掉落逻辑
        for (BlockDropStrategy strategy : strategies) {
            if (strategy.canHandle(state)) {
                strategy.handleDrop(world, player, pos, state, tool);
                break; // 一个方块只匹配一个策略
            }
        }
    }
}