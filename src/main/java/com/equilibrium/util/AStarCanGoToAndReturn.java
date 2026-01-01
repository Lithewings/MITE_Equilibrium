package com.equilibrium.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class AStarCanGoToAndReturn {
    // 最大搜索范围
    private static final int MAX_RANGE = 32;

    /**
     * 简化版寻路算法，只考虑最基本的情况
     */
    public static List<BlockPos> findSimplePath(World world, BlockPos start, BlockPos goal) {
        // 优先队列，按总代价排序
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::fCost));
        Map<BlockPos, Node> allNodes = new HashMap<>();
        Set<BlockPos> closedSet = new HashSet<>();

        // 起点
        Node startNode = new Node(start);
        startNode.gCost = 0;
        startNode.hCost = heuristic(start, goal);
        allNodes.put(start, startNode);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current == null) break;

            // 到达目标
            if (current.pos.equals(goal)) {
                return reconstructPath(current);
            }

            closedSet.add(current.pos);



            // 获取简单邻居（只有6个方向）
            for (BlockPos neighborPos : getSimpleNeighbors(current.pos)) {
                // 超出范围跳过
                if (heuristic(start, neighborPos) > MAX_RANGE) {
                    continue;
                }

                // 是否可以移动到邻居

                int dy = current.pos.getY() - neighborPos.getY();

                // 水平上下移动
                if (dy == 0 ||dy == 1||dy==-1) {
                    if(!canStandSimply(world, current.pos) || !isPassable(world,neighborPos))
                        continue;
                }
                else
                    continue;

                // 已在闭集跳过
                if (closedSet.contains(neighborPos)) {
                    continue;
                }

                // 移动代价：水平=1，上移=2，下移=1
                double moveCost = 1.0;
                if (neighborPos.getY() > current.pos.getY()) {
                    moveCost = 2.0; // 跳跃代价更高
                }

                double tentativeG = current.gCost + moveCost;

                Node neighborNode = allNodes.get(neighborPos);
                if (neighborNode == null) {
                    neighborNode = new Node(neighborPos);
                    allNodes.put(neighborPos, neighborNode);
                }

                // 找到更优路径
                if (tentativeG < neighborNode.gCost) {
                    neighborNode.gCost = tentativeG;
                    neighborNode.hCost = heuristic(neighborPos, goal);
                    neighborNode.parent = current;

                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                }
            }
        }

        return null; // 没找到路径
    }

    /**
     * 简单邻居：只有6个基本方向
     */
    private static List<BlockPos> getSimpleNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>(6);

        // 水平四个方向
        neighbors.add(pos.north());
        neighbors.add(pos.south());
        neighbors.add(pos.east());
        neighbors.add(pos.west());

        // 上方向
        neighbors.add(pos.up().north());
        neighbors.add(pos.up().south());
        neighbors.add(pos.up().east());
        neighbors.add(pos.up().west());

        //下方向
        neighbors.add(pos.down().north());
        neighbors.add(pos.down().south());
        neighbors.add(pos.down().east());
        neighbors.add(pos.down().west());


        return neighbors;
    }



    /**
     * 简化版站立检查
     */
    private static boolean canStandSimply(World world, BlockPos pos) {
        // 检查脚下方块是否坚固
        BlockPos groundPos = pos.down();
        if (world.getBlockState(groundPos).isAir()) {
            return false;
        }

        return true;
    }

    /**
     * 方块是否可通过（空气或可穿越）
     */
    private static boolean isPassable(World world, BlockPos pos) {

        BlockState blockState = world.getBlockState(pos);
        BlockState blockStateUp = world.getBlockState(pos.up());
        BlockState blockStateDown = world.getBlockState(pos.down());
        //不能踩在1.5格的栅栏上,同时必须满足1*2的大小空间可以通过
        if (blockState.canPathfindThrough(NavigationType.AIR)
                && blockStateUp.canPathfindThrough(NavigationType.AIR)
                && !blockStateDown.isIn(BlockTags.WALLS)
                && !blockStateDown.isIn(BlockTags.FENCES)
                && !blockStateDown.isIn(BlockTags.FENCE_GATES)

        ){
            return true;
        }
        return false;
    }

    /**
     * 启发函数（曼哈顿距离）
     */
    private static double heuristic(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) +
                Math.abs(a.getY() - b.getY()) +
                Math.abs(a.getZ() - b.getZ());
    }

    /**
     * 重构路径
     */
    private static List<BlockPos> reconstructPath(Node goalNode) {
        List<BlockPos> path = new ArrayList<>();
        Node current = goalNode;

        while (current != null) {
            path.add(0, current.pos);
            current = current.parent;
        }

        return path;
    }

    /**
     * 节点类
     */
    private static class Node {
        BlockPos pos;
        double gCost = Double.POSITIVE_INFINITY;
        double hCost = 0;
        Node parent = null;

        Node(BlockPos pos) {
            this.pos = pos;
        }

        double fCost() {
            return gCost + hCost;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return pos.equals(node.pos);
        }

        @Override
        public int hashCode() {
            return pos.hashCode();
        }
    }

}
