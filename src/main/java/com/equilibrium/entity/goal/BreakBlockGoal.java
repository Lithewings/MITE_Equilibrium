package com.equilibrium.entity.goal;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.tags.ModBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class BreakBlockGoal extends Goal {
    private static final int MIN_MAX_PROGRESS = 240;
    private final Predicate<Difficulty> difficultySufficientPredicate;
    private final MobEntity mob;

    private int maxProgress;

    protected boolean shouldStop;

    private float offsetX, offsetZ;

    protected BlockPos breakPos = BlockPos.ORIGIN;

    protected BlockState breakState = Blocks.AIR.getDefaultState();
    protected int breakProgress = -1, prevBreakStage = -1;

    // 添加一个静态的 Map 来存储每个方块的破坏进度
    public final static Map<BlockPos, Integer> blockBreakProgressMap = new ConcurrentHashMap<>();





    @Override
    public void start() {
        this.shouldStop = false;
        this.offsetX = (float) ((double) this.breakPos.getX() + 0.5 - this.mob.getX());
        this.offsetZ = (float) ((double) this.breakPos.getZ() + 0.5 - this.mob.getZ());

        // 初始化或获取已有的破坏进度
        blockBreakProgressMap.putIfAbsent(this.breakPos, 0);
    }



    @Override
    public void stop() {
        super.stop();
        this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.breakPos, -1);
    }






    public BreakBlockGoal(MobEntity mob, int maxProgress, Predicate<Difficulty> difficultySufficientPredicate) {
        this.mob = mob;
        this.maxProgress = maxProgress;
        this.difficultySufficientPredicate = difficultySufficientPredicate;

    }
    float harvestBonus = 1;

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }


    protected int getMaxProgress() {
        return(int)(800*harvestBonus);
    }

    public static final double[][] FIND_NEAREST_BLOCKS = {
            {0,4,0},{0, 3, 0}, {0, 2, 0},{0,1,0},{0,0,0},{0,-1,0},{0,-2,0},{0,-3,0}};

    public static BlockPos getPosFacing(Entity entity, boolean isBackward) {
        if (entity == null) {
            OnServerInitialize.LOGGER.error("EntityHelper/getPosFacing;entity==null");
            return BlockPos.ORIGIN;
        }
        var entityPos = entity.getBlockPos();
        var facing = entity.getHorizontalFacing();
        return switch (isBackward ? facing.getOpposite() : facing) {
            case EAST -> entityPos.east();
            case SOUTH -> entityPos.south();
            case WEST -> entityPos.west();
            default -> entityPos.north();
        };
    }
    public static BlockPos getFacingBlockPos(BlockPos zombiePos, BlockPos targetPos) {
        // 计算方向向量
        Vec3d zombieVec = new Vec3d(zombiePos.getX(), zombiePos.getY(), zombiePos.getZ());
        Vec3d targetVec = new Vec3d(targetPos.getX(), targetPos.getY(), targetPos.getZ());
        Vec3d directionVec = targetVec.subtract(zombieVec).normalize();

        // 确定僵尸面朝的方向
        Direction facingDirection;
        if (Math.abs(directionVec.x) > Math.abs(directionVec.z)) {
            facingDirection = directionVec.x > 0 ? Direction.EAST : Direction.WEST;
        } else {
            facingDirection = directionVec.z > 0 ? Direction.SOUTH : Direction.NORTH;
        }
        BlockPos blockPos = zombiePos.offset(facingDirection);
        // 返回僵尸面朝方向的方块位置
        return blockPos;
    }

    public boolean canBreakBlock(@NotNull BlockState state) {
        boolean hardBlock = state.isIn(ModBlockTags.HARVEST_ONE) || state.isIn(ModBlockTags.HARVEST_TWO)||state.isIn(ModBlockTags.HARVEST_THREE)||state.isIn(ModBlockTags.HARVEST_FOUR) ;
        //可能会存在隔着透明方块也能打到之后的方块
        return !state.isAir() && !hardBlock && !state.isIn(BlockTags.PLANKS);
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            for (double[] findPos : FIND_NEAREST_BLOCKS) {


                //僵尸向目标前进一格的位置,不考虑y
                BlockPos pendingBreakPos = getFacingBlockPos(this.mob.getBlockPos(), target.getBlockPos());
                pendingBreakPos=new BlockPos( (pendingBreakPos.getX()+(int)findPos[0]),  (pendingBreakPos.getY()+(int)findPos[1]),(pendingBreakPos.getZ()+(int)findPos[2]));


                BlockState pendingBreakState = this.mob.getWorld().getBlockState(pendingBreakPos);
                if(!canBreakBlock(pendingBreakState)) {
                    blockBreakProgressMap.put(pendingBreakPos, 0);
                    continue;
                }
                if (canBreakBlock(pendingBreakState) && this.mob.getNavigation().isIdle()) {
                    this.breakPos = pendingBreakPos;
                    this.breakState = pendingBreakState;
                    if (this.mob.getMainHandStack().isSuitableFor(breakState)) {
                        harvestBonus = 0.5f;
                    }
                    return true;
                }
            }
        }
        return false;
    }
