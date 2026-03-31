package com.equilibrium.server_and_client.server.moonphase_tasks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.List;
import java.util.Optional;
import java.util.Random;





public class MoonPhaseEventEntitySpawner {
    public static final EntityType<?>[] ANIMAL_TYPES = new EntityType[]{
            EntityType.PIG,
            EntityType.COW,
            EntityType.SHEEP,
            EntityType.CHICKEN
    };
    public static final EntityType<?>[] MOB_TYPES = new EntityType[]{
            EntityType.ZOMBIE,
            EntityType.CREEPER,
            EntityType.SPIDER,
            EntityType.SKELETON
    };

    //满月加强怪物
    public static void applyStrengthToHostileMobs(ServerWorld world) {
        for (PlayerEntity player : world.getPlayers())
            if (player != null) {
                // 玩家位置
                Vec3d playerPos = player.getPos();

                // 计算搜索区域：以玩家为中心的16个区块
                double range = 128; // ±128方块
                Box searchBox = new Box(playerPos.x - range, playerPos.y - range, playerPos.z - range,
                        playerPos.x + range, playerPos.y + range, playerPos.z + range);

                // 定义过滤条件：敌对生物
                TypeFilter<Entity, HostileEntity> hostileFilter = TypeFilter.instanceOf(HostileEntity.class);

                // 获取敌对生物列表
                List<HostileEntity> hostileMobs = world.getEntitiesByType(hostileFilter, searchBox, HostileEntity::isAlive);

                // 为每个敌对生物添加力量效果
                for (HostileEntity mob : hostileMobs) {
                    mob.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(256);
                    // 创建力量效果实例，持续时间单位为tick（20 ticks = 1秒），这里设为300 ticks, 即15秒
                    StatusEffectInstance strength = new StatusEffectInstance(StatusEffects.STRENGTH, 300, 0); // 0 为效果等级，0 级即为 I 级
                    mob.addStatusEffect(strength);
                }
            }
    }
    //新月束缚怪物
    public static void applyWeaknessToHostileMobs(ServerWorld world) {
        for (PlayerEntity player : world.getPlayers())
            if (player != null) {
                // 玩家位置
                Vec3d playerPos = player.getPos();

                // 计算搜索区域：以玩家为中心的16个区块
                double range = 128; // ±128方块
                Box searchBox = new Box(playerPos.x - range, playerPos.y - range, playerPos.z - range,
                        playerPos.x + range, playerPos.y + range, playerPos.z + range);

                // 定义过滤条件：敌对生物
                TypeFilter<Entity, HostileEntity> hostileFilter = TypeFilter.instanceOf(HostileEntity.class);

                // 获取敌对生物列表
                List<HostileEntity> hostileMobs = world.getEntitiesByType(hostileFilter, searchBox, HostileEntity::isAlive);

                // 为每个敌对生物添加虚弱效果并削弱追踪距离
                for (HostileEntity mob : hostileMobs) {
                    // 创建虚弱效果实例，持续时间单位为tick（20 ticks = 1秒），这里设为300 ticks, 即15秒
                    mob.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(16);
                    StatusEffectInstance weakness = new StatusEffectInstance(StatusEffects.WEAKNESS, 6000, 0); // 0 为效果等级，0 级即为 I 级
                    StatusEffectInstance slowness = new StatusEffectInstance(StatusEffects.SLOWNESS, 6000, 1); // 0 为效果等级，0 级即为 I 级
                    mob.addStatusEffect(weakness);
                    mob.addStatusEffect(slowness);
                }
            }
    }

