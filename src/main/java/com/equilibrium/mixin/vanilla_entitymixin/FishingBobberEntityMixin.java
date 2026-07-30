package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(FishingHook.class)
public abstract class FishingBobberEntityMixin extends Projectile {


    @Unique
    private boolean isWideOpenWater(BlockPos pos) {
        Level world = this.level();

        // 1. 获取所有曼哈顿距离小于等于8的方块位置
        Set<BlockPos> positionsToCheck = new HashSet<>();

        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                positionsToCheck.add(new BlockPos(pos.getX() + dx, pos.getY(), pos.getZ() + dz));
            }
        }

        // 2. 遍历集合，检查每个位置向下8格是否都是水
        for (BlockPos checkPos : positionsToCheck) {
            // 如果任意一个位置向下8格不全是水，返回false
            if (!checkWaterDepthAtPosition(checkPos, world)) {
                return false;
            }
        }

        // 3. 所有位置都满足条件，返回true
        return true;
    }

    @Unique
    private boolean checkWaterDepthAtPosition(BlockPos checkPos, Level world) {
        // 检查从checkPos开始，向下8个方块（包括checkPos）
        for (int i = 0; i < 8; i++) {
            BlockPos depthPos = new BlockPos(checkPos.getX(), checkPos.getY() - i, checkPos.getZ());

            // 检查方块是否为水
            BlockState blockState = world.getBlockState(depthPos);
            FluidState fluidState = blockState.getFluidState();

            if (!blockState.is(Blocks.WATER) && !fluidState.is(FluidTags.WATER)) {
                //含水的水草可以pass掉这段逻辑
                return false;
            }
        }

        return true;
    }





    @Unique
    private static Map<BlockPos,Boolean> IS_WIDE_OPEN_WATER = new ConcurrentHashMap<>();;


    public FishingBobberEntityMixin(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }

    @Shadow
    private int nibble;
    @Shadow
    private int timeUntilLured;
    @Shadow
    private int timeUntilHooked;
    @Shadow
    private float fishAngle;
    @Shadow
    @Final
    private static EntityDataAccessor<Integer> DATA_HOOKED_ENTITY;
    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_BITING;
    @Shadow
    @Final
    private int lureSpeed;


    @Shadow public abstract @Nullable Player getPlayerOwner();

    @Inject(method = "catchingFish",at = @At("HEAD"))
    private void tickFishingLogic(BlockPos pos, CallbackInfo ci) {
        ServerLevel serverWorld = (ServerLevel)this.level();


       boolean isWideOpenWater = false;
       if(serverWorld.getBlockState(pos).is(Blocks.WATER)){
//          OnServerInitialize.LOGGER.info("Fishing Bobble is in Water");
           if(!IS_WIDE_OPEN_WATER.containsKey(pos)){
               //不存在时计算一次是否处于开阔水面的逻辑,下次使用时直接使用该结果
               isWideOpenWater = isWideOpenWater(pos);
               IS_WIDE_OPEN_WATER.put(pos,isWideOpenWater);
           }
           else{
               isWideOpenWater = IS_WIDE_OPEN_WATER.getOrDefault(pos,false);
           }
       }

        int i = 1;

        if (this.random.nextFloat() < 0.5F && isWideOpenWater) {
            i++;
//            if(this.getPlayerOwner() instanceof ServerPlayerEntity player)
//                player.sendMessage(Text.of("应用一次开阔水面的钓鱼逻辑"));
        }




        BlockPos blockPos = pos.above();
        if (this.random.nextFloat() < 0.5F && this.level().isRainingAt(blockPos)) {
            i++;
            if(this.level().isThundering()){
                i+=2;
            }
        }

        if (this.random.nextFloat() < 0.5F && !this.level().canSeeSky(blockPos)) {
            i--;
        }

        // === 在这里插入黄昏/黎明加速逻辑 ===
        long timeOfDay = this.level().getDayTime() % 24000L;
        boolean isDusk = timeOfDay >= 12000L && timeOfDay < 13000L;
        boolean isDawn = timeOfDay >= 23000L || timeOfDay < 1000L;
        if (this.random.nextFloat() < 0.25F &&isDusk || isDawn) {
            i=i+4;
        }



        // 检查是否为海洋生物群系
        boolean isOceanBiome =this.level().getBiome(pos).is(BiomeTags.IS_OCEAN);
        // 海洋生物群系加速逻辑（50%概率 i++）
        if (isOceanBiome && this.random.nextFloat() < 0.5F) {
            i++;
        }

        if (this.nibble > 0) {
            this.nibble--;
            if (this.nibble <= 0) {
                this.timeUntilLured = 0;
                this.timeUntilHooked = 0;
                this.getEntityData().set(DATA_BITING, false);
            }
        } else if (this.timeUntilHooked > 0) {
            this.timeUntilHooked -= i;
            if (this.timeUntilHooked > 0) {
                this.fishAngle = this.fishAngle + (float)this.random.triangle(0.0, 9.188);
                float f = this.fishAngle * (float) (Math.PI / 180.0);
                float g = Mth.sin(f);
                float h = Mth.cos(f);
                double d = this.getX() + (double)(g * (float)this.timeUntilHooked * 0.1F);
                double e = (double)((float)Mth.floor(this.getY()) + 1.0F);
                double j = this.getZ() + (double)(h * (float)this.timeUntilHooked * 0.1F);
                BlockState blockState = serverWorld.getBlockState(BlockPos.containing(d, e - 1.0, j));
                if (blockState.is(Blocks.WATER)) {
                    if (this.random.nextFloat() < 0.15F) {
                        serverWorld.sendParticles(ParticleTypes.BUBBLE, d, e - 0.1F, j, 1, (double)g, 0.1, (double)h, 0.0);
                    }

                    float k = g * 0.04F;
                    float l = h * 0.04F;
                    serverWorld.sendParticles(ParticleTypes.FISHING, d, e, j, 0, (double)l, 0.01, (double)(-k), 1.0);
                    serverWorld.sendParticles(ParticleTypes.FISHING, d, e, j, 0, (double)(-l), 0.01, (double)k, 1.0);
                }
            } else {
                this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                double m = this.getY() + 0.5;
                serverWorld.sendParticles(
                        ParticleTypes.BUBBLE, this.getX(), m, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), 0.0, (double)this.getBbWidth(), 0.2F
                );
                serverWorld.sendParticles(
                        ParticleTypes.FISHING, this.getX(), m, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), 0.0, (double)this.getBbWidth(), 0.2F
                );
                this.nibble = Mth.nextInt(this.random, 40, 60);
                this.getEntityData().set(DATA_BITING, true);
            }
        } else if (this.timeUntilLured > 0) {
            this.timeUntilLured -= i;
            float f = 0.15F;
            if (this.timeUntilLured < 20) {
                f += (float)(20 - this.timeUntilLured) * 0.05F;
            } else if (this.timeUntilLured < 40) {
                f += (float)(40 - this.timeUntilLured) * 0.02F;
            } else if (this.timeUntilLured < 60) {
                f += (float)(60 - this.timeUntilLured) * 0.01F;
            }

            if (this.random.nextFloat() < f) {
                float g = Mth.nextFloat(this.random, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
                float h = Mth.nextFloat(this.random, 25.0F, 60.0F);
                double d = this.getX() + (double)(Mth.sin(g) * h) * 0.1;
                double e = (double)((float)Mth.floor(this.getY()) + 1.0F);
                double j = this.getZ() + (double)(Mth.cos(g) * h) * 0.1;
                BlockState blockState = serverWorld.getBlockState(BlockPos.containing(d, e - 1.0, j));
                if (blockState.is(Blocks.WATER)) {
                    serverWorld.sendParticles(ParticleTypes.SPLASH, d, e, j, 2 + this.random.nextInt(2), 0.1F, 0.0, 0.1F, 0.0);
                }
            }

            if (this.timeUntilLured <= 0) {
                this.fishAngle = Mth.nextFloat(this.random, 0.0F, 360.0F);
                this.timeUntilHooked = Mth.nextInt(this.random, 20, 80);
            }
        } else {
            this.timeUntilLured = Mth.nextInt(this.random, 1000, 6000);
            this.timeUntilLured = this.timeUntilLured - this.lureSpeed;
        }
    }


    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 1))
    private Entity modifyExperienceOrb(Entity original) {
        Player player = this.getPlayerOwner();
        if (player == null) return original;
        return new ExperienceOrb(player.level(), player.getX(), player.getY() + 0.5, player.getZ() + 0.5, 0);
    }






}
