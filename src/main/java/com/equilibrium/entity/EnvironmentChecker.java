package com.equilibrium.entity;

import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import com.equilibrium.entity.path_finder.AStarCanGoToAndReturn;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.*;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_ADVANCE_ANIMAL_AI;
import static com.equilibrium.entity.EnvironmentChecker.Navigation.canNavigateToSurfaceGrass;
import static com.equilibrium.entity.EnvironmentChecker.Navigation.canNavigateToSurfaceWater;


public class EnvironmentChecker {

    private final PathAwareEntity entity;

    private final int environmentCheckInterValTime;

    private int checkEnvironmentIsSuitableTime;

    private int grassBlockLackTimes;

    private int grassWaterLackTimes;

    private int grassLackTimes;

    private int waterLackTimes;

    private boolean lastIllnessState = false;


    private boolean shouldWaitPlayers = true;


    public EnvironmentChecker(PathAwareEntity entity, int environmentCheckInterValTime) {
        this.entity = entity;
        this.environmentCheckInterValTime = environmentCheckInterValTime;
        this.checkEnvironmentIsSuitableTime = this.environmentCheckInterValTime;

    }


    private int tickCount = 100;

    public void tickTask() {

        this.tickCount--;
        if (this.entity.getWorld() instanceof ServerWorld serverWorld) {
            this.initIfNeeded(serverWorld);
            this.renderIllnessSkinIfNeeded(serverWorld);
            this.ifShouldAlwaysBaby();
            if (tickCount <= 0) {
                updateSkinWithoutLimit(serverWorld);
                tickCount = 100;
            }
        }

        this.checkEnvironment();
    }

    public void interactTask(PlayerEntity player) {
        if (player.isSneaking()) {
            this.checkBodyStats(player);
        }
    }


