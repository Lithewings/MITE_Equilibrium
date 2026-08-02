package com.equilibrium.server_and_client.server.moonphase_tasks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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
    public static void applyStrengthToHostileMobs(ServerLevel world) {
        for (Player player : world.players())
            if (player != null) {
                // 玩家位置
                Vec3 playerPos = player.position();

                // 计算搜索区域：以玩家为中心的16个区块
                double range = 128; // ±128方块
                AABB searchBox = new AABB(playerPos.x - range, playerPos.y - range, playerPos.z - range,
                        playerPos.x + range, playerPos.y + range, playerPos.z + range);

                // 定义过滤条件：敌对生物
                EntityTypeTest<Entity, Monster> hostileFilter = EntityTypeTest.forClass(Monster.class);

                // 获取敌对生物列表
                List<Monster> hostileMobs = world.getEntities(hostileFilter, searchBox, Monster::isAlive);

                // 为每个敌对生物添加力量效果
                for (Monster mob : hostileMobs) {
                    mob.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(256);
                    // 创建力量效果实例，持续时间单位为tick（20 ticks = 1秒），这里设为300 ticks, 即15秒
                    MobEffectInstance strength = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300, 0); // 0 为效果等级，0 级即为 I 级
                    mob.addEffect(strength);
                }
            }
    }
    //新月束缚怪物
    public static void applyWeaknessToHostileMobs(ServerLevel world) {
        for (Player player : world.players())
            if (player != null) {
                // 玩家位置
                Vec3 playerPos = player.position();

                // 计算搜索区域：以玩家为中心的16个区块
                double range = 128; // ±128方块
                AABB searchBox = new AABB(playerPos.x - range, playerPos.y - range, playerPos.z - range,
                        playerPos.x + range, playerPos.y + range, playerPos.z + range);

                // 定义过滤条件：敌对生物
                EntityTypeTest<Entity, Monster> hostileFilter = EntityTypeTest.forClass(Monster.class);

                // 获取敌对生物列表
                List<Monster> hostileMobs = world.getEntities(hostileFilter, searchBox, Monster::isAlive);

                // 为每个敌对生物添加虚弱效果并削弱追踪距离
                for (Monster mob : hostileMobs) {
                    // 创建虚弱效果实例，持续时间单位为tick（20 ticks = 1秒），这里设为300 ticks, 即15秒
                    mob.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16);
                    MobEffectInstance weakness = new MobEffectInstance(MobEffects.WEAKNESS, 6000, 0); // 0 为效果等级，0 级即为 I 级
                    MobEffectInstance slowness = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6000, 1); // 0 为效果等级，0 级即为 I 级
                    mob.addEffect(weakness);
                    mob.addEffect(slowness);
                }
            }
    }

    public static void spawnMobNearPlayer(ServerLevel world) {
        for (Player player : world.players())
            if (player != null) {
                Random random = new Random();
                Vec3 playerPos = player.position();

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
                    while (world.isEmptyBlock(spawnPos) && spawnPos.getY() > 0) {
                        spawnPos = spawnPos.below();
                    }

                    // Ensure the spawn position is not in lava and the block above is air
                    if (world.getBlockState(spawnPos).getBlock() != Blocks.LAVA && !world.isEmptyBlock(spawnPos)) {
                        BlockPos spawnAbovePos = spawnPos.above();
                        BlockPos spawnAbovePos2 = spawnPos.above(2);

                        // Check if the position is valid for spawning a mob
                        if (world.isEmptyBlock(spawnAbovePos) && world.isEmptyBlock(spawnAbovePos2)) {
//                            MITEequilibrium.LOGGER.info("A Mob Respawned");
//                            player.sendMessage(Text.of("血月升起,怪物刷新一次"));
                            // ----------- 新增光照检查逻辑 -----------
                            // Minecraft中，世界光照范围是0~15，值越高表示越亮
                            // 如果光照值 >= 5，就跳过此位置
                            int lightLevel = world.getMaxLocalRawBrightness(spawnAbovePos);
                            if (lightLevel >= 5) {
//                                player.sendMessage(Text.of("光线抑制"));
//                                player.sendMessage(Text.of("lightLevel"+lightLevel));
                                continue;  // 光线过亮，不生成怪物

                            }
                            // ------------------------------------
                            EntityType<?> mobType = MOB_TYPES[random.nextInt(MOB_TYPES.length)];
                            Mob mob = (Mob) mobType.create(world);
                            MobEffectInstance statusEffectInstance = new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 2, false, true, false);
                            if (mob != null) {
                                mob.moveTo(spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
                                //为怪物施加力量效果,除了苦力怕
                                if (!(mob instanceof Creeper)) {
                                    mob.addEffect(statusEffectInstance);
                                }
                                // Ensure SkeletonEntity spawns with a bow
                                if (mob instanceof Skeleton) {
                                    Skeleton skeleton = (Skeleton) mob;
                                    skeleton.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
                                }

                                world.addFreshEntity(mob);
                                break;
                            }
                        }
                    }
                }
            }

    }

    public static void spawnLighteningNearPlayer(ServerLevel world, Player player) {
        Random random = new Random();
        BlockPos playerBlockPos = player.blockPosition();

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
            if (!state.isRedstoneConductor(world,search)) {
                //如果找到了第一个屋顶实体方块,则返回,就在这里的高度生成闪电
                //否则继续向上搜索
                y++;
                search=search.above();
            }
            else
                break;
        }
        //露天
        if(y==384)
            y=playerBlockPos.getY();

        //这里确定了闪电的水平高度

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world);
        //搜索该位置最近的一个避雷针位置
        Optional<BlockPos> availableLighteningRod = getLightningRodPos(world,spawnPos);

        if (lightning != null) {
            if(availableLighteningRod.isPresent())
                //避雷针处生成闪电,若没有,则在玩家附近生成
                lightning.moveTo(Vec3.atBottomCenterOf(availableLighteningRod.get()));
            else{
                lightning.moveTo(new Vec3(spawnPos.getX(),y,spawnPos.getZ()));
//                player.sendMessage(Text.of("一处闪电生成在了 "+"X :"+spawnPos.getX()+"Y :"+y+"Z :"+spawnPos.getZ()));
            }

            world.addFreshEntity(lightning);
        }

    }
    //从ServerWorld中拿到的方法
    private static Optional<BlockPos> getLightningRodPos(ServerLevel serverWorld , BlockPos pos) {
        Optional<BlockPos> optional = serverWorld.getPoiManager()
                .findClosest(
                        poiType -> poiType.is(PoiTypes.LIGHTNING_ROD),
                        innerPos -> innerPos.getY() == serverWorld.getHeight(Heightmap.Types.WORLD_SURFACE, innerPos.getX(), innerPos.getZ()) - 1,
                        pos,
                        128,
                        PoiManager.Occupancy.ANY
                );
        return optional.map(innerPos -> innerPos.above(1));
    }

    public static void spawnAnimalNearPlayer(ServerLevel world) {
        Player player = world.getRandomPlayer();
        if (player != null) {
            Random random = new Random();
            Vec3 playerPos = player.position();

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
                while (world.isEmptyBlock(spawnPos) && spawnPos.getY() > 0) {
                    spawnPos = spawnPos.below();
                }

                // Ensure the spawn position is not in lava
                if (world.getBlockState(spawnPos).getBlock() == Blocks.LAVA || world.getBlockState(spawnPos).getBlock() == Blocks.WATER) {
                    continue;
                }

                // Check if the position is valid for spawning an animal
                if (world.isEmptyBlock(spawnPos.above()) && world.isEmptyBlock(spawnPos.above(2))) {
                    EntityType<?> animalType = ANIMAL_TYPES[random.nextInt(ANIMAL_TYPES.length)];
                    Mob animal = (Mob) animalType.create(world,null,spawnPos, MobSpawnType.NATURAL,true,false);
                    if (animal != null) {
                        animal.moveTo(spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
                        world.addFreshEntity(animal);
                        break;
                    }
                }
            }
        }
    }
}
