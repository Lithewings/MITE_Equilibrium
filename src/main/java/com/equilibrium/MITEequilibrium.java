package com.equilibrium;

import com.equilibrium.block.ModBlocks;

import com.equilibrium.craft_time_register.BlockInit;
import com.equilibrium.craft_time_register.UseBlock;
import com.equilibrium.entity.goal.BreakBlockGoal;
import com.equilibrium.event.BreakBlockEvent;
import com.equilibrium.event.CraftingMetalPickAxeCallback;
import com.equilibrium.event.CropIllnessEvent;
import com.equilibrium.event.MoonPhaseEvent;
import com.equilibrium.item.*;
import com.equilibrium.network.C2SClickTimesPacket;
import com.equilibrium.network.C2STriggerContentChangePacket;
import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import com.equilibrium.network.S2CStockChangeGrassColorPacket;
import com.equilibrium.persistent_state.StateSaverAndLoader;
import com.equilibrium.util.MapNbtSerializer;
import com.equilibrium.util.XpHashMap;
import com.equilibrium.util.OnServerInitializeMethod;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.GameVersion;
import net.minecraft.MinecraftVersion;
import net.minecraft.SaveVersion;
import net.minecraft.SharedConstants;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.equilibrium.craft_time_register.BlockEnityRegistry;

import com.equilibrium.util.CreativeGroup;
import com.equilibrium.craft_time_worklevel.CraftingIngredients;
import com.equilibrium.craft_time_worklevel.FurnaceIngredients;


import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.equilibrium.block.enchanting_table.ModBlockEntityTypes.modBlockEntityTypesInit;

import static com.equilibrium.block.enchanting_table.ModScreenTypes.registerScreenHandlers;
import static com.equilibrium.enchantments.EnchantmentsCodec.registerAllOfEnchantments;
import static com.equilibrium.entity.ModEntities.registerModEntities;


import static com.equilibrium.entity.mob.ModEntityTypes.modEntityTypeRegister;
import static com.equilibrium.entity.mob.ModSpawnRestriction.setModSpawnRestriction;
import static com.equilibrium.event.CropIllnessEvent.applyIllnessForCrop;
import static com.equilibrium.event.CropIllnessEvent.updateCropBlockPos;
import static com.equilibrium.event.MoonPhaseEvent.*;
import static com.equilibrium.event.MoonPhaseEvent.RandomTickModifier;
import static com.equilibrium.event.sound.SoundEventRegistry.registrySoundEvents;
import static com.equilibrium.item.Armors.registerArmors;
import static com.equilibrium.item.Metal.registerModItemRaw;
import static com.equilibrium.item.extend_item.CoinItems.registerCoinItems;
import static com.equilibrium.item.food.FoodItems.registerFoodItems;
import static com.equilibrium.item.food.ItemComponentModifier.foodComponentModify;
import static com.equilibrium.item.food.WaterBowl.vanillaBowlItemUse;
import static com.equilibrium.structure_generator.ModPlacementGenerator.*;
import static com.equilibrium.status.registerStatusEffect.registerStatusEffects;
import static com.equilibrium.structure_generator.StructureRegister.registerStructure;
import static com.equilibrium.tags.ModBlockTags.registerModBlockTags;
import static com.equilibrium.tags.ModEntityTags.registerModEntityTags;
import static com.equilibrium.tags.ModItemTags.registerModItemTags;


import static com.equilibrium.util.OnServerInitializeMethod.onUseCrystalItem;
import static com.equilibrium.util.OnServerInitializeMethod.onUseHayBlockItem;


public class MITEequilibrium implements ModInitializer {

    public static final String MOD_ID = "miteequilibrium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    //服务器状态
    public static StateSaverAndLoader serverState;

    public static final BooleanProperty FERTILIZED = BooleanProperty.of("fertilized");

    public static final IntProperty GRASSBLOCK_POLLUTED = IntProperty.of("grassblock_polluted",0,7);

    public static final BooleanProperty CROP_IS_ILLNESS = BooleanProperty.of("crop_illness");


    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void init() {
        // 任务在mod加载时初始化,初始化僵尸破坏的方块进度
        scheduler.scheduleAtFixedRate(() -> {
            synchronized (BreakBlockGoal.blockBreakProgressMap) {
                BreakBlockGoal.blockBreakProgressMap.clear();
                System.out.println("Progress map cleared.");
            }
        }, 240, 240, TimeUnit.SECONDS);  // 30秒后首次运行，以后每隔30秒执行一次
    }

    public static void initXpMap() {
        XpHashMap.setXpForLevel(1, 10);
        XpHashMap.setXpForLevel(2, 50);
        XpHashMap.setXpForLevel(3, 100);
        XpHashMap.setXpForLevel(4, 200);
        XpHashMap.setXpForLevel(5, 500);
    }

    public static void talkToAllServerPlayer(MinecraftServer server, String context) {
        for (ServerPlayerEntity serverPlayer : server.getPlayerManager().getPlayerList()) {
            serverPlayer.sendMessage(Text.of(context));
        }
    }

