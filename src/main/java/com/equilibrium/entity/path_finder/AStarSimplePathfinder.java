package com.equilibrium.entity.path_finder;

import com.equilibrium.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.*;

/**
 * 简易版A*寻路，用于在三维方块格子里找一条从start到goal的可通行路径。
 */
public class AStarSimplePathfinder {

    // 1) 设定最大搜索范围
    private static final int MAX_RANGE = 32;

    /**
     * 寻路节点内部类
     */
    private static class Node {
        BlockPos pos;   // 当前节点所在坐标
        double gCost;   // 从起点到此节点的实际花费
        double hCost;   // 启发式预估，从此节点到目标的花费
        Node parent;    // 用于重构路径时，记录走到这里的父节点

        Node(BlockPos p) {
            this.pos = p;
            this.gCost = Double.POSITIVE_INFINITY; // 初始设得很大
            this.hCost = 0;
        }

        double fCost() {
            return gCost + hCost;
        }
    }

    /**
     * 尝试从 start 到 goal 做A*寻路。
     *
     * @param world 当前世界，用于判断方块是否可通行
     * @param start 起点坐标
     * @param goal  终点坐标
     * @return 如果找到有效路径则返回 路径上的每个方块坐标；否则返回 null
     */
    public static List<BlockPos> findPath(Level world, BlockPos start, BlockPos goal) {
        // 最小堆(优先队列)，按 fCost 从小到大排序
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::fCost));
        // 存所有访问或创建过的节点，方便查找
        Map<BlockPos, Node> allNodes = new HashMap<>();
        // 闭集，存已经确定不再优化的节点坐标
        Set<BlockPos> closedSet = new HashSet<>();

        // 起点节点
        Node startNode = new Node(start);
        startNode.gCost = 0;
        startNode.hCost = heuristic(start, goal);
        allNodes.put(start, startNode);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            // 取出 fCost 最小的节点
            Node current = openSet.poll();
            if (current == null) break;

            // 如果已经到达目标点(或足够接近)，就可以重构路径返回
            if (current.pos.equals(goal)) {
                return reconstructPath(current);
            }

            closedSet.add(current.pos);

            // 遍历该节点的相邻坐标(6个方向：上、下、南、北、东、西)
            for (BlockPos neighborPos : getNeighbors(current.pos)) {

                // --- 2) 判断是否超出最大搜索范围 ---
                // 采用曼哈顿距离判断与起点的距离
                if (heuristic(start, neighborPos) > MAX_RANGE) {
                    continue;
                }

                // 若相邻方块不可通过，则跳过
                if (!isPassable(world, neighborPos)) {
                    continue;
                }
                // 若已在闭集，跳过
                if (closedSet.contains(neighborPos)) {
                    continue;
                }

                // gCost: 到邻居节点的花费 = 当前节点的gCost + 1(或其他权值)
                double tentativeG = current.gCost + 1;

                // 取出邻居对应的Node，如果没有则创建
                Node neighborNode = allNodes.get(neighborPos);
                if (neighborNode == null) {
                    neighborNode = new Node(neighborPos);
                    allNodes.put(neighborPos, neighborNode);
                }

                // 如果这条路径更优，或者邻居还没进过openSet，就更新
                if (tentativeG < neighborNode.gCost) {
                    neighborNode.gCost = tentativeG;
                    neighborNode.hCost = heuristic(neighborPos, goal);
                    neighborNode.parent = current;

                    // 如果还不在 openSet，则加入
                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                }
            }
        }
        // openSet 为空都没找到路径，返回null
        return null;
    }

    public static boolean hasPath(Level world, BlockPos start, BlockPos goal) {
        // 快速检查：起点和终点相同
        if (start.equals(goal)) {
            return true;
        }

        // 快速检查：终点是否可通过
        if (!isPassable(world, goal)) {
            return false;
        }

        // 快速检查：是否在最大搜索范围内
        if (heuristic(start, goal) > MAX_RANGE) {
            return false;
        }

        // 最小堆(优先队列)，按 fCost 从小到大排序
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::fCost));
        // 存所有访问或创建过的节点，方便查找
        Map<BlockPos, Node> allNodes = new HashMap<>();
        // 闭集，存已经确定不再优化的节点坐标
        Set<BlockPos> closedSet = new HashSet<>();

        // 起点节点
        Node startNode = new Node(start);
        startNode.gCost = 0;
        startNode.hCost = heuristic(start, goal);
        allNodes.put(start, startNode);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            // 取出 fCost 最小的节点
            Node current = openSet.poll();
            if (current == null) break;

            // 如果已经到达目标点，返回true
            if (current.pos.equals(goal)) {
                return true;
            }

            closedSet.add(current.pos);

            // 遍历该节点的相邻坐标(6个方向：上、下、南、北、东、西)
            for (BlockPos neighborPos : getNeighbors(current.pos)) {

                // --- 2) 判断是否超出最大搜索范围 ---
                // 采用曼哈顿距离判断与起点的距离
                if (heuristic(start, neighborPos) > MAX_RANGE) {
                    continue;
                }

                // 若相邻方块不可通过，则跳过
                if (!isPassable(world, neighborPos)) {
                    continue;
                }
                // 若已在闭集，跳过
                if (closedSet.contains(neighborPos)) {
                    continue;
                }

                // gCost: 到邻居节点的花费 = 当前节点的gCost + 1
                double tentativeG = current.gCost + 1;

                // 取出邻居对应的Node，如果没有则创建
                Node neighborNode = allNodes.get(neighborPos);
                if (neighborNode == null) {
                    neighborNode = new Node(neighborPos);
                    allNodes.put(neighborPos, neighborNode);
                }

                // 如果这条路径更优，或者邻居还没进过openSet，就更新
                if (tentativeG < neighborNode.gCost) {
                    neighborNode.gCost = tentativeG;
                    neighborNode.hCost = heuristic(neighborPos, goal);
                    // 注意：这里不再设置parent

                    // 如果还不在 openSet，则加入
                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                }
            }
        }
        // openSet 为空都没找到路径，返回false
        return false;
    }

    /**
     * 启发式函数(Heuristic)，这里用三维曼哈顿距离
     */
    private static double heuristic(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX())
                + Math.abs(a.getY() - b.getY())
                + Math.abs(a.getZ() - b.getZ());
    }

    /**
     * 返回某坐标6个邻居方块(上、下、南、北、东、西)。
     */
    private static List<BlockPos> getNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>(6);
        neighbors.add(pos.above());
        neighbors.add(pos.below());
        neighbors.add(pos.north());
        neighbors.add(pos.south());
        neighbors.add(pos.east());
        neighbors.add(pos.west());
        return neighbors;
    }




    /**
     * 判断方块是否可通过。此处仅示例判断"是空气"。
     * 可根据需求改为 "方块要么是空气、要么是门" 等。
     * 非固体方块不认为是不可通行的
     */
    private static boolean isPassable(Level world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isAir())
            return true;
        if (blockState.is(BlockTags.DOORS)) {
            return blockState.getValue(BlockStateProperties.OPEN);
        }
        if (blockState.is(BlockTags.FENCE_GATES)) {
            return blockState.getValue(BlockStateProperties.OPEN);
        }
        if (!blockState.isRedstoneConductor(world, pos)) {
            if (blockState.is(Blocks.GLASS)||blockState.is(ModBlockTags.GLASS_MADE) || blockState.is(BlockTags.STAIRS) || blockState.is(BlockTags.SLABS)) {
                return false;
            } else
                return true;
        }
        return false;
    }

    /**
     * 从终点节点逐级向父节点回溯，构建整条路径(从起点 -> 终点)。
     */
    private static List<BlockPos> reconstructPath(Node endNode) {
        List<BlockPos> path = new ArrayList<>();
        Node current = endNode;
        while (current != null) {
            path.add(current.pos);
            current = current.parent;
        }
        // 回溯得到的是 终点->起点，需反转
        Collections.reverse(path);
        return path;
    }
}
