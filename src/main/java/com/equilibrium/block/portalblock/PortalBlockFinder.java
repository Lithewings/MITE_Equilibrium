package com.equilibrium.block.portalblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.equilibrium.block.portalblock.PortalBlock.AXIS;

public class PortalBlockFinder {

    private static final int[][] DIRECTIONS = {
            {1, 0, 0}, {-1, 0, 0},   // 东西
            {0, 1, 0}, {0, -1, 0},   // 上下
            {0, 0, 1}, {0, 0, -1}    // 南北
    };

    /**
     * 收集与起始坐标相连的所有传送门方块（相同方块类型）。
     *
     * @param world        世界对象
     * @param start        起始传送门方块坐标
     * @param netherPortalBlock  传送门方块类型,期望为NetherPortalBlock
     * @param maxDepth     最大递归深度（步数限制），建议使用 64
     * @return 所有连通传送门方块的坐标列表（包含起始坐标）
     */
    public static List<BlockPos> collectNetherPortalBlocks(Level world, BlockPos start, NetherPortalBlock netherPortalBlock, int maxDepth) {
        Set<BlockPos> visited = new HashSet<>();
        List<BlockPos> result = new ArrayList<>();
        dfs(world, start, netherPortalBlock, visited, result, 0, maxDepth);
        return result;
    }

    private static void dfs(Level world, BlockPos pos, NetherPortalBlock netherPortalBlock,
                            Set<BlockPos> visited, List<BlockPos> result,
                            int depth, int maxDepth) {
        // 超出深度限制或已访问过则停止
        if (depth > maxDepth || visited.contains(pos)) {
            return;
        }
        // 若当前方块不是目标传送门方块，则停止
        if (!world.getBlockState(pos).is(netherPortalBlock)) {
            return;
        }

        visited.add(pos);
        result.add(pos);

        // 向六个方向继续搜索
        for (int[] dir : DIRECTIONS) {
            BlockPos next = pos.offset(dir[0], dir[1], dir[2]);
            dfs(world, next, netherPortalBlock, visited, result, depth + 1, maxDepth);
        }
    }

    /**
     * 替换所有相连的传送门方块为指定方块（例如空气）。
     *
     * @param world           世界对象
     * @param start           起始传送门方块坐标
     * @param netherPortalBlock     原传送门方块类型
     * @param modPortalBlock 替换后的方块类型
     * @param maxDepth        最大递归深度
     * @param axis      原先的下界传送门方块方向:x或z
     */
    public static void replacePortalBlocks(Level world, BlockPos start, NetherPortalBlock netherPortalBlock,
                                           PortalBlock modPortalBlock, int maxDepth, Direction.Axis axis) {

        List<BlockPos> netherPortalBlockPosList = collectNetherPortalBlocks(world, start, netherPortalBlock, maxDepth);
        for (BlockPos pos : netherPortalBlockPosList) {
            world.removeBlock(pos,false);
            switch (axis){
                case X -> world.setBlockAndUpdate(pos, modPortalBlock.defaultBlockState().setValue(AXIS,Direction.Axis.X));
                case Z -> world.setBlockAndUpdate(pos, modPortalBlock.defaultBlockState().setValue(AXIS,Direction.Axis.Z));
            }

        }
    }
}