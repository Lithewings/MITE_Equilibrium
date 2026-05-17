package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends ProjectileEntity {


    @Unique
    private boolean isWideOpenWater(BlockPos pos) {
        World world = this.getWorld();

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
    private boolean checkWaterDepthAtPosition(BlockPos checkPos, World world) {
        // 检查从checkPos开始，向下8个方块（包括checkPos）
        for (int i = 0; i < 8; i++) {
            BlockPos depthPos = new BlockPos(checkPos.getX(), checkPos.getY() - i, checkPos.getZ());

            // 检查方块是否为水
            BlockState blockState = world.getBlockState(depthPos);
            FluidState fluidState = blockState.getFluidState();

            if (!blockState.isOf(Blocks.WATER) && !fluidState.isIn(FluidTags.WATER)) {
                //含水的水草可以pass掉这段逻辑
                return false;
            }
        }

        return true;
    }





    @Unique
    private static Map<BlockPos,Boolean> IS_WIDE_OPEN_WATER = new ConcurrentHashMap<>();;


    public FishingBobberEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    private int hookCountdown;
    @Shadow
    private int waitCountdown;
    @Shadow
    private int fishTravelCountdown;
    @Shadow
    private float fishAngle;
    @Shadow
    @Final
    private static TrackedData<Integer> HOOK_ENTITY_ID ;
    @Shadow
    @Final
    private static TrackedData<Boolean> CAUGHT_FISH;
    @Shadow
    @Final
    private int waitTimeReductionTicks;


    @Shadow public abstract @Nullable PlayerEntity getPlayerOwner();

    @Inject(method = "tickFishingLogic",at = @At("HEAD"))
    private void tickFishingLogic(BlockPos pos, CallbackInfo ci) {
        ServerWorld serverWorld = (ServerWorld)this.getWorld();


       boolean isWideOpenWater = false;
       if(serverWorld.getBlockState(pos).isOf(Blocks.WATER)){
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




        BlockPos blockPos = pos.up();
        if (this.random.nextFloat() < 0.5F && this.getWorld().hasRain(blockPos)) {
            i++;
            if(this.getWorld().isThundering()){
                i+=2;
            }
        }

        if (this.random.nextFloat() < 0.5F && !this.getWorld().isSkyVisible(blockPos)) {
            i--;
        }

        // === 在这里插入黄昏/黎明加速逻辑 ===
        long timeOfDay = this.getWorld().getTimeOfDay() % 24000L;
        boolean isDusk = timeOfDay >= 12000L && timeOfDay < 13000L;
        boolean isDawn = timeOfDay >= 23000L || timeOfDay < 1000L;
        if (this.random.nextFloat() < 0.25F &&isDusk || isDawn) {
            i=i+4;
        }



        // 检查是否为海洋生物群系
        boolean isOceanBiome =this.getWorld().getBiome(pos).isIn(BiomeTags.IS_OCEAN);
        // 海洋生物群系加速逻辑（50%概率 i++）
        if (isOceanBiome && this.random.nextFloat() < 0.5F) {
            i++;
        }

        if (this.hookCountdown > 0) {
            this.hookCountdown--;
            if (this.hookCountdown <= 0) {
                this.waitCountdown = 0;
                this.fishTravelCountdown = 0;
                this.getDataTracker().set(CAUGHT_FISH, false);
            }
        } else if (this.fishTravelCountdown > 0) {
            this.fishTravelCountdown -= i;
            if (this.fishTravelCountdown > 0) {
                this.fishAngle = this.fishAngle + (float)this.random.nextTriangular(0.0, 9.188);
                float f = this.fishAngle * (float) (Math.PI / 180.0);
                float g = MathHelper.sin(f);
                float h = MathHelper.cos(f);
                double d = this.getX() + (double)(g * (float)this.fishTravelCountdown * 0.1F);
                double e = (double)((float)MathHelper.floor(this.getY()) + 1.0F);
                double j = this.getZ() + (double)(h * (float)this.fishTravelCountdown * 0.1F);
                BlockState blockState = serverWorld.getBlockState(BlockPos.ofFloored(d, e - 1.0, j));
                if (blockState.isOf(Blocks.WATER)) {
                    if (this.random.nextFloat() < 0.15F) {
                        serverWorld.spawnParticles(ParticleTypes.BUBBLE, d, e - 0.1F, j, 1, (double)g, 0.1, (double)h, 0.0);
                    }

                    float k = g * 0.04F;
                    float l = h * 0.04F;
                    serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, (double)l, 0.01, (double)(-k), 1.0);
                    serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, (double)(-l), 0.01, (double)k, 1.0);
                }
            } else {
                this.playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                double m = this.getY() + 0.5;
                serverWorld.spawnParticles(
                        ParticleTypes.BUBBLE, this.getX(), m, this.getZ(), (int)(1.0F + this.getWidth() * 20.0F), (double)this.getWidth(), 0.0, (double)this.getWidth(), 0.2F
                );
                serverWorld.spawnParticles(
                        ParticleTypes.FISHING, this.getX(), m, this.getZ(), (int)(1.0F + this.getWidth() * 20.0F), (double)this.getWidth(), 0.0, (double)this.getWidth(), 0.2F
                );
                this.hookCountdown = MathHelper.nextInt(this.random, 40, 60);
                this.getDataTracker().set(CAUGHT_FISH, true);
            }
        } else if (this.waitCountdown > 0) {
            this.waitCountdown -= i;
            float f = 0.15F;
            if (this.waitCountdown < 20) {
                f += (float)(20 - this.waitCountdown) * 0.05F;
            } else if (this.waitCountdown < 40) {
                f += (float)(40 - this.waitCountdown) * 0.02F;
            } else if (this.waitCountdown < 60) {
                f += (float)(60 - this.waitCountdown) * 0.01F;
            }

            if (this.random.nextFloat() < f) {
                float g = MathHelper.nextFloat(this.random, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
                float h = MathHelper.nextFloat(this.random, 25.0F, 60.0F);
                double d = this.getX() + (double)(MathHelper.sin(g) * h) * 0.1;
                double e = (double)((float)MathHelper.floor(this.getY()) + 1.0F);
                double j = this.getZ() + (double)(MathHelper.cos(g) * h) * 0.1;
                BlockState blockState = serverWorld.getBlockState(BlockPos.ofFloored(d, e - 1.0, j));
                if (blockState.isOf(Blocks.WATER)) {
                    serverWorld.spawnParticles(ParticleTypes.SPLASH, d, e, j, 2 + this.random.nextInt(2), 0.1F, 0.0, 0.1F, 0.0);
                }
            }

            if (this.waitCountdown <= 0) {
                this.fishAngle = MathHelper.nextFloat(this.random, 0.0F, 360.0F);
                this.fishTravelCountdown = MathHelper.nextInt(this.random, 20, 80);
            }
        } else {
            this.waitCountdown = MathHelper.nextInt(this.random, 1000, 6000);
            this.waitCountdown = this.waitCountdown - this.waitTimeReductionTicks;
        }
    }


    @ModifyArg(method = "use",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z",ordinal = 1))
    public Entity use(Entity par1) {
        PlayerEntity playerEntity = this.getPlayerOwner();
        return new ExperienceOrbEntity(playerEntity.getWorld(), playerEntity.getX(), playerEntity.getY() + 0.5, playerEntity.getZ() + 0.5, 0);
    }






}