    private void updateSkin(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            // 检查玩家是否在同一个维度且能看到实体
            if (player.getWorld().getRegistryKey() == this.entity.getWorld().getRegistryKey() &&
                    player.canSee(this.entity)) {
                ServerPlayNetworking.send(
                        player,
                        new S2CIllnessTextureBooleanPacket.IllnessAppearancePayload(this.entity.getId(), isIllness())
                );
            }
        }
    }

    private void updateSkinWithoutLimit(ServerWorld world) {
        //Init时只有生病时才发包,或周期发包
        if (this.isIllness()) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                ServerPlayNetworking.send(
                        player,
                        new S2CIllnessTextureBooleanPacket.IllnessAppearancePayload(this.entity.getId(), isIllness())
                );
            }
        }
    }

    public void renderIllnessSkinIfNeeded(ServerWorld world) {
        boolean currentIllness = isIllness();
        if (currentIllness != this.lastIllnessState) {
            // 状态改变了，发送网络包
            this.updateSkin(world);
            lastIllnessState = currentIllness;
        }
    }

    private void ifShouldAlwaysBaby() {
        if (isIllness() && this.entity instanceof PassiveEntity passiveEntity && passiveEntity.isBaby()) {
            passiveEntity.setBreedingAge(passiveEntity.getBreedingAge() - 1);
        }
    }


    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.checkEnvironmentIsSuitableTime = nbt.getInt("checkEnvironmentIsSuitableTime");
        this.grassBlockLackTimes = nbt.getInt("grassBlockLackTimes");
        this.grassWaterLackTimes = nbt.getInt("grassWaterLackTimes");
        this.grassLackTimes = nbt.getInt("grassLackTimes");
        this.waterLackTimes = nbt.getInt("waterLackTimes");
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("checkEnvironmentIsSuitableTime", this.checkEnvironmentIsSuitableTime);
        nbt.putInt("grassBlockLackTimes", this.grassBlockLackTimes);
        nbt.putInt("grassWaterLackTimes", this.grassWaterLackTimes);
        nbt.putInt("grassLackTimes", this.grassLackTimes);
        nbt.putInt("waterLackTimes", this.waterLackTimes);

    }


    public boolean isIllness() {
        return this.grassBlockLackTimes > 3 || this.grassWaterLackTimes > 3 || this.grassLackTimes > 3 || this.waterLackTimes > 3;
    }

    private void checkBodyStats(PlayerEntity player) {
        player.sendMessage(Text.of(this.entity.getName()));
        player.sendMessage(Text.of("Baby: " + this.entity.isBaby()));
        player.sendMessage(Text.of("Lack of Water: " + this.waterLackTimes + " times"));
        player.sendMessage(Text.of("Lack of Grass: " + this.grassLackTimes + " times"));
        player.sendMessage(Text.of("Lack of GrassBlock: " + this.grassBlockLackTimes + " times"));
        player.sendMessage(Text.of("Illness: " + isIllness()));
    }

    public void initIfNeeded(ServerWorld serverWorld) {
        //初始状态,直到服务器有人,则发包
        //后续shouldWaitPlayers=false,不再init
        if (shouldWaitPlayers && !serverWorld.getPlayers().isEmpty()) {
            updateSkinWithoutLimit(serverWorld);
            shouldWaitPlayers = false;
        }
    }


    public void checkEnvironment() {
        //高级动物AI:检查环境方面
        if(this.entity.getWorld() instanceof ServerWorld serverWorld)
            if(!getGameBooleanRuleFromServer(ENABLE_ADVANCE_ANIMAL_AI,serverWorld.getServer()))
                return;

        if (this.entity.isBaby()||this.isIllness())
            this.checkEnvironmentIsSuitableTime = this.checkEnvironmentIsSuitableTime - 4;
        else
            this.checkEnvironmentIsSuitableTime--;
        if (checkEnvironmentIsSuitableTime > 0) {
            return;
        }


        //检查环境
        if (!checkFootBlockIsGrassBlock()) {
            this.grassBlockLackTimes++;
        } else
            this.grassBlockLackTimes = 0;

        if (!checkWater()) {
            this.waterLackTimes++;
        } else
            this.waterLackTimes = 0;

        if (!checkGrass()) {
            this.grassLackTimes++;
        } else
            this.grassLackTimes = 0;


        this.checkEnvironmentIsSuitableTime = this.environmentCheckInterValTime;

    }

    private boolean checkFootBlockIsGrassBlock() {

        BlockState blockState = this.entity.getWorld().getBlockState(this.entity.getBlockPos().down());
        return blockState.isOf(Blocks.GRASS_BLOCK);
    }


    private boolean checkWater() {
        return canNavigateToSurfaceWater(this.entity);
    }

    private boolean checkGrass() {
        return canNavigateToSurfaceGrass(this.entity);
    }


    public static class Navigation{    // 计算距离平方的辅助方法
        public static double getSquaredDistance(BlockPos pos, double x, double y, double z) {
            double dx = pos.getX() + 0.5 - x;
            double dy = pos.getY() + 0.5 - y;
            double dz = pos.getZ() + 0.5 - z;
            return dx * dx + dy * dy + dz * dz;
        }

        public static boolean canNavigateToSurfaceWater(PathAwareEntity entity) {

            World world = entity.getWorld();

            // 以生物为中心，搜索16格范围内的方块
            int searchRadius = 16;
            int x = entity.getBlockPos().getX();
            int y = entity.getBlockPos().getY();
            int z = entity.getBlockPos().getZ();


            ArrayList<BlockPos> posArrayList = new ArrayList<>();

            // 从左上角到右下角顺序搜索
            for (int dx = -searchRadius; dx <= searchRadius; dx++) {
                for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                    for (int dy = -4; dy <= 4; dy++) {
                        // 计算当前搜索位置的世界坐标
                        int worldX = x + dx;
                        int worldY = y + dy;
                        int worldZ = z + dz;

                        // 获取方块
                        BlockPos pos = new BlockPos(worldX, worldY, worldZ);
                        BlockState blockState = world.getBlockState(pos);

                        boolean isWater = blockState.getBlock() == Blocks.WATER ||
                                blockState.getFluidState().getFluid() == Fluids.WATER ||
                                blockState.getFluidState().getFluid() == Fluids.FLOWING_WATER;

                        if (isWater) {
                            posArrayList.add(pos);
                        }
                    }

                }
            }

            if (!posArrayList.isEmpty()) {

                posArrayList.sort((pos1, pos2) -> {
                    double dist1 = getSquaredDistance(pos1, x, y, z);
                    double dist2 = getSquaredDistance(pos2, x, y, z);
                    return Double.compare(dist1, dist2);
                });


                for (BlockPos pos : posArrayList) {
                    //找到通往水面之上的路径
                    List<BlockPos> list = AStarCanGoToAndReturn.findSimplePath(entity.getWorld(), entity.getBlockPos(), pos.up());
                    if (list != null) {
//                    drawPath(list, world);
                        //导航到水附近
                        entity.getNavigation().startMovingTo(pos.getX(), pos.getY() + 1, pos.getZ(), 1);
                        return true;
                    }

                }
            }
            return false;
        }

        public static boolean canNavigateToSurfaceGrass(PathAwareEntity entity) {

            World world = entity.getWorld();

            // 以生物为中心，搜索16格范围内的方块
            int searchRadius = 16;
            int x = entity.getBlockPos().getX();
            int y = entity.getBlockPos().getY();
            int z = entity.getBlockPos().getZ();


            ArrayList<BlockPos> posArrayList = new ArrayList<>();

            // 从左上角到右下角顺序搜索
            for (int dx = -searchRadius; dx <= searchRadius; dx++) {
                for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                    for (int dy = -4; dy <= 4; dy++) {
                        // 计算当前搜索位置的世界坐标
                        int worldX = x + dx;
                        int worldY = y + dy;
                        int worldZ = z + dz;

                        // 获取方块
                        BlockPos pos = new BlockPos(worldX, worldY, worldZ);
                        BlockState blockState = world.getBlockState(pos);

                        if (blockState.isOf(Blocks.SHORT_GRASS) || blockState.isOf(Blocks.TALL_GRASS)) {
                            posArrayList.add(pos);
                        }
                    }

                }
            }

            if (!posArrayList.isEmpty()) {
                posArrayList.sort((pos1, pos2) -> {
                    double dist1 = getSquaredDistance(pos1, x, y, z);
                    double dist2 = getSquaredDistance(pos2, x, y, z);
                    return Double.compare(dist1, dist2);
                });
                for (BlockPos pos : posArrayList) {
                    //找到通往草的路径
                    List<BlockPos> list = AStarCanGoToAndReturn.findSimplePath(entity.getWorld(), entity.getBlockPos(), pos);
                    if (list != null) {
                        //导航到草附近
//                    drawPath(list, world);
                        entity.getNavigation().startMovingTo(pos.getX(), pos.getY() + 1, pos.getZ(), 1);
                        return true;
                    }

                }
            }
            return false;
        }

        public static void drawPath(List<BlockPos> list, World world) {
            for (BlockPos blockPos : list) {
                if (blockPos == list.getFirst()) {
                    world.setBlockState(blockPos, Blocks.RED_WOOL.getDefaultState());
                } else if (blockPos == list.getLast()) {
                    world.setBlockState(blockPos, Blocks.GREEN_WOOL.getDefaultState());
                } else
                    world.setBlockState(blockPos, Blocks.WHITE_WOOL.getDefaultState());

                new Thread(() -> {
                    try {
                        Thread.sleep(3000); // 10秒 = 10000毫秒
                        // 延迟结束后，在服务器主线程执行方块操作
                        world.getServer().execute(() -> {
                            world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }}


}