    public static void spawnMobNearPlayer(ServerWorld world) {
        for (PlayerEntity player : world.getPlayers())
            if (player != null) {
                Random random = new Random();
                Vec3d playerPos = player.getPos();

                for (int i = 0; i < 10; i++) { // Try up to 10 times to find a valid position
                    double offsetX = (random.nextDouble() - 0.5) * 40.0; // Offset range extended to 40
                    double offsetZ = (random.nextDouble() - 0.5) * 40.0; // Offset range extended to 40
                    if (Math.abs(offsetX) < 16) {
                        offsetX += 16 * Math.signum(offsetX); // Ensure minimum offset of 16 blocks
                    }
                    if (Math.abs(offsetZ) < 16) {
                        offsetZ += 16 * Math.signum(offsetZ); // Ensure minimum offset of 16 blocks
                    }
                    BlockPos spawnPos = new BlockPos((int) (playerPos.x + offsetX), (int) playerPos.y, (int) (playerPos.z + offsetZ));

                    // Find the highest non-air block at the spawn position
                    while (world.isAir(spawnPos) && spawnPos.getY() > 0) {
                        spawnPos = spawnPos.down();
                    }

                    // Ensure the spawn position is not in lava and the block above is air
                    if (world.getBlockState(spawnPos).getBlock() != Blocks.LAVA && !world.isAir(spawnPos)) {
                        BlockPos spawnAbovePos = spawnPos.up();
                        BlockPos spawnAbovePos2 = spawnPos.up(2);

                        // Check if the position is valid for spawning a mob
                        if (world.isAir(spawnAbovePos) && world.isAir(spawnAbovePos2)) {
//                            MITEequilibrium.LOGGER.info("A Mob Respawned");
//                            player.sendMessage(Text.of("血月升起,怪物刷新一次"));
                            // ----------- 新增光照检查逻辑 -----------
                            // Minecraft中，世界光照范围是0~15，值越高表示越亮
                            // 如果光照值 >= 5，就跳过此位置
                            int lightLevel = world.getLightLevel(spawnAbovePos);
                            if (lightLevel >= 5) {
//                                player.sendMessage(Text.of("光线抑制"));
//                                player.sendMessage(Text.of("lightLevel"+lightLevel));
                                continue;  // 光线过亮，不生成怪物

                            }
                            // ------------------------------------
                            EntityType<?> mobType = MOB_TYPES[random.nextInt(MOB_TYPES.length)];
                            MobEntity mob = (MobEntity) mobType.create(world);
                            StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.STRENGTH, -1, 2, false, true, false);
                            if (mob != null) {
                                mob.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
                                //为怪物施加力量效果,除了苦力怕
                                if (!(mob instanceof CreeperEntity)) {
                                    mob.addStatusEffect(statusEffectInstance);
                                }
                                // Ensure SkeletonEntity spawns with a bow
                                if (mob instanceof SkeletonEntity) {
                                    SkeletonEntity skeleton = (SkeletonEntity) mob;
                                    skeleton.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
                                }

                                world.spawnEntity(mob);
                                break;
                            }
                        }
                    }
                }
            }

    }

    public static void spawnLighteningNearPlayer(ServerWorld world, PlayerEntity player) {
        Random random = new Random();
        BlockPos playerBlockPos = player.getBlockPos();

        int offsetX = (int) ((random.nextDouble() - 0.5) * 40.0); // Offset range extended to 40
        int offsetZ = (int) ((random.nextDouble() - 0.5) * 40.0); // Offset range extended to 40

        //预先就在玩家高度生成
        BlockPos spawnPos = new BlockPos(playerBlockPos.getX() + offsetX, playerBlockPos.getY(), playerBlockPos.getZ()+offsetZ);


        //以下获取玩家头顶方块

        //玩家头顶的高度
        int y = playerBlockPos.getY();


        BlockPos search = playerBlockPos;


        while (y < 384) {
            BlockState state =world.getBlockState(search);
            //检查玩家头顶方块
            if (!state.isSolidBlock(world,search)) {
                //如果找到了第一个屋顶实体方块,则返回,就在这里的高度生成闪电
                //否则继续向上搜索
                y++;
                search=search.up();
            }
            else
                break;
        }
        //露天
        if(y==384)
            y=playerBlockPos.getY();

        //这里确定了闪电的水平高度

        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
        //搜索该位置最近的一个避雷针位置
        Optional<BlockPos> availableLighteningRod = getLightningRodPos(world,spawnPos);

        if (lightning != null) {
            if(availableLighteningRod.isPresent())
                //避雷针处生成闪电,若没有,则在玩家附近生成
                lightning.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(availableLighteningRod.get()));
            else{
                lightning.refreshPositionAfterTeleport(new Vec3d(spawnPos.getX(),y,spawnPos.getZ()));
//                player.sendMessage(Text.of("一处闪电生成在了 "+"X :"+spawnPos.getX()+"Y :"+y+"Z :"+spawnPos.getZ()));
            }

            world.spawnEntity(lightning);
        }

    }
    //从ServerWorld中拿到的方法
    private static Optional<BlockPos> getLightningRodPos(ServerWorld serverWorld , BlockPos pos) {
        Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage()
                .getNearestPosition(
                        poiType -> poiType.matchesKey(PointOfInterestTypes.LIGHTNING_ROD),
                        innerPos -> innerPos.getY() == serverWorld.getTopY(Heightmap.Type.WORLD_SURFACE, innerPos.getX(), innerPos.getZ()) - 1,
                        pos,
                        128,
                        PointOfInterestStorage.OccupationStatus.ANY
                );
        return optional.map(innerPos -> innerPos.up(1));
    }

    public static void spawnAnimalNearPlayer(ServerWorld world) {
        PlayerEntity player = world.getRandomAlivePlayer();
        if (player != null) {
            Random random = new Random();
            Vec3d playerPos = player.getPos();

            for (int i = 0; i < 10; i++) { // Try up to 10 times to find a valid position
                double offsetX = (random.nextDouble() - 0.5) * 64.0; // Offset range extended to 64
                double offsetZ = (random.nextDouble() - 0.5) * 64.0; // Offset range extended to 64
                if (Math.abs(offsetX) < 32) {
                    offsetX += 32 * Math.signum(offsetX); // Ensure minimum offset of 32 blocks
                }
                if (Math.abs(offsetZ) < 32) {
                    offsetZ += 32 * Math.signum(offsetZ); // Ensure minimum offset of 32 blocks
                }
                BlockPos spawnPos = new BlockPos((int) (playerPos.x + offsetX), (int) playerPos.y, (int) (playerPos.z + offsetZ));

                // Find the highest non-air block at the spawn position
                while (world.isAir(spawnPos) && spawnPos.getY() > 0) {
                    spawnPos = spawnPos.down();
                }

                // Ensure the spawn position is not in lava
                if (world.getBlockState(spawnPos).getBlock() == Blocks.LAVA || world.getBlockState(spawnPos).getBlock() == Blocks.WATER) {
                    continue;
                }

                // Check if the position is valid for spawning an animal
                if (world.isAir(spawnPos.up()) && world.isAir(spawnPos.up(2))) {
                    EntityType<?> animalType = ANIMAL_TYPES[random.nextInt(ANIMAL_TYPES.length)];
                    MobEntity animal = (MobEntity) animalType.create(world,null,spawnPos, SpawnReason.NATURAL,true,false);
                    if (animal != null) {
                        animal.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
                        world.spawnEntity(animal);
                        break;
                    }
                }
            }
        }
    }
}