//    @Override
//    public boolean canStart() {
//        // 获取僵尸面朝方向
//        LivingEntity target = this.mob.getTarget();
//        if (target != null) {
//            for (double[] findPos : FIND_NEAREST_BLOCKS) {
//                // 确定要挖掘的方块位置
//                BlockPos pendingBreakPos = new BlockPos((int) (this.mob.getX() + findPos[0]), (int) (this.mob.getY() + findPos[1]), (int) (this.mob.getZ() + findPos[2]));
//
//                // 获取僵尸的面朝方向
//                BlockPos frontPos = getPosFacing(this.mob);
//
//                // 检查方块是否在僵尸面前
//                if (pendingBreakPos.getX() != frontPos.getX() || pendingBreakPos.getZ() != frontPos.getZ()) {
//                    continue;
//                }
//
//                // 判断这个方块是否可以被挖掘
//                BlockState pendingBreakState = this.mob.getWorld().getBlockState(pendingBreakPos);
//                if (canBreakBlock(pendingBreakState) && this.mob.getNavigation().isIdle()) {
//                    this.breakPos = pendingBreakPos;
//                    this.breakState = pendingBreakState;
//                    if (this.mob.getMainHandStack().isSuitableFor(breakState)) {
//                        harvestBonus = 0.5f;
//                    }
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

//    @Override
//    public boolean canStart() {
//        //Choose a block to break
//        LivingEntity target = this.mob.getTarget();
//        if (target != null) {
//            for (double[] findPos : FIND_NEAREST_BLOCKS) {
//                //Should not dig upward when not above target
//                if (findPos[1] == -1 && this.mob.getY() <= target.getY()) continue;
//                //Should not dig downward when not under target
//                if (findPos[1] == 2 && this.mob.getY() >= target.getY()) continue;
//                //Choose a pos to break
//                BlockPos pendingBreakPos = new BlockPos((int) (this.mob.getX() + findPos[0]), (int) (this.mob.getY() + findPos[1]), (int) (this.mob.getZ() + findPos[2]));
//                //Should not break the block behind itself
//                BlockPos backPos = getPosFacing(this.mob);
//                if (pendingBreakPos.getX() == backPos.getX() && pendingBreakPos.getZ() == backPos.getZ()) continue;
//                BlockState pendingBreakState = this.mob.getWorld().getBlockState(pendingBreakPos);
//
//                //Determine whether to start
//                if (canBreakBlock(pendingBreakState) && this.mob.getNavigation().isIdle()) {
//                    this.breakPos = pendingBreakPos;
//                    this.breakState = pendingBreakState;
//                    if(this.mob.getMainHandStack().isSuitableFor(breakState))
//                        harvestBonus=0.5f;
//                    return true;
//                }
//            }
//        }
//        return false;
//    }


    @Override
    public boolean shouldContinue() {
        if (this.mob.getTarget() == null) {
            return false;
        }
        boolean b1 = !this.shouldStop;
        boolean b2 = this.breakProgress <= this.getMaxProgress();
        boolean b3 = canBreakBlock(this.breakState);
        boolean b4 = this.breakPos.isWithinDistance(this.mob.getPos(), 2);
        boolean b5 = this.mob.getDamageTracker().getTimeSinceLastAttack() > 20;
        return (!this.shouldStop) && (this.breakProgress <= this.getMaxProgress() )&& (canBreakBlock(this.breakState)) &&( this.breakPos.isWithinDistance(this.mob.getPos(), 5));
    }




    public static final Predicate<BlockState> IS_GRAVITY_AFFECTED = state -> state != null && (state.isOf(Blocks.GRAVEL));


    public static void checkBlockGravity(World world, BlockPos pos) {
        try {
            if (!(world instanceof ServerWorld)) return;
            for (BlockPos bp : new BlockPos[]{pos, pos.up(), pos.down(), pos.east(), pos.west(), pos.south(), pos.north()}) {
                //Check the pos and its immediate pos
                BlockState state = world.getBlockState(bp);
                if (IS_GRAVITY_AFFECTED.test(state)) {
                    if (FallingBlock.canFallThrough(world.getBlockState(bp.down())) || bp.getY() < world.getBottomY()) {
                        if (IS_GRAVITY_AFFECTED.test(state)) FallingBlockEntity.spawnFromBlock(world, bp, state);
                        //Recurse for further neighbor tick
                        for (BlockPos bpn : new BlockPos[]{bp.up(), bp.down(), bp.east(), bp.west(), bp.south(), bp.north()})
                            checkBlockGravity(world, bpn);
                    }
                }
            }
        } catch (StackOverflowError error) {
            OnServerInitialize.LOGGER.error("WorldHelper/checkBlockGravity(): StackOverflowError");
        }
    }




    public int getBreakProgress() {
        return blockBreakProgressMap.getOrDefault(this.breakPos, 0);
    }


    //maxProgress = 800Override
    @Override
    public void tick() {
        // 获取当前的破坏进度
        int currentProgress = this.getBreakProgress();

        // 更新破坏进度
        blockBreakProgressMap.put(this.breakPos, currentProgress + 1);

        int breakProgress = this.getBreakProgress();
        BlockState pendingBreakState = this.mob.getWorld().getBlockState(this.breakPos);
        if(pendingBreakState.isOf(Blocks.AIR)) {
            this.shouldStop = true;
        }

        if (this.offsetX * (float) ((double) this.breakPos.getX() + 0.5 - this.mob.getX()) + this.offsetZ * (float) ((double) this.breakPos.getZ() + 0.5 - this.mob.getZ()) < 0.0f) {
            this.shouldStop = true;
        }

        float breakStage = ((float) breakProgress / getMaxProgress()) * 8;
        if ((int) breakStage != this.prevBreakStage) {
            this.mob.swingHand(this.mob.getActiveHand());
            this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.breakPos, (int) breakStage);
            this.mob.getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, this.breakPos, Block.getRawIdFromState(this.breakState));
            this.prevBreakStage = (int) breakStage;
        }
        if (breakProgress >= this.getMaxProgress()) {
            this.mob.getWorld().removeBlock(this.breakPos, false);
            blockBreakProgressMap.remove(this.breakPos);  // 破坏完成后移除该方块的位置
            checkBlockGravity(this.mob.getWorld(), this.breakPos);
        }
    }





}
