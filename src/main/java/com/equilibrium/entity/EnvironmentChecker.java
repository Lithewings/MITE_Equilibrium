package com.equilibrium.entity;

import com.equilibrium.entity.path_finder.AStarCanGoToAndReturn;
import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_ADVANCE_ANIMAL_AI;
import static com.equilibrium.entity.EnvironmentChecker.Navigation.canNavigateToSurfaceGrass;
import static com.equilibrium.entity.EnvironmentChecker.Navigation.canNavigateToSurfaceWater;


public class EnvironmentChecker {

    private final PathfinderMob entity;

    private final int environmentCheckInterValTime;

    private int checkEnvironmentIsSuitableTime;

    private int grassBlockLackTimes;

    private int grassWaterLackTimes;

    private int grassLackTimes;

    private int waterLackTimes;

    private boolean lastIllnessState = false;


    private boolean shouldWaitPlayers = true;


    public EnvironmentChecker(PathfinderMob entity, int environmentCheckInterValTime) {
        this.entity = entity;
        this.environmentCheckInterValTime = environmentCheckInterValTime;
        this.checkEnvironmentIsSuitableTime = this.environmentCheckInterValTime;

    }


    private int tickCount = 100;

    public void tickTask() {

        this.tickCount--;
        if (this.entity.level() instanceof ServerLevel serverWorld) {
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

    public void interactTask(Player player) {
        if (player.isShiftKeyDown()) {
            this.checkBodyStats(player);
        }
    }


    private void updateSkin(ServerLevel world) {
        for (ServerPlayer player : world.players()) {
            // 检查玩家是否在同一个维度且能看到实体
            if (player.level().dimension() == this.entity.level().dimension() &&
                    player.hasLineOfSight(this.entity)) {
                ServerPlayNetworking.send(
                        player,
                        new S2CIllnessTextureBooleanPacket.IllnessAppearancePayload(this.entity.getId(), isIllness())
                );
            }
        }
    }

    private void updateSkinWithoutLimit(ServerLevel world) {
        //Init时只有生病时才发包,或周期发包
        if (this.isIllness()) {
            for (ServerPlayer player : world.players()) {
                ServerPlayNetworking.send(
                        player,
                        new S2CIllnessTextureBooleanPacket.IllnessAppearancePayload(this.entity.getId(), isIllness())
                );
            }
        }
    }

    public void renderIllnessSkinIfNeeded(ServerLevel world) {
        boolean currentIllness = isIllness();
        if (currentIllness != this.lastIllnessState) {
            // 状态改变了，发送网络包
            this.updateSkin(world);
            lastIllnessState = currentIllness;
        }
    }

    private void ifShouldAlwaysBaby() {
        if (isIllness() && this.entity instanceof AgeableMob passiveEntity && passiveEntity.isBaby()) {
            passiveEntity.setAge(passiveEntity.getAge() - 1);
        }
    }


    public void readCustomDataFromNbt(CompoundTag nbt) {
        this.checkEnvironmentIsSuitableTime = nbt.getInt("checkEnvironmentIsSuitableTime");
        this.grassBlockLackTimes = nbt.getInt("grassBlockLackTimes");
        this.grassWaterLackTimes = nbt.getInt("grassWaterLackTimes");
        this.grassLackTimes = nbt.getInt("grassLackTimes");
        this.waterLackTimes = nbt.getInt("waterLackTimes");
    }

    public void writeCustomDataToNbt(CompoundTag nbt) {
        nbt.putInt("checkEnvironmentIsSuitableTime", this.checkEnvironmentIsSuitableTime);
        nbt.putInt("grassBlockLackTimes", this.grassBlockLackTimes);
        nbt.putInt("grassWaterLackTimes", this.grassWaterLackTimes);
        nbt.putInt("grassLackTimes", this.grassLackTimes);
        nbt.putInt("waterLackTimes", this.waterLackTimes);

    }


    public boolean isIllness() {
        return this.grassBlockLackTimes > 3 || this.grassWaterLackTimes > 3 || this.grassLackTimes > 3 || this.waterLackTimes > 3;
    }

    private void checkBodyStats(Player player) {
        player.sendSystemMessage(Component.translationArg(this.entity.getName()));
        player.sendSystemMessage(Component.nullToEmpty("Baby: " + this.entity.isBaby()));
        player.sendSystemMessage(Component.nullToEmpty("Lack of Water: " + this.waterLackTimes + " times"));
        player.sendSystemMessage(Component.nullToEmpty("Lack of Grass: " + this.grassLackTimes + " times"));
        player.sendSystemMessage(Component.nullToEmpty("Lack of GrassBlock: " + this.grassBlockLackTimes + " times"));
        player.sendSystemMessage(Component.nullToEmpty("Illness: " + isIllness()));
    }

    public void initIfNeeded(ServerLevel serverWorld) {
        //初始状态,直到服务器有人,则发包
        //后续shouldWaitPlayers=false,不再init
        if (shouldWaitPlayers && !serverWorld.players().isEmpty()) {
            updateSkinWithoutLimit(serverWorld);
            shouldWaitPlayers = false;
        }
    }


    public void checkEnvironment() {
        //高级动物AI:检查环境方面
        if(this.entity.level() instanceof ServerLevel serverWorld)
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

        BlockState blockState = this.entity.level().getBlockState(this.entity.blockPosition().below());
        return blockState.is(Blocks.GRASS_BLOCK);
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

        public static boolean canNavigateToSurfaceWater(PathfinderMob entity) {

            Level world = entity.level();

            // 以生物为中心，搜索16格范围内的方块
            int searchRadius = 16;
            int x = entity.blockPosition().getX();
            int y = entity.blockPosition().getY();
            int z = entity.blockPosition().getZ();


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
                                blockState.getFluidState().getType() == Fluids.WATER ||
                                blockState.getFluidState().getType() == Fluids.FLOWING_WATER;

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
                    List<BlockPos> list = AStarCanGoToAndReturn.findSimplePath(entity.level(), entity.blockPosition(), pos.above());
                    if (list != null) {
//                    drawPath(list, world);
                        //导航到水附近
                        entity.getNavigation().moveTo(pos.getX(), pos.getY() + 1, pos.getZ(), 1);
                        return true;
                    }

                }
            }
            return false;
        }

        public static boolean canNavigateToSurfaceGrass(PathfinderMob entity) {

            Level world = entity.level();

            // 以生物为中心，搜索16格范围内的方块
            int searchRadius = 16;
            int x = entity.blockPosition().getX();
            int y = entity.blockPosition().getY();
            int z = entity.blockPosition().getZ();


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

                        if (blockState.is(Blocks.SHORT_GRASS) || blockState.is(Blocks.TALL_GRASS)) {
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
                    List<BlockPos> list = AStarCanGoToAndReturn.findSimplePath(entity.level(), entity.blockPosition(), pos);
                    if (list != null) {
                        //导航到草附近
//                    drawPath(list, world);
                        entity.getNavigation().moveTo(pos.getX(), pos.getY() + 1, pos.getZ(), 1);
                        return true;
                    }

                }
            }
            return false;
        }

        public static void drawPath(List<BlockPos> list, Level world) {
            for (BlockPos blockPos : list) {
                if (blockPos == list.getFirst()) {
                    world.setBlockAndUpdate(blockPos, Blocks.RED_WOOL.defaultBlockState());
                } else if (blockPos == list.getLast()) {
                    world.setBlockAndUpdate(blockPos, Blocks.GREEN_WOOL.defaultBlockState());
                } else
                    world.setBlockAndUpdate(blockPos, Blocks.WHITE_WOOL.defaultBlockState());

                new Thread(() -> {
                    try {
                        Thread.sleep(3000); // 10秒 = 10000毫秒
                        // 延迟结束后，在服务器主线程执行方块操作
                        world.getServer().execute(() -> {
                            world.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }}


}
