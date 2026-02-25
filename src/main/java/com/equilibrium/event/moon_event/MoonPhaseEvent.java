package com.equilibrium.event.moon_event;

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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.*;

import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.NotNull;

import static com.equilibrium.event.CropIllnessEvent.applyIllnessForCrop;

public class MoonPhaseEvent {








    private static final EntityType<?>[] ANIMAL_TYPES = new EntityType[]{
            EntityType.PIG,
            EntityType.COW,
            EntityType.SHEEP,
            EntityType.CHICKEN
    };
    private static final EntityType<?>[] MOB_TYPES = new EntityType[]{
            EntityType.ZOMBIE,
            EntityType.CREEPER,
            EntityType.SPIDER,
            EntityType.SKELETON
    };
    private static String moonType;

    public static String getMoonType(World world) {
        moonType = WorldMoonPhasesSelector.calculateMoonType(world);
        if (moonType != null)
            return moonType;
        else {
            return "errorMoontype";
        }
    }

    @NotNull
    public static ServerWorld moonEvent(MinecraftServer server) {
        //月相事件
        String moonType = getMoonType(server.getWorld(World.OVERWORLD));
        ServerWorld serverOverWorld = server.getWorld(World.OVERWORLD);
        boolean isNoPlayersInTheOverWorld = serverOverWorld.getPlayers().isEmpty();
        Random random = new Random();


        if (isNoPlayersInTheOverWorld) {
            if (serverOverWorld.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED) != 3) {
//                    for (PlayerEntity player : server.getPlayerManager().getPlayerList())
//                        player.sendMessage(Text.of("由于主世界没有玩家,随机刻速度已回调至默认值"), true);
                //有可能目前是蓝月,但玩家在地底世界,所以会陷入这里恢复默认,但蓝月那边又改成5,这样反复执行了这段代码
                RandomTickModifier(serverOverWorld, 3);
            }

        }

        if (Objects.equals(moonType, "errorMoontype"))
            for (PlayerEntity player : server.getPlayerManager().getPlayerList())
                player.sendMessage(Text.of("月相加载失败"), true);
        else {
            //月相事件,只在主世界进行
            //增大随机刻的条件
            boolean shouldRandomTickIncrease = (moonType.equals("blueMoon") || (moonType.equals("harvestMoon")) || (moonType.equals("haloMoon")));
            if (!shouldRandomTickIncrease) {
                if (serverOverWorld.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED) != 3) {
//                        for (PlayerEntity player : server.getPlayerManager().getPlayerList())
//                            player.sendMessage(Text.of("由于处在普通月相,随机刻已回调至默认值"), true);
                    RandomTickModifier(serverOverWorld, 3);
                }
            }


            if (moonType.equals("bloodMoon")) {
                if (serverOverWorld.getTimeOfDay() % 100 == 0) {
                    //执行间隔事件
                    spawnMobNearPlayer(serverOverWorld);

                }
                if (serverOverWorld.getTimeOfDay() % random.nextInt(50, 64) == 0) {
                    //执行间隔事件
                    controlWeather(serverOverWorld);
//                        this.sendMessage(Text.of("雷电事件"));
                }
                if (serverOverWorld.getTimeOfDay() % 64 == 0) {
                    //施加作物疾病
                    applyIllnessForCrop(serverOverWorld);

                }


            }


            if (moonType.equals("harvestMoon") || (moonType.equals("haloMoon"))) {
                if (serverOverWorld.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED) != 4)
                    RandomTickModifier(serverOverWorld, 4);
//               if (this.age % 100 == 0) {
                //执行间隔事件
//               this.sendMessage(Text.of("黄月/幻月升起,触发事件"));
//               }
            }

            if (moonType.equals("fullMoon")) {
                if (serverOverWorld.getTimeOfDay() % 100 == 0) {
//              this.sendMessage(Text.of("满月升起,触发事件"));
                    applyStrengthToHostileMobs(serverOverWorld);
                }
            }

            if (moonType.equals("newMoon")) {
                if (serverOverWorld.getTimeOfDay() % 100 == 0) {
                    applyWeaknessToHostileMobs(serverOverWorld);
//              this.sendMessage(Text.of("新月升起,触发事件"));
                }
            }

            //第一次蓝月,不改变随机刻速度
            if (moonType.equals("blueMoon")) {
                if (serverOverWorld.getTimeOfDay() > 24000) {

                    if (serverOverWorld.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED) != 5)
                        RandomTickModifier(serverOverWorld, 5);
                    if (serverOverWorld.getTimeOfDay() % 1200 == 0) {

//								this.sendMessage(Text.of("蓝月升起,触发事件"));
                        //执行间隔事件
                        spawnAnimalNearPlayer(serverOverWorld);
                    }
                } else {
                    if (serverOverWorld.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED) != 3) {
//                            for (PlayerEntity player : server.getPlayerManager().getPlayerList())
//                                player.sendMessage(Text.of("由于第一天的蓝月并没有随机刻增益,随机刻应该修改为3"), true);
                        RandomTickModifier(serverOverWorld, 3);
                    }
                }
                //应该是用world.找到所有玩家,这里无非就是避免客户端世界直接转服务器世界造成崩溃
                //待改进:应该是this.getWorld,如果不是客户端世界再执行spawnAnimal方法

            }
        }
        return serverOverWorld;
    }







    
    
    
    
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
                    MobEntity animal = (MobEntity) animalType.create(world,null,spawnPos,SpawnReason.NATURAL,true,false);
                    if (animal != null) {
                        animal.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
                        world.spawnEntity(animal);
                        break;
                    }
                }
            }
        }
    }


    public static void RandomTickModifier(ServerWorld world, int randomTickSpeed) {
        world.getGameRules().get(GameRules.RANDOM_TICK_SPEED).set(randomTickSpeed,world.getServer());
    }


    public static void controlWeather(ServerWorld world) {
        PlayerEntity playerEntity = world.getRandomAlivePlayer();
        long timeOfDay = world.getTimeOfDay() % 24000; // 获取当前时间（一天有24000刻）

        if (timeOfDay >= 0 && timeOfDay < 14000) {
            startThunderstorm(world);
            if (playerEntity != null)
                if (playerEntity.getRandom().nextInt(4) == 0) {
//                    playerEntity.sendMessage(Text.of("雷声"));
                    playerEntity.getWorld().playSound(null, BlockPos.ofFloored(playerEntity.getPos()), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 1.0F, 1.0F);
                } else {
                    spawnLighteningNearPlayer(world, playerEntity);
//                    playerEntity.sendMessage(Text.of("雷电"));
                }


        } else { // 其他时间段
            clearWeather(world);
        }
    }

    public static void startThunderstorm(ServerWorld world) {
        ServerWorldProperties worldProperties = (ServerWorldProperties) world.getLevelProperties();
        worldProperties.setThundering(true);
        worldProperties.setRaining(true);
        worldProperties.setClearWeatherTime(0);
        worldProperties.setRainTime(6000);
        worldProperties.setThunderTime(6000);


    }

    public static void clearWeather(ServerWorld world) {
        ServerWorldProperties worldProperties = (ServerWorldProperties) world.getLevelProperties();
        worldProperties.setThundering(false); // 关闭雷雨
        worldProperties.setRaining(false); // 关闭降雨
        worldProperties.setClearWeatherTime(12000); // 设置晴天时间长度（可以根据需要调整）
    }


}