    // 注册命令的标准方式，适配 CommandDispatcher 的签名
    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                CommandManager.literal("village")
                        .executes
                                (OnServerInitializeMethod::isPickAxeCrafted)

        );
    }

    private static final int TICK_INTERVAL = 500; // 每隔500 tick检查一次
    private int tickCount = 0; // 记录当前 tick

//    SharedConstants.gameVersion = new GameVersion() {
//        @Override
//        public SaveVersion getSaveVersion() {
//            return new SaveVersion(108109);
//        }
//
//        @Override
//        public String getId() {
//            return "108109";
//        }
//
//        @Override
//        public String getName() {
//            return "MITE:Equilibrium Beta v1.0.8";
//        }
//
//        @Override
//        public int getProtocolVersion() {
//            return 108109;
//        }
//
//        @Override
//        public int getResourceVersion(ResourceType type) {
//            return 34;
//        }
//
//        @Override
//        public Date getBuildTime() {
//            return new Date();
//        }
//
//        @Override
//        public boolean isStable() {
//            return true;
//        }
//    };
    public void onInitialize() {
        SharedConstants.gameVersion = new GameVersion() {
            @Override
            public SaveVersion getSaveVersion() {
                return new SaveVersion(108109,"MITE:Equilibrium Beta");
            }

            @Override
            public String getId() {
                return "108109";
            }

            @Override
            public String getName() {
                return "MITE:Equilibrium Beta v1.0.8";
            }

            @Override
            public int getProtocolVersion() {
                return 108109;
            }

            @Override
            public int getResourceVersion(ResourceType type) {
                return 34;
            }

            @Override
            public Date getBuildTime() {
                return new Date();
            }

            @Override
            public boolean isStable() {
                return true;
            }
        };






        //原版物品修改
        DefaultItemComponentEvents.MODIFY.register(new VanillaItemModifier());




        ServerLifecycleEvents.SERVER_STARTED.register(server -> {

            //锁定游戏难度
            server.setDifficultyLocked(true);
            //读取服务器持久状态数据
            serverState = StateSaverAndLoader.getServerState(server);


            CropIllnessEvent.CROP_BLOCK_POS = MapNbtSerializer.fromNbt(
                    serverState.mapNbt2,
                    dis -> {
                        try {
                            return new BlockPos(dis.readInt(), dis.readInt(), dis.readInt());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    dis -> {
                        try {
                            return dis.readBoolean();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    ConcurrentHashMap::new);


            //之前的土地污染map被存在了nbt里,现在把它取出来
            //读取土地污染map
            S2CStockChangeGrassColorPacket.BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP = MapNbtSerializer.fromNbt(
                   serverState.mapNbt1,
                    dis -> {
                        try {
                            return new BlockPos(dis.readInt(), dis.readInt(), dis.readInt());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    dis -> {
                        try {
                            return dis.readInt();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    ConcurrentHashMap::new
            );






//			StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
//			int level = serverState.difficultyLevel;
//			//极限模式下不生效
//			switch (level){
//				case 0:
//					server.setDifficulty(Difficulty.EASY,true);
//					break;
//				case 1:
//					server.setDifficulty(Difficulty.NORMAL,true);
//					break;
//				case 2:
//					server.setDifficulty(Difficulty.HARD,true);
//					break;
//				default:
//					server.setDifficulty(Difficulty.NORMAL,true);
//					break;
//			}


        });



        // 注册服务器 tick 事件
        ServerTickEvents.START_SERVER_TICK.register(server -> {



            //更新服务器状态,在这里修改的所有数据都会被保存
            if (tickCount % (TICK_INTERVAL / 10) == 0) {
                //保存土地污染map,这个map被网络包定义的一个static的map共享,现在把它读取到nbt然后保存,不需要传参因为可以断定要传送的数据位置
                serverState.saveMapNbtToBuffer1();
                //保存生病农作物的map
                serverState.saveMapNbtToBuffer2();
            }


            // 每隔 TICK_INTERVAL 次 tick 触发一次检查
            tickCount++;
            //获取时间,得到月相,决定是否触发月相事件

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
                    if (serverOverWorld.getTimeOfDay() % 256 == 0) {
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
            //护甲更新,玩家游戏模式更新,作物状态更新
            if (tickCount % (TICK_INTERVAL / 10) == 0) {
                for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
                    OnServerInitializeMethod.updatePlayerArmor(serverPlayerEntity);
//					if(serverPlayerEntity.isCreative())
//						serverPlayerEntity.changeGameMode(GameMode.SURVIVAL);
                }
                updateCropBlockPos(serverOverWorld);
            }

            if (tickCount >= TICK_INTERVAL) {
                tickCount = 0; // 重置 tick 计数器
            }
        });
        //使用物品监听器,能不在这里写就不要在这里写,用物品自带的onUse方法

        UseItemCallback.EVENT.register((player, world, hand) -> {
            // 获取玩家手中的物品
            ItemStack itemStack = player.getStackInHand(hand);

            if(itemStack.isOf(Items.HAY_BLOCK)){
                if(player.experienceLevel>=75)
                    return onUseHayBlockItem(itemStack, player, world, 0);
            }

            // 判断是否为青金石等晶体
            if(player.experienceLevel<=35) {
                if (itemStack.getItem() == Items.REDSTONE) {
                    return onUseCrystalItem(itemStack, player, world, 10);
                }
                if (itemStack.getItem() == Items.LAPIS_LAZULI) {
                    return onUseCrystalItem(itemStack, player, world, 25);
                }
                if (itemStack.getItem() == Items.QUARTZ) {
                    return onUseCrystalItem(itemStack, player, world, 50);
                }
                if (itemStack.getItem() == Items.EMERALD) {
                    return onUseCrystalItem(itemStack, player, world, 250);
                }
                if (itemStack.getItem() == Items.DIAMOND) {
                    return onUseCrystalItem(itemStack, player, world, 500);
                }
            }
            if (itemStack.getItem() == Items.BOWL) {
                return vanillaBowlItemUse(world,player,hand,itemStack);
            }
            // 其他物品时不做处理
            return TypedActionResult.pass(itemStack);
        });


        //原版物品添加tooltip
        //不能和数据生成一起使用



//
//
//
//
//
//
//		});
        //结构注册
        registerStructure();
        //生物类型注册
        modEntityTypeRegister();
        //食物修改
        foodComponentModify();
        //生成限制
        setModSpawnRestriction();
        //xp映射表
        initXpMap();


        //网络服务
        C2SClickTimesPacket.register();
        C2STriggerContentChangePacket.register();
        //网络服务:客户端接收
        S2CIllnessTextureBooleanPacket.register();
        S2CStockChangeGrassColorPacket.register();


        //玩家食用食品监听器
//		OnPlayerEntityEatEvent.EVENT.register((player)->{
//			if(!player.getWorld().isClient()) {
//				player.sendMessage(Text.of("你吃掉了食物!"),true);
//			}
//			return ActionResult.SUCCESS;
//        });


        //合成金属镐监听器
        CraftingMetalPickAxeCallback.EVENT.register((world, player) -> {

//			DataFixer dataFixer = client.getDataFixer();  // 你需要初始化 DataFixer 实例
//			RegistryWrapper.WrapperLookup registryLookup = client.player.getRegistryManager();  // 同样初始化RegistryWrapper


            //创建持久状态类
            StateSaverAndLoader serverState;
            if (!world.isClient()) {
                serverState = StateSaverAndLoader.getServerState(world.getServer());
            } else {
                return ActionResult.PASS;
            }
            //直接访问成员变量即可
            boolean craftedIronPickaxe = serverState.isPickAxeCrafted;

            if (!craftedIronPickaxe) {
                if (!world.isClient()) {
                    serverState.isPickAxeCrafted = true;
                    player.sendMessage(Text.of("你第一次合成了金属镐"));
                } else
                    return ActionResult.PASS;
            } else {
                if (!world.isClient()) {
//					player.sendMessage(Text.of("你多次合成了铁镐"));
                    return ActionResult.PASS;
                } else
                    return ActionResult.PASS;
            }
            return ActionResult.PASS;
        });
        //命令注册
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        //附魔注册(记得把数据驱动部分也做好)
        registerAllOfEnchantments();
        //僵尸破坏方块进度列表
        init();
        //护甲添加
        registerArmors();
        //物品栏添加
        ModItemGroup.registerModItemGroup();
        //模组杂项物品添加
        ModItems.registerModItems();
        //方块添加测试
        ModBlocks.registerModBlocks();
        //以下开始正式添加物品:

        //食物
        registerFoodItems();
        //添加硬币物品
        registerCoinItems();
        //添加工具物品
        Tools.registerModItemTools();
        //添加锭
        Metal.registerModItemIngots();
        //添加金属颗粒
        Metal.registerModItemNuggets();
        //粗矿
        registerModItemRaw();
        //注册矿物
        registerModOre();
        //注册实体
        registerModEntities();
        //修改战利品表(已经弃用,用数据包代替)
//		modifierLootTables();
        //注册事件
        PlayerBlockBreakEvents.AFTER.register(new BreakBlockEvent());
        //创建标签
        registerModBlockTags();
        registerModItemTags();
        registerModEntityTags();
        //注册(药水)效果
        registerStatusEffects();
        registerScreenHandlers();

        BlockInit.registerBlocks();
        BlockInit.registerBlockItems();
        BlockInit.registerFuels();

        BlockEnityRegistry.init();
        CraftingIngredients.init();
        FurnaceIngredients.initFuel();
        FurnaceIngredients.initItem();

        CreativeGroup.addGroup();
        UseBlock.init();

        registrySoundEvents();
        modBlockEntityTypesInit();
        LOGGER.info("Hello Fabric world!");
    }


}