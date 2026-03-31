package com.equilibrium;

import com.equilibrium.block.ModBlocksRegistry;

import com.equilibrium.block.ModBlocksRegistry2;
import com.equilibrium.entity.goal.BreakBlockGoal;
import com.equilibrium.server_and_client.server.event.BreakBlockEvent;
import com.equilibrium.server_and_client.server.event.CraftingMetalPickAxeCallback;
import com.equilibrium.server_and_client.server.CropIllnessEvent;
import com.equilibrium.server_and_client.server.EventOnServerInitOrRunning;
import com.equilibrium.item.*;
import com.equilibrium.network.*;
import com.equilibrium.server_and_client.server.command.ServerCommands;
import com.equilibrium.server_and_client.server.persistent_state.MapNbtSerializer;
import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.equilibrium.util.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.GameVersion;
import net.minecraft.SaveVersion;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.equilibrium.block.furnace_and_its_entity.FurnaceEntityRegistry;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import static com.equilibrium.DifficultyEntryOnGameRules.initGameRules;
import static com.equilibrium.GlobalModConfig.isSleepChunksAlwaysLoading;
import static com.equilibrium.block.reference.BlocksHardnessList.initModBlocksHardnessHashMap;
import static com.equilibrium.block.reference.BlocksHardnessList.initVanillaBlocksHardnessHashMap;
import static com.equilibrium.block.enchanting_table.ModBlockEntityTypes.modBlockEntityTypesInit;

import static com.equilibrium.block.enchanting_table.ModScreenTypes.registerScreenHandlers;


import static com.equilibrium.entity.ModEntities.*;
import static com.equilibrium.entity.ModSpawnRestriction.registerModSpawnRestriction;
import static com.equilibrium.server_and_client.server.CropIllnessEvent.applyIllnessForCrop;
import static com.equilibrium.server_and_client.server.CropIllnessEvent.updateCropBlockPos;
import static com.equilibrium.server_and_client.server.moonphase_tasks.MoonPhaseEvent.*;

import static com.equilibrium.server_and_client.server.event.SleepChunkLoaderEvents.registerSleepEvents;

import static com.equilibrium.server_and_client.server.SoundEventRegistry.registrySoundEvents;
import static com.equilibrium.item.Armors.registerArmors;
import static com.equilibrium.item.Metal.registerModItemRaw;
import static com.equilibrium.item.extend_item.CoinItems.registerCoinItems;
import static com.equilibrium.item.food.FoodOrFarmItems.registerFoodItems;
import static com.equilibrium.item.ItemComponentModifier.foodComponentModify;


import static com.equilibrium.structure.ModPlacementGenerator.*;
import static com.equilibrium.status.registerStatusEffect.registerStatusEffects;
import static com.equilibrium.structure.StructureRegister.registerStructure;
import static com.equilibrium.tags.ModBlockTags.registerModBlockTags;
import static com.equilibrium.tags.ModEntityTags.registerModEntityTags;
import static com.equilibrium.tags.ModItemTags.registerModItemTags;


import static com.equilibrium.block.CraftingDifficultyHelper.initCraftingDifficulties;


public class OnServerInitialize implements ModInitializer {

    public static final String MOD_ID = "miteequilibrium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    //服务器状态
    public StateSaverAndLoader serverState;

    public static final BooleanProperty FERTILIZED = BooleanProperty.of("fertilized");

    public static final IntProperty GRASSBLOCK_POLLUTED = IntProperty.of("grassblock_polluted", 0, 7);

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


    private static final int TICK_INTERVAL = 500; // 每隔500 tick检查一次
    private int tickCount = 0; // 记录当前 tick


    public void onInitialize() {


        SharedConstants.gameVersion = new GameVersion() {
            @Override
            public SaveVersion getSaveVersion() {
                return new SaveVersion(108109, "MITE:Equilibrium Beta");
            }

            @Override
            public String getId() {
                return "108109";
            }

            @Override
            public String getName() {
                return "MITE:Equilibrium Beta v1.1.0";
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
        //难度词条
        initGameRules();


        //原版物品修改
        DefaultItemComponentEvents.MODIFY.register(new VanillaItemModifier());


        ServerLifecycleEvents.SERVER_STARTED.register(server -> {

            //成就删除
            AdvancementRemover.removeAllMinecraftAdvancements(server.getAdvancementLoader().getManager());


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
        });


        // 注册服务器 tick 事件
        ServerTickEvents.START_SERVER_TICK.register(server -> {

            serverState = StateSaverAndLoader.getServerState(server);

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

            ServerWorld serverOverWorld = moonPhaseEvent(server);
            //护甲更新,玩家游戏模式更新,作物状态更新
            if (tickCount % (TICK_INTERVAL / 10) == 0) {
                for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
                    EventOnServerInitOrRunning.updatePlayerArmor(serverPlayerEntity);
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


        UseItemCallback.EVENT.register(EventOnServerInitOrRunning::onUseItem);


        //移除原版工作台方块,创造模式除外
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            Block block = world.getBlockState(hitResult.getBlockPos()).getBlock();
            if (!player.getWorld().isClient) {
                if (!player.isCreative()) {
                    if (block == Blocks.CRAFTING_TABLE) {
                        world.removeBlock(hitResult.getBlockPos(), true);
                    }
                }
            }
            return ActionResult.PASS;
        });


//        //结构注册
        registerStructure();


        //食物修改
        foodComponentModify();


        //生成限制
        registerModSpawnRestriction();

        //xp映射表
        initXpMap();


        //S->C,发包
        S2CStockChangeGrassColorPacket.registerOnServer();
        S2CIllnessTextureBooleanPacket.registerOnServer();
        S2CGameRuleSyncPayloadForBooleanPacket.registerOnServer();

        //C->S,发包、接收
        C2SClickTimesPacket.registerOnServer();
        C2STriggerContentChangePacket.registerOnServer();

        //合成金属镐监听器
        CraftingMetalPickAxeCallback.EVENT.register(EventOnServerInitOrRunning::onCraftingMetalPickAxe);
        //命令注册
        CommandRegistrationCallback.EVENT.register(ServerCommands::registerCommands);


        //僵尸破坏方块进度列表
        init();
        //护甲添加
        registerArmors();
        //物品栏添加
        ModItemGroup.registerModItemGroup();
        //模组杂项物品添加
        OtherItems.registerModItems();
        //方块添加测试
        ModBlocksRegistry.registerModBlocks();
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

        //注册事件
        PlayerBlockBreakEvents.AFTER.register(new BreakBlockEvent());
        if (isSleepChunksAlwaysLoading())
            registerSleepEvents();
        //创建标签
        registerModBlockTags();
        registerModItemTags();
        registerModEntityTags();
        //注册(药水)效果
        registerStatusEffects();
        registerScreenHandlers();

        ModBlocksRegistry2.registerBlocks();
        ModBlocksRegistry2.registerBlockItems();
        ModBlocksRegistry2.registerFuels();

        initVanillaBlocksHardnessHashMap();
        initModBlocksHardnessHashMap();
        initCraftingDifficulties();

        FurnaceEntityRegistry.init();

        registrySoundEvents();
        modBlockEntityTypesInit();


        LOGGER.info("Hello Fabric world!");
    }


}