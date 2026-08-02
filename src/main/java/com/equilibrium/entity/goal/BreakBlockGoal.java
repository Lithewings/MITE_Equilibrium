package com.equilibrium.entity.goal;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class BreakBlockGoal extends Goal {
    private static final int MIN_MAX_PROGRESS = 240;
    private final Predicate<Difficulty> difficultySufficientPredicate;
    private final Mob mob;

    private int maxProgress;

    protected boolean shouldStop;

    private float offsetX, offsetZ;

    protected BlockPos breakPos = BlockPos.ZERO;

    protected BlockState breakState = Blocks.AIR.defaultBlockState();
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
        this.mob.level().destroyBlockProgress(this.mob.getId(), this.breakPos, -1);
    }






    public BreakBlockGoal(Mob mob, int maxProgress, Predicate<Difficulty> difficultySufficientPredicate) {
        this.mob = mob;
        this.maxProgress = maxProgress;
        this.difficultySufficientPredicate = difficultySufficientPredicate;

    }
    float harvestBonus = 1;

    @Override
    public boolean requiresUpdateEveryTick() {
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
            return BlockPos.ZERO;
        }
        var entityPos = entity.blockPosition();
        var facing = entity.getDirection();
        return switch (isBackward ? facing.getOpposite() : facing) {
            case EAST -> entityPos.east();
            case SOUTH -> entityPos.south();
            case WEST -> entityPos.west();
            default -> entityPos.north();
        };
    }
    public static BlockPos getFacingBlockPos(BlockPos zombiePos, BlockPos targetPos) {
        // 计算方向向量
        Vec3 zombieVec = new Vec3(zombiePos.getX(), zombiePos.getY(), zombiePos.getZ());
        Vec3 targetVec = new Vec3(targetPos.getX(), targetPos.getY(), targetPos.getZ());
        Vec3 directionVec = targetVec.subtract(zombieVec).normalize();

        // 确定僵尸面朝的方向
        Direction facingDirection;
        if (Math.abs(directionVec.x) > Math.abs(directionVec.z)) {
            facingDirection = directionVec.x > 0 ? Direction.EAST : Direction.WEST;
        } else {
            facingDirection = directionVec.z > 0 ? Direction.SOUTH : Direction.NORTH;
        }
        BlockPos blockPos = zombiePos.relative(facingDirection);
        // 返回僵尸面朝方向的方块位置
        return blockPos;
    }

    public boolean canBreakBlock(@NotNull BlockState state) {
        boolean hardBlock = state.is(ModBlockTags.HARVEST_ONE) || state.is(ModBlockTags.HARVEST_TWO)||state.is(ModBlockTags.HARVEST_THREE)||state.is(ModBlockTags.HARVEST_FOUR) ;
        //可能会存在隔着透明方块也能打到之后的方块
        return !state.isAir() && !hardBlock && !state.is(BlockTags.PLANKS);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            for (double[] findPos : FIND_NEAREST_BLOCKS) {


                //僵尸向目标前进一格的位置,不考虑y
                BlockPos pendingBreakPos = getFacingBlockPos(this.mob.blockPosition(), target.blockPosition());
                pendingBreakPos=new BlockPos( (pendingBreakPos.getX()+(int)findPos[0]),  (pendingBreakPos.getY()+(int)findPos[1]),(pendingBreakPos.getZ()+(int)findPos[2]));


                BlockState pendingBreakState = this.mob.level().getBlockState(pendingBreakPos);
                if(!canBreakBlock(pendingBreakState)) {
                    blockBreakProgressMap.put(pendingBreakPos, 0);
                    continue;
                }
                if (canBreakBlock(pendingBreakState) && this.mob.getNavigation().isDone()) {
                    this.breakPos = pendingBreakPos;
                    this.breakState = pendingBreakState;
                    if (this.mob.getMainHandItem().isCorrectToolForDrops(breakState)) {
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
    public boolean canContinueToUse() {
        if (this.mob.getTarget() == null) {
            return false;
        }
        boolean b1 = !this.shouldStop;
        boolean b2 = this.breakProgress <= this.getMaxProgress();
        boolean b3 = canBreakBlock(this.breakState);
        boolean b4 = this.breakPos.closerToCenterThan(this.mob.position(), 2);
        boolean b5 = this.mob.getCombatTracker().getCombatDuration() > 20;
        return (!this.shouldStop) && (this.breakProgress <= this.getMaxProgress() )&& (canBreakBlock(this.breakState)) &&( this.breakPos.closerToCenterThan(this.mob.position(), 5));
    }




    public static final Predicate<BlockState> IS_GRAVITY_AFFECTED = state -> state != null && (state.is(Blocks.GRAVEL));


    public static void checkBlockGravity(Level world, BlockPos pos) {
        try {
            if (!(world instanceof ServerLevel)) return;
            for (BlockPos bp : new BlockPos[]{pos, pos.above(), pos.below(), pos.east(), pos.west(), pos.south(), pos.north()}) {
                //Check the pos and its immediate pos
                BlockState state = world.getBlockState(bp);
                if (IS_GRAVITY_AFFECTED.test(state)) {
                    if (FallingBlock.isFree(world.getBlockState(bp.below())) || bp.getY() < world.getMinBuildHeight()) {
                        if (IS_GRAVITY_AFFECTED.test(state)) FallingBlockEntity.fall(world, bp, state);
                        //Recurse for further neighbor tick
                        for (BlockPos bpn : new BlockPos[]{bp.above(), bp.below(), bp.east(), bp.west(), bp.south(), bp.north()})
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
        BlockState pendingBreakState = this.mob.level().getBlockState(this.breakPos);
        if(pendingBreakState.is(Blocks.AIR)) {
            this.shouldStop = true;
        }

        if (this.offsetX * (float) ((double) this.breakPos.getX() + 0.5 - this.mob.getX()) + this.offsetZ * (float) ((double) this.breakPos.getZ() + 0.5 - this.mob.getZ()) < 0.0f) {
            this.shouldStop = true;
        }

        float breakStage = ((float) breakProgress / getMaxProgress()) * 8;
        if ((int) breakStage != this.prevBreakStage) {
            this.mob.swing(this.mob.getUsedItemHand());
            this.mob.level().destroyBlockProgress(this.mob.getId(), this.breakPos, (int) breakStage);
            this.mob.level().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, this.breakPos, Block.getId(this.breakState));
            this.prevBreakStage = (int) breakStage;
        }
        if (breakProgress >= this.getMaxProgress()) {
            this.mob.level().removeBlock(this.breakPos, false);
            blockBreakProgressMap.remove(this.breakPos);  // 破坏完成后移除该方块的位置
            checkBlockGravity(this.mob.level(), this.breakPos);
        }
    }





}
